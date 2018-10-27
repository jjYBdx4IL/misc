package com.github.jjYBdx4IL.parser.kaitai.archive;

import static org.junit.Assert.*;

import com.github.jjYBdx4IL.utils.env.Maven;
import io.kaitai.struct.KaitaiStream;
import io.kaitai.struct.RandomAccessFileKaitaiStream;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipTest {

    private static final Logger LOG = LoggerFactory.getLogger(ZipTest.class);
    private static final String TEST_LINE_CONTENT = "this is some test content, you know.\n";
    private static File tempDir = Maven.getTempTestDir(ZipTest.class);
    private static File zipFile = null;
    
    @BeforeClass
    public static void genZipFile() throws IOException {
        zipFile = new File(tempDir, "test.zip");
        LOG.info("writing to zip file " + zipFile.getAbsolutePath());
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            ZipEntry e = new ZipEntry("testentry1");
            LOG.info("PUT NEXT ENTRY");
            zos.putNextEntry(e);
            LOG.info("WRITE TO ENTRY");
            for (int i = 0; i < 1000; i++) {
                zos.write(TEST_LINE_CONTENT.getBytes());
            }
            LOG.info("CLOSE ENTRY");
            zos.closeEntry();
            LOG.info("CLOSE");
        }
    }    
    
    @Test
    public void test() throws IOException {
        Zip data = Zip.fromFile(zipFile.getAbsolutePath());

        assertEquals(3, data.sections().size());
    }

}
