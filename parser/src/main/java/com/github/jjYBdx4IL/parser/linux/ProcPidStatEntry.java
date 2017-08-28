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

//CHECKSTYLE:OFF
/**
 *
 * @author Github jjYBdx4IL Projects
 */
public enum ProcPidStatEntry {

    pid(0), //: 1289
    tcomm(1), //: (java)
    state(2), //: S
    ppid(3), //: 1272
    pgid(4), //: 1271
    sid(5), //: 1271
    tty_nr(6), //: 0
    tty_pgrp(7), //: -1
    flags(8), //: 1077936128
    min_flt(9), //: 198123
    cmin_flt(10), //: 11758436
    maj_flt(11), //: 343
    cmaj_flt(12), //: 1099
    utime(13), //: 195.050000
    stime(14), //: 21.800000
    cutime(15), //: 2122.050000
    cstime(16), //: 86.210000
    priority(17), //: 20
    nice(18), //: 0
    num_threads(19), //: 45
    it_real_value(20), //: 0.000000
    start_time(21), //: 02.20 01:40 (5014.8s)
    vsize(22), //: 7875907584
    rss(23), //: 142401
    rsslim(24), //: 9223372036854775807
    start_code(25), //: 1
    end_code(26), //: 1
    start_stack(27), //: 0
    esp(28), //: 0
    eip(29), //: 0
    pending(30), //: 0000000000000000
    blocked(31), //: 0000000000000000
    sigign(32), //: 0000000000000001
    sigcatch(33), //: 0000000001005cce
    wchan(34), //: 0
    zero1(35), //: 0
    zero2(36), //: 0
    exit_signal(37), //: 0000000000000011
    cpu(38), //: 1
    rt_priority(39), //: 0
    policy(40); //: 0

    private final int index;

    private ProcPidStatEntry(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }

}
