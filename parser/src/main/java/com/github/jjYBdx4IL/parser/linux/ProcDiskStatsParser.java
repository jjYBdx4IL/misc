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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class ProcDiskStatsParser {

    private static final Logger LOG = LoggerFactory.getLogger(ProcDiskStatsParser.class);
    public final static String PROC_DISKSTATS_PATH = "/proc/diskstats";

    /**
     * The /proc/diskstats file displays the I/O statistics of block devices. Each line contains the following
     * 14 fields:
     * <ul>
     * <li>1 - major number
     * <li>2 - minor mumber
     * <li>3 - device name
     * <li>4 - reads completed successfully
     * <li>5 - reads merged
     * <li>6 - sectors read
     * <li>7 - time spent reading (ms)
     * <li>8 - writes completed
     * <li>9 - writes merged
     * <li>10 - sectors written
     * <li>11 - time spent writing (ms)
     * <li>12 - I/Os currently in progress
     * <li>13 - time spent doing I/Os (ms)
     * <li>14 - weighted time spent doing I/Os (ms)
     * </ul>
     */
    public static class Data {

        private final int majorNumber;
        private final int minorNumber;
        private final String deviceName;
        private final long readsCompletedSuccessfully;
        private final long readsMerged;
        private final long sectorsRead;
        private final long timeSpentReadingMillis;
        private final long writesCompleted;
        private final long writesMerged;
        private final long sectorsWritten;
        private final long timeSpentWritingMillis;
        private final long iosCurrentlyInProgress;
        private final long timeSpentDoingIOsMillis;
        private final long weightedTimeSpentDoingIOsMillis;

        public Data(
                int majorNumber,
                int minorNumber,
                String deviceName,
                long readsCompletedSuccessfully,
                long readsMerged,
                long sectorsRead,
                long timeSpentReadingMillis,
                long writesCompleted,
                long writesMerged,
                long sectorsWritten,
                long timeSpentWritingMillis,
                long iosCurrentlyInProgress,
                long timeSpentDoingIOsMillis,
                long weightedTimeSpentDoingIOsMillis
        ) {
            this.majorNumber = majorNumber;
            this.minorNumber = minorNumber;
            this.deviceName = deviceName;
            this.readsCompletedSuccessfully = readsCompletedSuccessfully;
            this.readsMerged = readsMerged;
            this.sectorsRead = sectorsRead;
            this.timeSpentReadingMillis = timeSpentReadingMillis;
            this.writesCompleted = writesCompleted;
            this.writesMerged = writesMerged;
            this.sectorsWritten = sectorsWritten;
            this.timeSpentWritingMillis = timeSpentWritingMillis;
            this.iosCurrentlyInProgress = iosCurrentlyInProgress;
            this.timeSpentDoingIOsMillis = timeSpentDoingIOsMillis;
            this.weightedTimeSpentDoingIOsMillis = weightedTimeSpentDoingIOsMillis;
        }

        public Data(Scanner s) {
            this.majorNumber = s.nextInt();
            this.minorNumber = s.nextInt();
            this.deviceName = s.next();
            this.readsCompletedSuccessfully = s.nextLong();
            this.readsMerged = s.nextLong();
            this.sectorsRead = s.nextLong();
            this.timeSpentReadingMillis = s.nextLong();
            this.writesCompleted = s.nextLong();
            this.writesMerged = s.nextLong();
            this.sectorsWritten = s.nextLong();
            this.timeSpentWritingMillis = s.nextLong();
            this.iosCurrentlyInProgress = s.nextLong();
            this.timeSpentDoingIOsMillis = s.nextLong();
            this.weightedTimeSpentDoingIOsMillis = s.nextLong();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("ProcDiskStatsData [");
            builder.append("deviceName=");
            builder.append(getDeviceName());
            builder.append(", iosCurrentlyInProgress=");
            builder.append(getIosCurrentlyInProgress());
            builder.append(", majorNumber=");
            builder.append(getMajorNumber());
            builder.append(", minorNumber=");
            builder.append(getMinorNumber());
            builder.append(", readsCompletedSuccessfully=");
            builder.append(getReadsCompletedSuccessfully());
            builder.append(", readsMerged=");
            builder.append(getReadsMerged());
            builder.append(", sectorsRead=");
            builder.append(getSectorsRead());
            builder.append(", sectorsWritten=");
            builder.append(getSectorsWritten());
            builder.append(", timeSpentDoingIOsMillis=");
            builder.append(getTimeSpentDoingIOsMillis());
            builder.append(", timeSpentReadingMillis=");
            builder.append(getTimeSpentReadingMillis());
            builder.append(", timeSpentWritingMillis=");
            builder.append(getTimeSpentWritingMillis());
            builder.append(", weightedTimeSpentDoingIOsMillis=");
            builder.append(getWeightedTimeSpentDoingIOsMillis());
            builder.append(", writesCompleted=");
            builder.append(getWritesCompleted());
            builder.append(", writesMerged=");
            builder.append(getWritesMerged());
            builder.append("]");
            return builder.toString();
        }

        /**
         * /proc/diskstats column no 1.
         * @return the majorNumber
         */
        public int getMajorNumber() {
            return majorNumber;
        }

        /**
         * /proc/diskstats column no 2.
         * @return the minorNumber
         */
        public int getMinorNumber() {
            return minorNumber;
        }

        /**
         * /proc/diskstats column no 3.
         * @return the deviceName
         */
        public String getDeviceName() {
            return deviceName;
        }

        /**
         * /proc/diskstats column no 4.
         * @return the readsCompletedSuccessfully
         */
        public long getReadsCompletedSuccessfully() {
            return readsCompletedSuccessfully;
        }

        /**
         * /proc/diskstats column no 5.
         * @return the readsMerged
         */
        public long getReadsMerged() {
            return readsMerged;
        }

        /**
         * /proc/diskstats column no 6.
         * @return the sectorsRead
         */
        public long getSectorsRead() {
            return sectorsRead;
        }

        /**
         * /proc/diskstats column no 7.
         * @return the timeSpentReadingMillis
         */
        public long getTimeSpentReadingMillis() {
            return timeSpentReadingMillis;
        }

        /**
         * /proc/diskstats column no 8.
         * @return the writesCompleted
         */
        public long getWritesCompleted() {
            return writesCompleted;
        }

        /**
         * /proc/diskstats column no 9.
         * @return the writesMerged
         */
        public long getWritesMerged() {
            return writesMerged;
        }

        /**
         * /proc/diskstats column no 10.
         * @return the sectorsWritten
         */
        public long getSectorsWritten() {
            return sectorsWritten;
        }

        /**
         * /proc/diskstats column no 11.
         * @return the timeSpentWritingMillis
         */
        public long getTimeSpentWritingMillis() {
            return timeSpentWritingMillis;
        }

        /**
         * /proc/diskstats column no 12.
         * @return the iosCurrentlyInProgress
         */
        public long getIosCurrentlyInProgress() {
            return iosCurrentlyInProgress;
        }

        /**
         * /proc/diskstats column no 13.
         * @return the timeSpentDoingIOsMillis
         */
        public long getTimeSpentDoingIOsMillis() {
            return timeSpentDoingIOsMillis;
        }

        /**
         * /proc/diskstats column no 14.
         * @return the weightedTimeSpentDoingIOsMillis
         */
        public long getWeightedTimeSpentDoingIOsMillis() {
            return weightedTimeSpentDoingIOsMillis;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 17 * hash + this.majorNumber;
            hash = 17 * hash + this.minorNumber;
            hash = 17 * hash + Objects.hashCode(this.deviceName);
            hash = 17 * hash + (int) (this.readsCompletedSuccessfully ^ (this.readsCompletedSuccessfully >>> 32));
            hash = 17 * hash + (int) (this.readsMerged ^ (this.readsMerged >>> 32));
            hash = 17 * hash + (int) (this.sectorsRead ^ (this.sectorsRead >>> 32));
            hash = 17 * hash + (int) (this.timeSpentReadingMillis ^ (this.timeSpentReadingMillis >>> 32));
            hash = 17 * hash + (int) (this.writesCompleted ^ (this.writesCompleted >>> 32));
            hash = 17 * hash + (int) (this.writesMerged ^ (this.writesMerged >>> 32));
            hash = 17 * hash + (int) (this.sectorsWritten ^ (this.sectorsWritten >>> 32));
            hash = 17 * hash + (int) (this.timeSpentWritingMillis ^ (this.timeSpentWritingMillis >>> 32));
            hash = 17 * hash + (int) (this.iosCurrentlyInProgress ^ (this.iosCurrentlyInProgress >>> 32));
            hash = 17 * hash + (int) (this.timeSpentDoingIOsMillis ^ (this.timeSpentDoingIOsMillis >>> 32));
            hash = 17 * hash + (int) (this.weightedTimeSpentDoingIOsMillis ^ (this.weightedTimeSpentDoingIOsMillis >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Data other = (Data) obj;
            if (this.majorNumber != other.majorNumber) {
                return false;
            }
            if (this.minorNumber != other.minorNumber) {
                return false;
            }
            if (!Objects.equals(this.deviceName, other.deviceName)) {
                return false;
            }
            if (this.readsCompletedSuccessfully != other.readsCompletedSuccessfully) {
                return false;
            }
            if (this.readsMerged != other.readsMerged) {
                return false;
            }
            if (this.sectorsRead != other.sectorsRead) {
                return false;
            }
            if (this.timeSpentReadingMillis != other.timeSpentReadingMillis) {
                return false;
            }
            if (this.writesCompleted != other.writesCompleted) {
                return false;
            }
            if (this.writesMerged != other.writesMerged) {
                return false;
            }
            if (this.sectorsWritten != other.sectorsWritten) {
                return false;
            }
            if (this.timeSpentWritingMillis != other.timeSpentWritingMillis) {
                return false;
            }
            if (this.iosCurrentlyInProgress != other.iosCurrentlyInProgress) {
                return false;
            }
            if (this.timeSpentDoingIOsMillis != other.timeSpentDoingIOsMillis) {
                return false;
            }
            if (this.weightedTimeSpentDoingIOsMillis != other.weightedTimeSpentDoingIOsMillis) {
                return false;
            }
            return true;
        }

    }

    public static Data get(String device) {
        try (InputStream is = new FileInputStream(PROC_DISKSTATS_PATH)) {
            return get(device, is);
        } catch (IOException ex) {
            LOG.error("failed to open " + PROC_DISKSTATS_PATH, ex);
            throw new RuntimeException(ex);
        }
    }

    protected static Data get(String device, InputStream procNetDevInputStream) throws IOException {
        Scanner s = new Scanner(procNetDevInputStream);
        while (s.hasNext()) {
            Data data = new Data(s);
            if (device.equals(data.getDeviceName())) {
                return data;
            }
        }
        return null;
    }

    private ProcDiskStatsParser() {
    }
}
