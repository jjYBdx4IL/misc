/*
 * Copyright © 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.diskcache;

import com.github.jjYBdx4IL.diskcache.jpa.DiskCacheEntry;
import com.github.jjYBdx4IL.diskcache.jpa.DiskCacheQueryFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

// CHECKSTYLE:OFF
/**
 * This class uses an embedded, disk-backed H2 database to implement a thread-safe, local and persistent
 * cache for raw data. Small chunks of data are stored inside the database for speed, larger chunks are stored
 * in separate files outside the database. This allows for the storage of chunks with sizes only limited by
 * available storage space.
 * <p>
 * How we ensure <b>thread safety</b> regarding data integrity:
 * <ul>
 * <li> Data integrity is of no concern in cases where the entire data tuple is stored in the database and
 * therefore handled entirely by the database.
 * <li> We only need to care about data integrity when storing data in separate files outside the database.
 * Here we need to care about two situations: storing and deleting a data tuple.
 * <li> We trivialize the storage case by allowing for the storage of duplicates, ie. parallel storage of the
 * same key. When retrieving data, we will always get the latest tuple (or a random one when the timestamps
 * are equal).
 * <li> Deletion of files on disk: files stored on disk are named/identified by the main table's sequence id,
 * ie. we will never re-use a file's name. That prevents race conditions that could occur when reading a
 * file's database entry and subsequently accessing the contents on disk -- contents which could have been
 * replaced by something entirely different in the meantime. The non-reuse of file names relieves us from having
 * to synchronize (flushing) the directory containing the big files.
 * </ul>
 * <p>
 * <b>Pruning</b>: tbd.
 *
 * @author jjYBdx4IL
 */
