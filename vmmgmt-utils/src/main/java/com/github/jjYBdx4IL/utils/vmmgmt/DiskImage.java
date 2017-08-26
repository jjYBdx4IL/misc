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
package com.github.jjYBdx4IL.utils.vmmgmt;

import com.github.jjYBdx4IL.utils.proc.ProcRunner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class DiskImage {

    private static final Logger log = LoggerFactory.getLogger(DiskImage.class);
    private final File image;
    private final DiskImageFormat format;
    private final long sizeGB;
    private final File backingDiskImage;

    public DiskImage(File image, DiskImageFormat format, long sizeGB, File backingDiskImage) {
        this.image = image;
        this.format = format;
        this.sizeGB = sizeGB;
        this.backingDiskImage = backingDiskImage;
    }

    public void create() throws IOException {
        if (!getImage().getParentFile().exists()) {
            getImage().getParentFile().mkdirs();
        }

        List<String> cmdParts = new ArrayList<>();
        Collections.addAll(cmdParts, "qemu-img", "create", "-f", getFormat().name());
        if (getBackingDiskImage() != null) {
            Collections.addAll(cmdParts, "-o", String.format("backing_file=%s", getBackingDiskImage().getAbsolutePath()));
        }
        Collections.addAll(cmdParts, getImage().getAbsolutePath(), String.format("%dG", getSizeGB()));
        ProcRunner r = new ProcRunner(true, cmdParts);
        int exitCode = r.run();
        if (exitCode != 0) {
            log.error(r.getOutputBlob());
            throw new IOException(String.format("qemu-img returned with exit code %d, cmd was: %s",
                    exitCode, StringUtils.join(" ", cmdParts)));
        }
    }

    /**
     * @return the image
     */
    public File getImage() {
        return image;
    }

    /**
     * @return the format
     */
    public DiskImageFormat getFormat() {
        return format;
    }

    /**
     * @return the sizeGB
     */
    public long getSizeGB() {
        return sizeGB;
    }

    /**
     * @return the backingDiskImage
     */
    public File getBackingDiskImage() {
        return backingDiskImage;
    }

    public void convert(DiskImage newDiskImage, boolean compress) throws IOException {
        List<String> cmdParts = new ArrayList<>();
        Collections.addAll(cmdParts, "qemu-img", "convert", "-f", getFormat().name(), "-O", newDiskImage.getFormat().name());
        if (compress) {
            cmdParts.add("-c");
        }
        if (newDiskImage.getBackingDiskImage() != null) {
            Collections.addAll(cmdParts, "-o", String.format("backing_file=%s", newDiskImage.getBackingDiskImage().getAbsolutePath()));
        }
        Collections.addAll(cmdParts, getImage().getAbsolutePath(), newDiskImage.getImage().getAbsolutePath());
        ProcRunner r = new ProcRunner(true, cmdParts);
        int exitCode = r.run();
        if (exitCode != 0) {
            log.error(r.getOutputBlob());
            throw new IOException(String.format("qemu-img returned with exit code %d, cmd was: %s",
                    exitCode, StringUtils.join(" ", cmdParts)));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DiskImage [");
        builder.append("backingDiskImage=");
        builder.append(backingDiskImage);
        builder.append(", format=");
        builder.append(format);
        builder.append(", image=");
        builder.append(image);
        builder.append(", sizeGB=");
        builder.append(sizeGB);
        builder.append("]");
        return builder.toString();
    }
}
