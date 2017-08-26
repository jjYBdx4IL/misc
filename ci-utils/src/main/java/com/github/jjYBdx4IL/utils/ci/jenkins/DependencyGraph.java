/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.ci.jenkins;

//CHECKSTYLE:OFF
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jjYBdx4IL
 */
public class DependencyGraph {

    public static final String URL_SUFFIX = "/depgraph-view/graph.json";

    private final URL jenkinsInstanceURL;
    private final List<Edge> edges = new ArrayList<>();
    private final List<JobNode> jobs = new ArrayList<>();

    public DependencyGraph(URL jenkinsInstanceURL) {
        this.jenkinsInstanceURL = jenkinsInstanceURL;
    }

    protected List<JobNode> getParentJobNames() {
        List<JobNode> parentJobNames = new ArrayList<>();

        Set<String> hasParents = new HashSet<>();
        for (Edge edge : edges) {
            hasParents.add(edge.to);
        }
        for (JobNode job : jobs) {
            if (!hasParents.contains(job.name)) {
                parentJobNames.add(job);
            }
        }

        return parentJobNames;
    }

    protected void load() throws IOException {
        edges.clear();
        jobs.clear();
        try (InputStream is = new URL(jenkinsInstanceURL.toExternalForm() + URL_SUFFIX).openStream()) {
            parseStream(is);
        }
    }
    public List<String> queue(String[] prepend, String[] append, boolean dryRun) throws IOException {
        load();
        List<String> queuedBuildJobs = new ArrayList<>();
        if (prepend != null) {
            for (String job : prepend) {
                queueJob(job, dryRun);
                queuedBuildJobs.add(job);
            }
        }
        for (JobNode job : getParentJobNames()) {
            queueJob(job.name, dryRun);
            queuedBuildJobs.add(job.name);
        }
        if (append != null) {
            for (String job : append) {
                queueJob(job, dryRun);
                queuedBuildJobs.add(job);
            }
        }
        
        return queuedBuildJobs;
    }

    @SuppressWarnings("deprecation")
    private void queueJob(String job, boolean dryRun) throws IOException {
        if (dryRun) {
            return;
        }
        IOUtils.toString(new URL(String.format("%s/job/%s/build", jenkinsInstanceURL.toExternalForm(), job)));
    }


    protected void parseStream(InputStream is) throws IOException {
        final Gson g = new Gson();
        final JsonElement root;
        try (InputStreamReader isr = new InputStreamReader(is)) {
            try (JsonReader reader = new JsonReader(isr)) {
                JsonParser parser = new JsonParser();
                root = parser.parse(reader);
            }
        }

        JsonArray arr = root.getAsJsonObject().get("edges").getAsJsonArray();
        Iterator<JsonElement> it = arr.iterator();
        while (it.hasNext()) {
            JsonElement el = it.next();
            Edge edge = g.fromJson(el.toString(), Edge.class);
            if (edge.type.equals("dep")) {
                edges.add(edge);
            }
        }

        arr = root.getAsJsonObject().get("clusters").getAsJsonArray();
        it = arr.iterator();
        while (it.hasNext()) {
            JsonArray nodes = it.next().getAsJsonObject().get("nodes").getAsJsonArray();
            Iterator<JsonElement> nodeIt = nodes.iterator();
            while (nodeIt.hasNext()) {
                JsonElement el = nodeIt.next();
                JobNode job = g.fromJson(el.toString(), JobNode.class);
                jobs.add(job);
            }
        }
    }

    class JobNode {
        private String name;
        private String fullName;
        private String url;
        private int x;
        private int y;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("JobNode [");
            builder.append("fullName=");
            builder.append(fullName);
            builder.append(", name=");
            builder.append(name);
            builder.append(", url=");
            builder.append(url);
            builder.append(", x=");
            builder.append(x);
            builder.append(", y=");
            builder.append(y);
            builder.append("]");
            return builder.toString();
        }
    }

    class Edge {
        private String from;
        private String to;
        private String type;

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Edge [");
            builder.append("from=");
            builder.append(from);
            builder.append(", to=");
            builder.append(to);
            builder.append(", type=");
            builder.append(type);
            builder.append("]");
            return builder.toString();
        }
    }

}
