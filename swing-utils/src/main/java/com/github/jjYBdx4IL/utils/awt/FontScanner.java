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
package com.github.jjYBdx4IL.utils.awt;

//CHECKSTYLE:OFF
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.io.DirectoryWalker;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class FontScanner extends DirectoryWalker<String> {

    public FontScanner() {
        super();
    }

    public ArrayList<String> getFontFiles(String startDirectory) throws IOException {
        ArrayList<String> dirs = new ArrayList<>();
        walk(new File(startDirectory), dirs);
        return dirs;
    }

    @Override
    protected void handleFile(File file, int depth, Collection<String> results) throws IOException {
        String path = file.getAbsolutePath();
        if (path.toLowerCase().endsWith(".ttf")) {
            results.add(file.getAbsolutePath());
        }
    }
}
