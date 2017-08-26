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
package com.github.jjYBdx4IL.utils.cache;

//CHECKSTYLE:OFF
import java.io.Serializable;
import java.net.URL;

/**
 *
 * @author jjYBdx4IL
 */
public class SimpleDiskCacheEntryHeader implements Serializable {
    private static final long serialVersionUID = 1L;
    private long creationTime;
    private String url;

    public SimpleDiskCacheEntryHeader(URL url) {
        this.creationTime = System.currentTimeMillis();
        this.url = url.toExternalForm();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getUrl() {
        return url;
    }

    public long getAgeSeconds() {
        return (System.currentTimeMillis() - creationTime) / 1000L;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleDiskCacheEntryHeader [");
        builder.append("creationTime=");
        builder.append(creationTime);
        builder.append(", serialVersionUID=");
        builder.append(serialVersionUID);
        builder.append(", url=");
        builder.append(url);
        builder.append("]");
        return builder.toString();
    }
}
