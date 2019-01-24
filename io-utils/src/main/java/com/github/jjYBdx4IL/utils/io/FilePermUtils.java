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
package com.github.jjYBdx4IL.utils.io;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Collections;
import java.util.EnumSet;

public class FilePermUtils {
    
    public static final String POSIX_PERMS_OWNER_ONLY_DIR = "rwx------";
    public static final String POSIX_PERMS_OWNER_ONLY_NONDIR = "rw-------";
    
    /**
     * First try at a cross-platform method to make a directory or file accessible by
     * the current user only. Resets all permissions on Windows systems and gives all
     * permissions to the current user including executability. On UNIX, either rwx or
     * rw permission is granted based on whether it's a directory or not. Currently only
     * Windows or UNIX-like systems are supported.
     * 
     * @param file the file or directory whose permissions to adjust
     * @throws IOException on failure
     */
    public static void setOwnerAccessOnlyNonExec(File file) throws IOException {
        Path path = file.toPath();

        if (SystemUtils.IS_OS_UNIX) {
            if (file.isDirectory()) {
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(POSIX_PERMS_OWNER_ONLY_DIR));
            } else {
                Files.setPosixFilePermissions(path, PosixFilePermissions.fromString(POSIX_PERMS_OWNER_ONLY_NONDIR));
            }
            return;
        }

        if (!SystemUtils.IS_OS_WINDOWS) {
            throw new IOException("changing permissions not supported on this OS");
        }

        UserPrincipalLookupService upls = path.getFileSystem().getUserPrincipalLookupService();
        UserPrincipal user = upls.lookupPrincipalByName(System.getProperty("user.name"));
        AclEntry.Builder builder = AclEntry.newBuilder();
        builder.setPermissions(
            EnumSet.of(
                AclEntryPermission.ADD_FILE,
                AclEntryPermission.ADD_SUBDIRECTORY,
                AclEntryPermission.APPEND_DATA,
                AclEntryPermission.DELETE,
                AclEntryPermission.DELETE_CHILD,
                AclEntryPermission.EXECUTE,
                AclEntryPermission.LIST_DIRECTORY,
                AclEntryPermission.READ_ACL,
                AclEntryPermission.READ_ATTRIBUTES,
                AclEntryPermission.READ_DATA,
                AclEntryPermission.READ_NAMED_ATTRS,
                AclEntryPermission.SYNCHRONIZE,
                AclEntryPermission.WRITE_ACL,
                AclEntryPermission.WRITE_ATTRIBUTES,
                AclEntryPermission.WRITE_DATA,
                AclEntryPermission.WRITE_NAMED_ATTRS,
                AclEntryPermission.WRITE_OWNER
                ));
        builder.setPrincipal(user);
        builder.setType(AclEntryType.ALLOW);
        AclFileAttributeView aclAttr = Files.getFileAttributeView(path, AclFileAttributeView.class);
        aclAttr.setAcl(Collections.singletonList(builder.build()));

        // fileDataStoreFactory.getDataDirectory().setExecutable(false, true);
        // fileDataStoreFactory.getDataDirectory().setReadable(false, true);
        // fileDataStoreFactory.getDataDirectory().setWritable(false, true);

    }

}
