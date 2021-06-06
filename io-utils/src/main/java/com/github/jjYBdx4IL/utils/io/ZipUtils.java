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

import static java.nio.charset.StandardCharsets.UTF_8;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /**
     * Beware! This method is not very reliable (compared to "unzip -t" command
     * line utility).
     * 
     * @param file
     *            the zip file to test
     * @return true iff no errors were found
     */
    public static boolean test(File file) {

        long count = 0;

        byte[] buf = new byte[4096];

        try (ZipFile zipFile = new ZipFile(file)) {
            for (ZipEntry zipEntry : Collections.list(zipFile.entries())) {
                try (InputStream is = zipFile.getInputStream(zipEntry)) {
                    while (is.read(buf) != -1) {
                    }
                }
                zipEntry.getCrc();
                zipEntry.getCompressedSize();
                zipEntry.getName();
                count++;
            }
        } catch (IOException e) {
            return false;
        }

        return count > 0;
    }

    /**
     * See {@link #extractRecreate(InputStream, Pattern, File, int)}. 
     */
    public static void extractRecreate(Path zipFile, String regex, Path destDir, int stripPathComponents)
        throws IOException {
        extractRecreate(zipFile.toFile(), regex, destDir.toFile(), stripPathComponents);
    }

    /**
     * See {@link #extractRecreate(InputStream, Pattern, File, int)}. 
     */
    public static void extractRecreate(String zipFileLoc, String regex, String destDir, int stripPathComponents)
        throws IOException {
        extractRecreate(new File(zipFileLoc), regex, new File(destDir), stripPathComponents);
    }

    /**
     * See {@link #extractRecreate(InputStream, Pattern, File, int)}. 
     */
    public static void extractRecreate(File zipFile, String regex, File destDir, int stripPathComponents)
        throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFile)) {
            extractRecreate(fis, regex != null ? Pattern.compile(regex) : null, destDir, stripPathComponents);
        }
    }

    /**
     * See {@link #extractRecreate(InputStream, Pattern, File, int)}. 
     */
    public static void extractRecreate(InputStream is, Pattern regex, Path destDir, int stripPathComponents)
        throws IOException {
        extractRecreate(is, regex, destDir.toFile(), stripPathComponents);
    }

    /**
     * Extracts a zip stream into a destination folder. The destination folder
     * will be recreated. This function condenses the probably most typical use
     * case for archive extraction and provides an argument to remove leading
     * path components while extracting the archive.
     * 
     * <p>Caveat: because this implementation is working on a continuous input
     * stream, it is not suited (performance-wise) for random access, ie.
     * extracing single small files out of large archives.
     * 
     * @param is
     *            zip archive data input stream
     * @param regex
     *            select files and folders to be extracted (example entry path
     *            names to match against: "some/folder/", "some/file"). Set to
     *            null to include everything.
     * @param destDir
     *            the destination folder. will be erased without warning.
     * @param stripPathComponents
     *            strip this many path components from the files and folders
     *            being extracted. Set to 0 to not strip any path components and
     *            enable regular behavior. Folder entries above that level are
     *            silently ignored. Files above that level will lead to an
     *            IOException - use the regex argument to exclude them.
     * @throws IOException
     *             on any error, incl. no files extracted, duplicate zip entries
     */
    public static void extractRecreate(InputStream is, Pattern regex, File destDir, int stripPathComponents)
        throws IOException {
        if (destDir.exists()) {
            FileUtils.deleteDirectory(destDir);
        }
        boolean fileExtracted = false;
        try (ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String name = ze.getName();
                if (regex != null && !regex.matcher(name).find()) {
                    continue;
                }
                if (stripPathComponents > 0) {
                    int n = nthIndexOf(name, stripPathComponents);
                    if (n == -1) {
                        if (ze.isDirectory()) {
                            continue;
                        }
                        throw new IOException("archive entry above stripped path: " + name + ", use regex to exclude");
                    }
                    name = name.substring(n + 1);
                    if (name.length() == 0) {
                        if (ze.isDirectory()) {
                            continue;
                        }
                        throw new IOException("empty non-directory entry name after path stripping: " + ze.getName());
                    }
                }
                if (ze.isDirectory()) {
                    File newDir = new File(destDir, name);
                    if (!newDir.exists()) {
                        if (!newDir.mkdirs()) {
                            throw new IOException("failed to create directory: " + newDir);
                        }
                    }
                    continue;
                }
                File newFile = new File(destDir, name);
                if (newFile.exists()) {
                    throw new IOException("file already exists (duplicate zip entry?): " + name);
                }
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                long size = ze.getSize();
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    IOUtils.copyLarge(zis, fos, 0, size);
                }
                fileExtracted = true;
            }
        }
        if (!fileExtracted) {
            throw new IOException("no files have been extracted");
        }
    }

    private static int nthIndexOf(String s, int n) {
        int pos = 0;
        for (int i = 0; i < n && pos != -1; i++) {
            pos = s.indexOf("/", pos);
        }
        return pos;
    }

    /**
     * See {@link #zip(OutputStream, Path, String)}.
     */
    public static void zip(Path outputFile, Path srcDir) throws IOException {
        zip(outputFile, srcDir, null);
    }
    
    /**
     * See {@link #zip(OutputStream, Path, String)}.
     */
    public static void zip(Path outputFile, Path srcDir, String prefix) throws IOException {
        try (OutputStream os = new FileOutputStream(outputFile.toFile())) {
            zip(os, srcDir, prefix);
        }
    }
    
    /**
     * See {@link #zip(OutputStream, Path, String)}.
     */
    public static void zip(OutputStream os, Path srcDir) throws IOException {
        zip(os, srcDir, null);
    }
    
    /**
     * Zip a dir into an output stream, prepending the zip entry names with an optional prefix.
     */
    public static void zip(OutputStream os, Path srcDir, String prefix) throws IOException {
        if (prefix == null) {
            prefix = "";
        }
        try (ZipOutputStream zos = new ZipOutputStream(os)) {
            Files.walkFileTree(srcDir, new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path relPath = srcDir.relativize(dir);
                    if (relPath.isAbsolute()) {
                        throw new IllegalStateException();
                    }
                    String dirNorm = relPath.toString().replace(File.separatorChar, '/');
                    
                    ZipEntry ze = new ZipEntry(dirNorm + "/");
                    ze.setTime(attrs.lastModifiedTime().toMillis());
                    zos.putNextEntry(ze);
                    
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path relPath = srcDir.relativize(file);
                    if (relPath.isAbsolute()) {
                        throw new IllegalStateException();
                    }
                    String fileNorm = relPath.toString().replace(File.separatorChar, '/');
                    
                    ZipEntry ze = new ZipEntry(fileNorm);
                    ze.setTime(attrs.lastModifiedTime().toMillis());
                    ze.setSize(attrs.size());
                    zos.putNextEntry(ze);
                    FileUtils.copyFile(file.toFile(), zos);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * See {@link #zipEntryToString(Path, String, Charset)}. Use UTF-8 as charset. 
     */
    public static String zipEntryToString(File zipFile, String entryName) throws IOException {
        return zipEntryToString(zipFile.toPath(), entryName);
    }
    
    /**
     * See {@link #zipEntryToString(Path, String, Charset)}. Use UTF-8 as charset.
     */
    public static String zipEntryToString(Path zipFile, String entryName) throws IOException {
        return zipEntryToString(zipFile, entryName, UTF_8);
    }
    
    /**
     * See {@link #zipEntryToString(Path, String, Charset)}. 
     */
    public static String zipEntryToString(File zipFile, String entryName, Charset cs) throws IOException {
        return zipEntryToString(zipFile.toPath(), entryName, cs);
    }

    /**
     * Return zip file entry as string.
     */
    public static String zipEntryToString(Path zipFile, String entryName, Charset cs)
        throws IOException {
        try (ZipFile zf = new ZipFile(zipFile.toFile())) {
            ZipEntry ze = zf.getEntry(entryName);
            if (ze == null) {
                return null;
            }
            try (InputStream is = zf.getInputStream(ze)) {
                return IOUtils.toString(is, cs);
            }
        }
    }

    /**
     * See {@link #getManifest(Path)}.
     */
    public static Manifest getManifest(File zipFile) throws IOException {
        return getManifest(zipFile.toPath());
    }
    
    /**
     * Read zip (jar/war) file META-INF/MANIFEST.MF.
     */
    public static Manifest getManifest(Path zipFile) throws IOException {
        try (ZipFile zf = new ZipFile(zipFile.toFile())) {
            ZipEntry ze = zf.getEntry("META-INF/MANIFEST.MF");
            if (ze == null) {
                return null;
            }
            try (InputStream is = zf.getInputStream(ze)) {
                return new Manifest(is);
            }
        }
        
    }
}















