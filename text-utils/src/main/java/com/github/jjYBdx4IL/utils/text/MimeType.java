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
package com.github.jjYBdx4IL.utils.text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//CHECKSTYLE:OFF
/**
 * Thread-safe and actually useful with a lot of pre-defined mime-types.
 * 
 */
public class MimeType {

    public static final String FALLBACK = "application/octet-stream";
    
    private final Map<String, String> map = new ConcurrentHashMap<>();

    public void addMimeTypes(String def) {
        String[] parts = def.split("\\s+");
        for (int i = 1; i < parts.length; i++) {
            map.put(parts[i].toLowerCase(), parts[0].toLowerCase());
        }
    }

    public MimeType() {
        addMimeTypes("application/javascript js");
        addMimeTypes("application/msword doc docx docm");
        addMimeTypes("application/pdf pdf");
        addMimeTypes("application/postscript ai eps ps");
        addMimeTypes("application/postscript ps");
        addMimeTypes("application/rss+xml rss");
        addMimeTypes("application/rtf rtf");
        addMimeTypes("application/vnd.ms-excel xls");
        addMimeTypes("application/vnd.ms-excel xls XLS");
        addMimeTypes("application/vnd.ms-excel xls xlsx xlsm");
        addMimeTypes("application/vnd.ms-powerpoint ppt pps pot");
        addMimeTypes("application/vnd.ms-powerpoint ppt pptx pptm");
        addMimeTypes("application/vnd.oasis.database odb");
        addMimeTypes("application/vnd.oasis.opendocument.text odt");
        addMimeTypes("application/vnd.oasis.presentation odp");
        addMimeTypes("application/vnd.oasis.spreadsheet ods");
        addMimeTypes("application/vnd.oasis.text odt");
        addMimeTypes("application/vnd.openxmlformats-officedocument." + "spreadsheetml.sheet xlsx");
        addMimeTypes("application/vnd.openxmlformats-officedocument." + "wordprocessingml.document docx");
        addMimeTypes("application/x-awk awk");
        addMimeTypes("application/x-blender blend");
        addMimeTypes("application/x-cd-image iso");
        addMimeTypes("application/x-compress zip gz tar rar");
        addMimeTypes("application/x-deb deb");
        addMimeTypes("application/x-font-otf otf OTF");
        addMimeTypes("application/x-font-ttf ttf TTF");
        addMimeTypes("application/x-java-applet class");
        addMimeTypes("application/x-java-archive jar");
        addMimeTypes("application/xml xml");
        addMimeTypes("application/x-ms-dos-executable exe msi");
        addMimeTypes("application/x-perl pl");
        addMimeTypes("application/x-php php");
        addMimeTypes("application/x-rpm rpm");
        addMimeTypes("application/x-sharedlib o");
        addMimeTypes("application/x-shellscript sh");
        addMimeTypes("application/x-tar tar");
        addMimeTypes("application/x-texinfo texinfo texi");
        addMimeTypes("application/x-tex tex");
        addMimeTypes("application/x-trash autosave");
        addMimeTypes("application/x-troff t tr roff");
        addMimeTypes("application/x-vnd.oasis.opendocument.spreadsheet ods");
        addMimeTypes("application/zip zip");
        addMimeTypes("audio/ac3 ac3");
        addMimeTypes("audio/basic au");
        addMimeTypes("audio/midi mid");
        addMimeTypes("audio/midi midi mid");
        addMimeTypes("audio/mpeg mp3 mpeg3");
        addMimeTypes("audio/x-aifc aifc");
        addMimeTypes("audio/x-aiff aif aiff");
        addMimeTypes("audio/x-generic wav wma mp3 ogg");
        addMimeTypes("audio/x-mpeg mpeg mpg");
        addMimeTypes("audio/x-wav wav");
        addMimeTypes("image/gif gif GIF");
        addMimeTypes("image/ief ief");
        addMimeTypes("image/jpeg jpeg jpg jpe JPG");
        addMimeTypes("image/png png");
        addMimeTypes("image/png png PNG");
        addMimeTypes("image/svg+xml svg svgz");
        addMimeTypes("image/tiff tiff tif");
        addMimeTypes("image/x-eps eps");
        addMimeTypes("image/x-generic bmp jpg jpeg png tif tiff xpm wmf emf");
        addMimeTypes("image/x-xwindowdump xwd");
        addMimeTypes("text/css css");
        addMimeTypes("text/csv csv");
        addMimeTypes("text/html htm html");
        addMimeTypes("text/html html htm HTML HTM");
        addMimeTypes("text/plain txt");
        addMimeTypes("text/plain txt text TXT TEXT");
        addMimeTypes("text/richtext rtx");
        addMimeTypes("text/rtf rtf");
        addMimeTypes("text/tab-separated-values tsv tab");
        addMimeTypes("text/x-bibtex bib");
        addMimeTypes("text/x-c++hdr h");
        addMimeTypes("text/x-csrc c");
        addMimeTypes("text/x-c++src cpp c++");
        addMimeTypes("text/x-java java");
        addMimeTypes("text/x-log log");
        addMimeTypes("text/xml xml osm");
        addMimeTypes("text/xml xml XML");
        addMimeTypes("text/x-pascal pas");
        addMimeTypes("text/x-po po pot");
        addMimeTypes("text/x-python py");
        addMimeTypes("text/x-sql sql");
        addMimeTypes("text/x-tcl tcl");
        addMimeTypes("text/x-tex tex");
        addMimeTypes("video/mpeg mpeg mpg mpe");
        addMimeTypes("video/mpeg mpeg mpg mpe mpv vbs mpegv");
        addMimeTypes("video/msvideo avi");
        addMimeTypes("video/quicktime qt mov");
        addMimeTypes("video/quicktime qt mov moov");
        addMimeTypes("video/x-generic wmv mpeg mp4 ogv swf mov dvd osp");
        addMimeTypes("video/x-msvideo avi");
    }

    public String get(String fileName) {
        return get(fileName, null);
    }
    
    public String get(String fileName, String charset) {
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String mimeType = map.getOrDefault(fileExt, FALLBACK);
        if (charset != null && (mimeType.startsWith("text/") || mimeType.contains("javascript"))) {
            mimeType += ";charset=" + charset.toLowerCase();
        }
        return mimeType;
    }

}
