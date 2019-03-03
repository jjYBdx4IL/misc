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
package com.github.jjYBdx4IL.cms;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CliExporter {

    private static Connection conn = null;
    
    public CliExporter() {
    }
    
    public static void main(String[] args) throws Exception {
        checkArgument(args.length == 1, "need one argument: path to h2 database (without .mv.db suffix)");
        checkNotNull(args[0], "need one argument: path to h2 database (without .mv.db suffix)");
        File dbmvFile = new File(args[0]+".mv.db");
        checkArgument(dbmvFile.exists() && dbmvFile.canRead(), "can't read: " + dbmvFile.getAbsolutePath());
        String DB_URL = "jdbc:h2:" + new File(args[0]).getAbsolutePath();
        Class.forName("org.h2.Driver");
        //System.setProperty("h2.bindAddress", "localhost");
        conn = DriverManager.getConnection(DB_URL, "sa", "sa");
        conn.setAutoCommit(false);
        try {
            new CliExporter().run();
        } catch (Throwable t) {
            conn.rollback();
            conn.close();
            throw t;
        }
        conn.close();
    }
    
    public void run() throws Exception {
        // SELECT id, published, firstpublishedat, lastmodified, createdat,title, pathid,content FROM ARTICLE
        // select at.article_id,t.name from article_tag at, tag t where at.tags_id = t.id and article_id = 19
        final String lf = "\n";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, published, firstpublishedat, lastmodified, createdat,title, pathid,content FROM ARTICLE");
        int count = 0;
        while(rs.next()) {
            count++;
            int id = rs.getInt("id");
            boolean isPublished = rs.getBoolean("published");
            Timestamp firstpublished = rs.getTimestamp("firstpublishedat");
            Timestamp lmod = rs.getTimestamp("lastmodified");
            Timestamp created = rs.getTimestamp("createdat");
            String title = rs.getString("title");
            String pathid = rs.getString("pathid");
            String mdContent = rs.getString("content");
            List<String> tags = getTags(id);
            
            if (firstpublished == null) {
                firstpublished = created;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("---").append(lf);
            sb.append("title: ").append(title).append(lf);
            sb.append("date: ").append(sdf.format(firstpublished)).append(lf);
            sb.append("url: ").append(pathid).append(lf);
            sb.append("created: ").append(sdf.format(created)).append(lf);
            sb.append("updated: ").append(sdf.format(lmod)).append(lf);
            if (!isPublished) {
                sb.append("draft: true").append(lf);
            }
            if (!tags.isEmpty()) {
                sb.append("tags:").append(lf);
                for (String tag : tags) {
                    sb.append("  - ").append(tag).append(lf);
                }
            }
            sb.append("---").append(lf);
            sb.append(mdContent);
            File out = getOutFileName(title, new Date(firstpublished.getTime()));
            System.out.println("creating: " + out);
            FileUtils.write(out, sb.toString(), "UTF-8", false);
        }
        System.out.println("processed: " + count);
        stmt.close();
    }
    
    private List<String> getTags(int articleId) throws Exception {
        List<String> tags = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select t.name as tagname from article_tag at, tag t where at.tags_id = t.id and article_id = "+articleId);
        while(rs.next()) {
            String tagname = rs.getString("tagname");
            checkNotNull(tagname);
            checkArgument(!tagname.trim().isEmpty());
            tags.add(tagname);
        }        
        Collections.sort(tags);
        return tags;
    }
    
    private File getOutFileName(String title, Date created) {
        checkNotNull(created);
        final int MAX_TITLEPART_LEN = 70;
        final String FILENAME_SUFFIX = ".md";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String datePart = sdf.format(created);
        Pattern sanitize = Pattern.compile("[^0-9a-zA-Z]+");
        title = sanitize.matcher(title).replaceAll("-");
        while (title.startsWith("-")) {
            title = title.substring(1);
        }
        while (title.endsWith("-")) {
            title = title.substring(0, title.length()-1);
        }
        if (title.length() > MAX_TITLEPART_LEN) {
            title = title.substring(0, MAX_TITLEPART_LEN);
        }
        File outFile = new File(datePart + title + FILENAME_SUFFIX);
        int i = 0;
        while (outFile.exists()) {
            i++;
            outFile = new File(datePart + title + "-" + i + FILENAME_SUFFIX);
        }
        return outFile;
    }
    
}

