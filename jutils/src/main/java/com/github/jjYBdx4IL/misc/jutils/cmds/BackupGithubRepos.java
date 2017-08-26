/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.misc.jutils.cmds;

import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandAnnotation;
import com.github.jjYBdx4IL.misc.jutils.JUtilsCommandInterface;
import com.github.jjYBdx4IL.utils.proc.ProcRunner;
import com.github.jjYBdx4IL.utils.cfg.AtomicPropsFile;
import com.github.jjYBdx4IL.utils.cfg.AtomicPropsFileSimpleGui;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jjYBdx4IL (https://github.com/jjYBdx4IL)
 */
@JUtilsCommandAnnotation(
        name = "bgr",
        help = "backup all github repositories of the configured user (not tested with private repos, paid accounts, organization accounts etc)",
        usage = "",
        minArgs = 0,
        maxArgs = 0
)
public class BackupGithubRepos implements JUtilsCommandInterface {

    private static final String OPTNAME_CONFIG = "c";
    private static final String OPTNAME_DESTDIR = "d";
    private static final String OPTNAME_EMBEDDED = "e";
    private static final String OPTNAME_VERBOSE = "v";
    private static final String OPTNAME_DUMPONLY = "u";
    private static final String OPTNAME_EXCLUDE_FORKS = "k";
    private static final String OPTNAME_NOMIRROR = "n";
    private static final String OPTNAME_SSHURL = "s";
    private static final String CFGKEY_GITHUB_USER = "github user";
    private static final String CFGKEY_GITHUB_OAUTH_TOKEN = "github oauth token";

    private boolean verbose = false;
    private boolean embedded = false;
    private boolean excludeForks = false;
    private boolean noMirror = false;
    private boolean useSshUrl = false;

    @Override
    public int run(CommandLine line) {
        try {
            verbose = line.hasOption(OPTNAME_VERBOSE);
            embedded = line.hasOption(OPTNAME_EMBEDDED);
            excludeForks = line.hasOption(OPTNAME_EXCLUDE_FORKS);
            noMirror = line.hasOption(OPTNAME_NOMIRROR);
            useSshUrl = line.hasOption(OPTNAME_SSHURL);

            if (embedded && noMirror) {
            	throw new RuntimeException("--no-mirror not supported with --embedded enabled");
            }
            
            if (line.hasOption(OPTNAME_DEVTESTS)) {
                runDevTests();
                return 0;
            }

            if (line.hasOption(OPTNAME_CONFIG)) {
                configure();
                return 0;
            }

            Map<String, String> repoUrls = getRepoUrls();

            if (line.hasOption(OPTNAME_DUMPONLY)) {
                for (String repoName : repoUrls.keySet()) {
                    System.out.println(repoUrls.get(repoName));
                }
                return 0;
            }

            if (!line.hasOption(OPTNAME_DESTDIR)) {
                throw new RuntimeException("need destination dir");
            }

            File destDir = new File(line.getOptionValue(OPTNAME_DESTDIR));
            if (!destDir.isAbsolute()) {
                throw new RuntimeException("destination dir is not absolute");
            }
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            if (!destDir.exists()) {
                throw new RuntimeException("failed to create " + destDir);
            }

            int failures = 0;
            for (String repoName : repoUrls.keySet()) {
                if (!backup(repoName, repoUrls.get(repoName), destDir)) {
                    failures++;
                }
            }
            if (failures != 0) {
                throw new RuntimeException(failures + " backups failed");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    @SuppressWarnings("unused")
    private void runDevTests() {
        // check linkage
        GitHubClient client = new GitHubClient();
        CloneCommand cloneCommand = Git.cloneRepository();
    }

    private boolean backup(String repoName, String repoUrl, File destDir) {
        File mirrorDir = new File(destDir, repoName);
        System.out.println("backing up " + repoName + " - " + repoUrl + " - to " + mirrorDir.getAbsolutePath());
        if (!mirrorDir.exists()) {
            if (embedded) {
                return cloneUsingEmbeddedJgit(mirrorDir, repoUrl);
            } else {
                return cloneUsingCmdlineGit(mirrorDir, repoUrl);
            }
        } else {
            if (embedded) {
                return fetchUsingEmbeddedJgit(mirrorDir, repoUrl);
            } else {
                return fetchUsingCmdlineGit(mirrorDir, repoUrl);
            }
        }
    }

    private boolean fetchUsingEmbeddedJgit(File mirrorDir, String repoUrl) {
        try {
            org.eclipse.jgit.lib.Repository cloneRepo = new FileRepositoryBuilder().setGitDir(mirrorDir).build();
            try (Git git = new Git(cloneRepo)) {
                FetchCommand cmd = git.fetch();
                try {
                    cmd.call();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean fetchUsingCmdlineGit(File mirrorDir, String repoUrl) {
    	if (noMirror) {
    		return runCmdlineGit(mirrorDir, "pull", "--all");
    	}
        return runCmdlineGit(mirrorDir, "fetch", "--all", "--tags");
    }

    private boolean cloneUsingCmdlineGit(File mirrorDir, String repoUrl) {
    	if (noMirror) {
    		return runCmdlineGit(mirrorDir.getParentFile(), "clone", repoUrl, mirrorDir.getName());
    	}
        return runCmdlineGit(mirrorDir.getParentFile(), "clone", "--mirror", repoUrl, mirrorDir.getName());
    }

    private boolean runCmdlineGit(File workDir, String... gitCmdArgs) {
        List<String> args = new ArrayList<>();
        args.add("git");
        for (String s : gitCmdArgs) {
            args.add(s);
        }
        if (verbose) {
            System.out.println("running external command: " + StringUtils.join(args, " "));
        }
        ProcRunner pr = new ProcRunner(true, args);
        pr.setWorkDir(workDir);
        int exitCode = 0;
        try {
            exitCode = pr.run();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (verbose) {
            System.out.println("exit code: " + exitCode);
        }
        if (exitCode != 0) {
            System.err.println(pr.getOutputBlob());
        }
        return exitCode == 0;
    }

    private boolean cloneUsingEmbeddedJgit(File mirrorDir, String repoUrl) {
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(repoUrl);
        cloneCommand.setDirectory(mirrorDir);
        cloneCommand.setBare(true);
        cloneCommand.setCloneAllBranches(true);
        try (Git git = cloneCommand.call()) {
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void configure() throws IOException {
        AtomicPropsFileSimpleGui gui = new AtomicPropsFileSimpleGui(BackupGithubRepos.class, CFGKEY_GITHUB_USER,
                CFGKEY_GITHUB_OAUTH_TOKEN);
        try {
            gui.loadOrShow(true);
            System.out.println("config file saved.");
        } catch (IOException ex) {
            String path = gui.saveInvalid();
            System.err.println("configuration dialog failed, please edit the config file by hand: " + path);
        }
    }

    private Map<String, String> getRepoUrls() throws IOException {
        AtomicPropsFile cfg = new AtomicPropsFile(BackupGithubRepos.class);
        cfg.load();

        GitHubClient client = new GitHubClient();
        client.setOAuth2Token(cfg.get(CFGKEY_GITHUB_OAUTH_TOKEN));

        RepositoryService service = new RepositoryService();
        Map<String, String> repos = new HashMap<>();
        for (Repository repo : service.getRepositories(cfg.get(CFGKEY_GITHUB_USER))) {
        	if (excludeForks && repo.isFork()) {
        		if (verbose) {
        			System.out.println("skipping forked repository " + repo.getName());
        		}
        		continue;
        	}
            Object prev = repos.put(repo.getName(), useSshUrl ? repo.getSshUrl() : repo.getCloneUrl());
            if (prev != null) {
                throw new IOException("duplice repo name returned");
            }
        }

        if (repos.isEmpty()) {
            throw new IOException("no repositories found");
        }

        return repos;
    }

    @Override
    public Options getCommandLineOptions() {
        Options options = new Options();
        options.addOption(OPTNAME_CONFIG, "config", false, "configure the github account and oauth token to use");
        options.addOption(OPTNAME_DESTDIR, "dest", true, "absolute destination directory for the backups (required)");
        options.addOption(OPTNAME_EMBEDDED, "embedded", false, "run embedded git (jgit), not recommended");
        options.addOption(OPTNAME_VERBOSE, "verbose", false, "be more verbose");
        options.addOption(OPTNAME_DUMPONLY, "dumponly", false, "dump only the list of repositories, do nothing else");
        options.addOption(OPTNAME_EXCLUDE_FORKS, "exclude-forks", false, "exclude forked repositories");
        options.addOption(OPTNAME_NOMIRROR, "no-mirror", false, "use regular clone/pull instead of --mirror/fetch");
        options.addOption(OPTNAME_SSHURL, "use-ssh-url", false, "use ssh url instead of http url");
        options.addOption(null, OPTNAME_DEVTESTS, false, "ignore this");
        return options;
    }

}
