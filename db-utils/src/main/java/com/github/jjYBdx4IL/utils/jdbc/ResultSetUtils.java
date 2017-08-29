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
package com.github.jjYBdx4IL.utils.jdbc;

//CHECKSTYLE:OFF
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Some jdbc utility functions.
 *
 * @author Github jjYBdx4IL Projects
 */
public class ResultSetUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ResultSetUtils.class);
    private static final String COL_SEPARATOR = ";";

    public static int getColForLabel(ResultSet rs, String labelname) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (labelname.equals(rsmd.getColumnLabel(i))) {
                return i;
            }
        }

        throw new SQLException("Invalid label name " + labelname);
    }

    public static void dump(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colmax = meta.getColumnCount();
        int i;
        Object o;

        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
        StringBuilder sb = new StringBuilder(512);
        for (i = 0; i < colmax; ++i) {
            if (i > 0) {
                sb.append(COL_SEPARATOR);
            }
            String s = meta.getColumnName(i + 1);
            if (s == null) {
                sb.append("NULL");
            } else {
                sb.append(s);
            }
            s = meta.getColumnTypeName(i + 1);
            if (s == null) {
                sb.append("(NULL)");
            } else {
                sb.append("(");
                sb.append(s);
                sb.append("(");
            }
        }
        LOG.info(sb.toString());

        while (rs.next()) {
            // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
            sb = new StringBuilder(512);
            for (i = 0; i < colmax; ++i) {
                if (i > 0) {
                    sb.append(COL_SEPARATOR);
                }
                o = rs.getObject(i + 1);
                if (o == null) {
                    sb.append("NULL");
                } else {
                    sb.append(o.toString());
                }
                LOG.info(sb.toString());
            }
        }
    }
}
