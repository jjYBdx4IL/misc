/*
 * Copyright © 2021 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.svncw;

import java.util.Map;

public class SvnInfoResult {

    // $ svn info
    // Path: .
    // Working Copy Root Path: /cygdrive/e/co
    // URL: svn+ssh://svn.host.de/devel/java/wildfly-config
    // Relative URL: ^/devel/java/wildfly-config
    // Repository Root: svn+ssh://svn.host.de
    // Repository UUID: a6e4e4fc-3e15-0430-22a9-cce411272424
    // Revision: 873
    // Node Kind: directory
    // Schedule: normal
    // Last Changed Author: joe
    // Last Changed Rev: 873
    // Last Changed Date: 2021-06-07 19:00:47 +0200 (Mon, 07 Jun 2021)

    public final String path;
    public final String workingCopyRootPath;
    public final String url;
    public final String relativeUrl;
    public final String repositoryRoot;
    public final String repositoryUuid;
    public final Long revision;
    public final String nodeKind;
    public final String schedule;
    public final String lastChangedAuthor;
    public final Long lastChangedRev;
    public final String lastChangedDate;
    
    SvnInfoResult(Map<String, String> m) {
        path = m.get("Path");
        workingCopyRootPath = m.get("Working Copy Root Path");
        url = m.get("URL");
        relativeUrl = m.get("Relative URL");
        repositoryRoot = m.get("Repository Root");
        repositoryUuid = m.get("Repository UUID");
        revision = Long.parseLong(m.get("Revision"));
        nodeKind = m.get("Node Kind");
        schedule = m.get("Schedule");
        lastChangedAuthor = m.get("Last Changed Author");
        lastChangedRev = Long.parseLong(m.get("Last Changed Rev"));
        lastChangedDate = m.get("Last Changed Date");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SvnInfoResult [path=").append(path).append(", workingCopyRootPath=").append(workingCopyRootPath)
            .append(", url=").append(url).append(", relativeUrl=").append(relativeUrl).append(", repositoryRoot=")
            .append(repositoryRoot).append(", repositoryUuid=").append(repositoryUuid).append(", revision=")
            .append(revision).append(", nodeKind=").append(nodeKind).append(", schedule=").append(schedule)
            .append(", lastChangedAuthor=").append(lastChangedAuthor).append(", lastChangedRev=").append(lastChangedRev)
            .append(", lastChangedDate=").append(lastChangedDate).append("]");
        return builder.toString();
    }
}
