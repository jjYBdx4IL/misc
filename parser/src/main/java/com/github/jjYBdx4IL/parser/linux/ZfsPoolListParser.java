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
package com.github.jjYBdx4IL.parser.linux;

import com.github.jjYBdx4IL.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ZfsPoolListParser {

    /**
     * Parse output of <code>zpool list -H -p</code>.
     */
    public static List<Result> parse(String input) throws ParseException {
        try (Scanner s = new Scanner(input)) {
            List<Result> r = new ArrayList<>();
            while (s.hasNextLine()) {
                r.add(new Result(s.nextLine()));
            }
            return r;
        }
    }

    public static class Result {
        public final String name;
        public final long size;
        public final long alloc;
        public final long free;
        public final String ckpoint;
        public final String expandsz;
        public final String frag;
        public final String cap;
        public final String dedup;
        public final String health;
        public final String altroot;
        public final float freePct;

        /**
         * The parse result.
         */
        public Result(String line) throws ParseException {
            String[] parts = line.split("\\t");
            if (parts.length != 11) {
                throw new ParseException("bad format: " + line);
            }
            name = parts[0];
            size = Long.parseLong(parts[1]);
            alloc = Long.parseLong(parts[2]);
            free = Long.parseLong(parts[3]);
            ckpoint = parts[4];
            expandsz = parts[5];
            frag = parts[6];
            cap = parts[7];
            dedup = parts[8];
            health = parts[9];
            altroot = parts[10];
            freePct = size == 0 ? 0.f : (100.f * free / size);
        }
    }
}
