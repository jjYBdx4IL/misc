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
package com.github.jjYBdx4IL.utils.net.yahoo;

//CHECKSTYLE:OFF
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class YahooObservation implements Serializable {

	private static final long serialVersionUID = -6533653506388858525L;
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final SimpleDateFormat SDF = new SimpleDateFormat(DATE_FORMAT);
	
	static {
		SDF.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
    private final Date date;
    private final double open, high, low, close, adjClose;
    private final long volume;

    // CHECKSTYLE IGNORE HiddenField FOR NEXT 2 LINES
    public YahooObservation(String date, String open, String high, String low, String close,
            String volume, String adjClose) throws ParseException {
        this.date = SDF.parse(date);
        this.open = Double.valueOf(open);
        this.high = Double.valueOf(high);
        this.low = Double.valueOf(low);
        this.close = Double.valueOf(close);
        this.volume = Long.valueOf(volume);
        this.adjClose = Double.valueOf(adjClose);
    }

    public static YahooObservation createDateDummy(String date) throws ParseException {
    	return new YahooObservation(date, "0.0", "0.0", "0.0", "0.0", "0", "0.0");
    }
    
    /**
     * @return the date
     */
    public Date getDate() {
        return new Date(date.getTime());
    }

    /**
     * @return the open
     */
    public double getOpen() {
        return open;
    }

    /**
     * @return the high
     */
    public double getHigh() {
        return high;
    }

    /**
     * @return the low
     */
    public double getLow() {
        return low;
    }

    /**
     * @return the close
     */
    public double getClose() {
        return close;
    }

    /**
     * @return the adjClose
     */
    public double getAdjClose() {
        return adjClose;
    }

    /**
     * @return the volume
     */
    public long getVolume() {
        return volume;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("YahooObservation [");
        builder.append("adjClose=");
        builder.append(adjClose);
        builder.append(", close=");
        builder.append(close);
        builder.append(", date=");
        builder.append(date);
        builder.append(", high=");
        builder.append(high);
        builder.append(", low=");
        builder.append(low);
        builder.append(", open=");
        builder.append(open);
        builder.append(", volume=");
        builder.append(volume);
        builder.append("]");
        return builder.toString();
    }
}
