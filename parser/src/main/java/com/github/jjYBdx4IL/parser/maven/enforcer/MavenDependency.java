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
package com.github.jjYBdx4IL.parser.maven.enforcer;

public class MavenDependency implements Comparable<MavenDependency> {

    private String groupId;
    private String artifactId;
    private String version;

    /**
     * Shallow class defining a maven dependency.
     * 
     * @param groupId
     *            the group id
     * @param artifactId
     *            the artifact id
     * @param version
     *            the version
     */
    public MavenDependency(String groupId, String artifactId, String version) {
        if (groupId == null || groupId.isEmpty()) {
            throw new IllegalArgumentException("groupId must neither be null nor empty");
        }
        if (artifactId == null || artifactId.isEmpty()) {
            throw new IllegalArgumentException("artifactId must neither be null nor empty");
        }
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("version must neither be null nor empty");
        }
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MavenDependency other = (MavenDependency) obj;
        if (artifactId == null) {
            if (other.artifactId != null) {
                return false;
            }
        } else if (!artifactId.equals(other.artifactId)) {
            return false;
        }
        if (groupId == null) {
            if (other.groupId != null) {
                return false;
            }
        } else if (!groupId.equals(other.groupId)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(MavenDependency other) {
        if (!groupId.equalsIgnoreCase(other.groupId)) {
            return groupId.compareToIgnoreCase(other.groupId);
        }
        if (!artifactId.equalsIgnoreCase(other.artifactId)) {
            return artifactId.compareToIgnoreCase(other.artifactId);
        }
        if (!version.equalsIgnoreCase(other.version)) {
            return VersionComparator.largerThan(version, other.version) ? 1 : -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MavenDependency [groupId=");
        builder.append(groupId);
        builder.append(", artifactId=");
        builder.append(artifactId);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
