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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

//CHECKSTYLE:OFF
public class IoUtils {

    private IoUtils() {
    }

    /**
     * Creates a new file as safely as possible by first writing to a temporary
     * file in the same directory as the destination file, and then using
     * {@link #syncRenameTo(File, File)} to sync the temp file's contents to
     * disk and then renaming it to dest in an atomic way if possible.
     * 
     * This function does *not* sync after the final rename operation.
     * 
     * @param dest
     *            the destination file name, an existing file will be
     *            overwritten in an atomic way if possible
     * @param data
     *            the data to write to the new file
     * @param cs
     *            the charset to use for writing
     * @throws IOException
     *             on error
     */
    public static void safeWriteTo(File dest, String data, Charset cs) throws IOException {
        File tempFile = File.createTempFile("." + dest.getName(), null, dest.getParentFile());
        try {
            FileUtils.writeStringToFile(tempFile, data, cs, true);
            IoUtils.syncRenameTo(tempFile, dest);
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    /**
     * Same as {@link #safeWriteTo(File, String, Charset)}, but always uses
     * UTF-8 for writing.
     * 
     * @param dest
     *            the destination file name, an existing file will be
     *            overwritten in an atomic way if possible
     * @param data
     *            the data to write to the new file
     * @throws IOException
     *             on error
     */
    public static void safeWriteTo(File dest, String data) throws IOException {
        safeWriteTo(dest, data, Charset.forName("UTF-8"));
    }

    /**
     * Same as {@link #safeWriteTo(File, String, Charset)}, but takes an
     * {@link java.io.InputStream}.
     * 
     * @param dest
     *            the destination file name, an existing file will be
     *            overwritten in an atomic way if possible
     * @param is
     *            the data to write
     * @throws IOException
     *             on error
     */
    public static void safeWriteTo(File dest, InputStream is) throws IOException {
        File tempFile = File.createTempFile("." + dest.getName(), null, dest.getParentFile());
        try {
            try (FileOutputStream fos = new FileOutputStream(tempFile, true)) {
                IOUtils.copyLarge(is, fos);
                fos.getFD().sync();
            }
            IoUtils.renameTo(tempFile, dest);
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    /**
     * fsync, then atomic move, with fallback to non-atomic move. Make sure you
     * have flushed or closed any resources associated with the source file.
     * 
     * Overwrites any file at the destination. Does not work for non-empty
     * source directories.
     * 
     * @param src
     *            the source file
     * @param dest
     *            the destination file
     * @throws IOException
     *             on errors
     */
    public static void syncRenameTo(File src, File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(src, true)) {
            fos.getFD().sync();
        }
        renameTo(src, dest);
    }

    /**
     * Atomic move, with fallback to non-atomic move. Make sure you have flushed
     * or closed any resources associated with the source file.
     * 
     * Overwrites any file at the destination. Does not work for non-empty
     * source directories.
     * 
     * @param src
     *            the source file
     * @param dest
     *            the destination file
     * @throws IOException
     *             on errors
     */
    public static void renameTo(File src, File dest) throws IOException {
        try {
            Files.move(src.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Read some input stream into a byte array, requiring a maximum length.
     * 
     * @param is
     *            the input stream
     * @param maxBytes
     *            max number of bytes to read
     * @return null if the input stream was longer than maxBytes
     * @throws IOException
     *             on I/O error
     */
    public static byte[] toByteArray(InputStream is, long maxBytes) throws IOException {
        if (is == null) {
            throw new NullPointerException();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        do {
            n = is.read(buf);
            if (n > 0) {
                baos.write(buf, 0, n);
            }
        } while (baos.size() < maxBytes && n > 0);
        if (baos.size() > maxBytes) {
            return null;
        }
        return baos.toByteArray();
    }

    /**
     * Read some input stream into a byte array, requiring an expected length.
     * Writing will always start at the start of the given byte array.
     * 
     * @param is
     *            the input stream
     * @param expBytes
     *            number of bytes to read and expect
     * @param arr
     *            provide a byte array for re-use. Ignored if expBytes is larger
     *            than <code>arr.length</code>.
     * @return null if the input stream was longer than maxBytes
     * @throws IOException
     *             on I/O error or if the number of read bytes is less than
     *             expected
     */
    public static byte[] toByteArray(InputStream is, int expBytes, byte[] arr) throws IOException {
        if (is == null) {
            throw new NullPointerException();
        }
        if (arr == null || arr.length < expBytes) {
            arr = new byte[expBytes];
        }

        int nread = 0;
        int n;
        do {
            n = is.read(arr, nread, expBytes - nread);
            if (n <= 0) {
                throw new IOException(
                    "not enough data: terminated after " + nread + " bytes, but expected " + expBytes + " bytes");
            }
            nread += n;
        } while (nread < expBytes);
        return arr;
    }

    /**
     * Calls {@link FileUtils#deleteQuietly(File)} on all arguments.
     * 
     * @param files
     *            the files to delete quietly
     */
    public static void deleteQuietly(File... files) {
        for (File f : files) {
            FileUtils.deleteQuietly(f);
        }
    }

    /**
     * Return the lower case extension of a file's name.
     * 
     * @param filename
     *            the filename
     * @return the filename's extension, ie. "java". Null if there is none or if
     *         filename is null or if filename ends with a dot.
     */
    public static String getExt(String filename) {
        if (filename == null) {
            return null;
        }
        if (filename.indexOf('.') == -1) {
            return null;
        }
        String ext = filename.substring(filename.lastIndexOf('.') + 1);
        return ext.isEmpty() ? null : ext.toLowerCase(Locale.ROOT);
    }

    /**
     * Return the lower case extension of a file's name.
     * 
     * @param file
     *            the file
     * @return the file name's extension, ie. "java". Null if there is none or
     *         if file is null or if the file's name ends with a dot.
     */
    public static String getExt(File file) {
        if (file == null) {
            return null;
        }
        return getExt(file.getName());
    }
}
