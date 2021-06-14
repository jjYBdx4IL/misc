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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class SvnUrl {

    public final String rootUrl;
    public final String relPath;
    
    /**
     * Constructor.
     */
    public SvnUrl(String rootUrl, String relPath) {
        checkNotNull(rootUrl);
        checkArgument(!rootUrl.isEmpty());
        checkArgument(!rootUrl.endsWith("/"));
        checkArgument(relPath == null || !relPath.isEmpty());
        checkArgument(relPath == null || !relPath.startsWith("/"));
        this.rootUrl = rootUrl;
        this.relPath = relPath;
    }

    /**
     * To string method.
     */
    public String toExternalForm() {
        if (relPath == null) {
            return rootUrl;
        }
        return rootUrl + "/" + relPath;
    }
}
