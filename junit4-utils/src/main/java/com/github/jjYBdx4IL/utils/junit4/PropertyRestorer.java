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
package com.github.jjYBdx4IL.utils.junit4;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

//CHECKSTYLE:OFF
/**
 * Helper class for temporarily changing system properties during unit testing.
 *
 * Example:
 * <pre>{@code
 *   private final PropertyRestorer propertyRestorer = PropertyRestorer.getInstance();
 *
 *   &#64;Before
 *   public void beforeTest() {
 *       propertyRestorer.restoreProps();;
 *   }
 *
 *   &#64;Test
 *   public void testFloatParseFloat() {
 *       propertyRestorer.setDefaultLocale(Locale.GERMAN);
 *       assertEquals("0,1", String.format("%f", 0.1f));
 *   }
 * }</pre>
 *
 * @author jjYBdx4IL
 */
public class PropertyRestorer {

    private TimeZone origTimeZone = null;
    private Locale origLocale = null;

    public static PropertyRestorer getInstance() {
        PropertyRestorer pr = new PropertyRestorer();
        return pr;
    }

    private final Map<String, String> props = new HashMap<>();

    private PropertyRestorer() {
    }

    public void forceDefaultTimeZoneRestore() {
        setDefaultTimeZone(TimeZone.getDefault());
    }

    public void forceDefaultLocaleRestore() {
        setDefaultLocale(Locale.getDefault());
    }

    public void setDefaultTimeZone(TimeZone tz) {
        if (origTimeZone == null) {
            origTimeZone = TimeZone.getDefault();
        }
        TimeZone.setDefault(tz);
    }

    public void setDefaultLocale(Locale locale) {
        if (origLocale == null) {
            origLocale = Locale.getDefault();
        }
        Locale.setDefault(locale);
    }

    public void setProperty(String key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (!this.props.containsKey(key)) {
            this.props.put(key, System.getProperty(key));
        }
        System.setProperty(key, value);
    }

    public void restoreProps() {
        for (String key : this.props.keySet()) {
            if (props.get(key) == null) {
                System.clearProperty(key);
            } else {
                System.setProperty(key, props.get(key));
            }
        }
        if (origTimeZone != null) {
            TimeZone.setDefault(origTimeZone);
        }
        if (origLocale != null) {
            Locale.setDefault(origLocale);
        }
    }
}
