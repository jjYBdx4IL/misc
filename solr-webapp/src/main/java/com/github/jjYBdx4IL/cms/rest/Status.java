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
package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.solr.IndexingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Date;

import javax.annotation.security.PermitAll;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

//CHECKSTYLE:OFF
@Path("status")
@PermitAll
public class Status {

    private static final Logger LOG = LoggerFactory.getLogger(Status.class);

    public static final String[] MNTPTS = new String[] { "/", "/data" };
    public static final long minFreeMB = 1024;

    // use the following nagios checks to monitor crawler and disk space:
    //
    // we can't merge the cert check with the rest it seems:
    // ./check_http -w 5 -c 10 --ssl -H host -u /status -j HEAD --max-age=10m
    // ./check_http -w 5 -c 10 --ssl -H host -C 14,7
    //
    // ./check_http -w 5 -c 10 -H localhost -p 8080 -u /status -j HEAD
    // --max-age=10m
    @HEAD
    public Response statusPage() throws IOException {

        boolean diskSpaceOK = true;
        for (String mntpt : MNTPTS) {
            java.nio.file.Path root = FileSystems.getDefault().getPath(mntpt);
            FileStore store = Files.getFileStore(root);
            long freeMB = store.getUsableSpace() / 1048576L;
            if (freeMB < minFreeMB) {
                diskSpaceOK = false;
                LOG.error(String.format("free disk space on %s: %.3f GB", mntpt, freeMB / 1024f));
            }
        }

        return Response.status(diskSpaceOK ? 200 : 501)
            .lastModified(new Date(IndexingTask.ping.get()))
            .header("X-Monitoring-Status-DiskSpace", diskSpaceOK ? "OK" : "CRITICAL")
            .build();
    }

}