public class DiskCache implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(DiskCache.class);
    public static final String DEFAULT_DB_NAME = "diskcachedb";
    public static final int MAX_KEY_LENGTH = 1024;
    public static final long DEFAULT_EXPIRY_SECS = 86400L;
    public static final String INVALID_DBNAME_CHARS = File.separatorChar + "/\\;:";
    // store every data file larger than this in its separate file on disk
    public static final long MAX_BLOB_SIZE = 32 * 1024;

    private static File getDefaultParentDir() {
        File configDir = new File(System.getProperty("user.home"), ".config");
        // store databases under maven target dir if we run as part of a maven test
        if (System.getProperty("basedir") != null) {
            configDir = new File(System.getProperty("basedir"), "target");
        }
        File f = new File(configDir, DiskCache.class.getName());
        return f;
    }

    protected long expiryMillis = DEFAULT_EXPIRY_SECS * 1000L;
    private final String dbName;
    private final File parentDir;
    private final File fileStorageDir;

    protected final Map<String, String> props = new HashMap<>();
    protected EntityManagerFactory emf = null;

    /**
     * @param parentDir may be null. in that case databases get crated either below ~/.config/...DiskCache or
     * &lt;pwd&gt;/target/...DiskCache if run as part of a maven test.
     * @param dbName the database name identifying the database on disk, ie. the directory below parentDir
     * where Derby stores the database's data. may be null, in which case the default "diskcachedb" is used.
     */
    public DiskCache(File parentDir, String dbName) {
        this(parentDir, dbName, false);
    }

    public DiskCache(String dbName) {
        this(null, dbName, false);
    }

    public DiskCache(File parentDir, String dbName, boolean reinit) {
        this.dbName = dbName != null ? dbName : DEFAULT_DB_NAME;

        if (StringUtils.containsAny(dbName, INVALID_DBNAME_CHARS)) {
            throw new IllegalArgumentException("the db name must not contain " + INVALID_DBNAME_CHARS);
        }

        this.parentDir = parentDir != null ? parentDir : getDefaultParentDir();

        final File dbDir = new File(this.parentDir, this.dbName);
        this.fileStorageDir = new File(dbDir, "files");

        if (dbDir.exists() && reinit) {
            LOG.info("deleting " + dbDir.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(dbDir);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
        if (!this.fileStorageDir.exists()) {
            this.fileStorageDir.mkdirs();
        }

        final String dbLocation = new File(dbDir, "db").getAbsolutePath().replaceAll(":", "\\:");

        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", Boolean.toString(LOG.isTraceEnabled()));
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        props.put("javax.persistence.jdbc.url", "jdbc:h2:" + dbLocation + ";MVCC=TRUE");

        emf = Persistence.createEntityManagerFactory("DiskCachePU", props);

        LOG.info("started.");
    }

    public DiskCache setExpirySecs(long secs) {
        this.expiryMillis = secs * 1000L;
        return this;
    }

    public void put(URL url, byte[] data) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException();
        }
        put(url.toExternalForm(), data);
    }

    public void put(String key, byte[] data) throws IOException {
        if (data == null) {
            throw new IllegalArgumentException();
        }
        try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
            put(key, input);
        }
    }

    public void put(String key, InputStream input) throws IOException {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        if (input == null) {
            throw new IllegalArgumentException();
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (key.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException("key too long: " + key);
        }

        byte[] buf = new byte[(int) MAX_BLOB_SIZE + 1];
        long size = IOUtils.read(input, buf);

        DiskCacheEntry dce;

        final EntityManager em = emf.createEntityManager();
        final DiskCacheQueryFactory queryFactory = new DiskCacheQueryFactory(em);

        TypedQuery<DiskCacheEntry> results = queryFactory.getByUrlQuery(key);
        if (results.getResultList().isEmpty()) {
            dce = new DiskCacheEntry();
            dce.setUrl(key);
        } else {
            dce = results.getResultList().get(0);
        }

        dce.setCreatedAt(0L);
        dce.setData(null);
        dce.setSize(-1L); // mark as unfinished

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // write data to a separate file on disk if it is larger than this
            if (size == MAX_BLOB_SIZE + 1) {
                em.persist(dce);
                tx.commit();

                File dataFile = new File(this.fileStorageDir, Long.toString(dce.getId()));
                try (FileOutputStream fos = new FileOutputStream(dataFile, false)) {
                    IOUtils.write(buf, fos);
                    size += IOUtils.copyLarge(input, fos);
                    fos.getFD().sync();
                }
                LOG.debug("wrote " + size + " bytes to " + dataFile.getAbsolutePath());

                tx.begin();
            } else {
                dce.setData(Arrays.copyOf(buf, (int) size));
            }

            dce.setCreatedAt(System.currentTimeMillis());
            dce.setSize(size);
            em.persist(dce);
            tx.commit();

            LOG.debug("stored " + key + " (" + size + " bytes), " + dce.toString());
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }

    /**
     *
     * @param key
     * @return returns null if the key was not found or the data has expired.
     * @throws IOException
     */
    public byte[] get(String key) throws IOException {
        return get(key, this.expiryMillis);
    }

    public byte[] get(String key, long _expiryMillis) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream is = getStream(key, _expiryMillis)) {
            if (is == null) {
                return null;
            }
            IOUtils.copyLarge(is, baos);
            return baos.toByteArray();
        }
    }

    /**
     *
     * @param key
     * @param _expiryMillis -1 or less to ignore expiration
     * @return
     */
    public InputStream getStream(String key, long _expiryMillis) {

        if (key == null || key.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException();
        }

        final EntityManager em = emf.createEntityManager();
        final DiskCacheQueryFactory queryFactory = new DiskCacheQueryFactory(em);

        TypedQuery<DiskCacheEntry> results = queryFactory.getByUrlQuery(key);
        if (results.getResultList().isEmpty()) {
            return null;
        }

        DiskCacheEntry dce = results.getResultList().get(0);

        final long notBefore = System.currentTimeMillis() - _expiryMillis;
        if (_expiryMillis >= 0L && dce.getCreatedAt() < notBefore) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format(Locale.ROOT, "entry %d seconds too old - %s", (notBefore - dce.getCreatedAt()) / 1000L, key));
            }
            return null;
        }

        if (dce.getData() == null) {
            try {
                return new FileInputStream(new File(this.fileStorageDir, Long.toString(dce.getId())));
            } catch (FileNotFoundException ex) {
                return null;
            }
        } else {
            return new ByteArrayInputStream(dce.getData());
        }
    }

    public InputStream getStream(String key) {
        return getStream(key, this.expiryMillis);
    }

    @Override
    public void close() throws IOException {
        if (emf != null) {
            emf.close();
            emf = null;
        }
        LOG.debug("closed");
    }
}
