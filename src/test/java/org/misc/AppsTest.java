package org.misc;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.misc.util.Apps;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AppsTest {
    private static final Logger LOGGER = Logger.getLogger(AppsTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {
        LOGGER.setLevel(Level.INFO);
    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void testGetUrlBondDaily() throws IOException {
        Configuration conf = Apps.getConfiguration();
        assertEquals("https://tw.stock.yahoo.com/s/list.php?c=%C2d%A4%BD%A5q&rr=0.96324200%201486284133", conf.getUrlBondDaily());
    }

    @Test
    public void testGeturlBondPublish() throws IOException {
        Configuration conf = Apps.getConfiguration();
        assertEquals("http://www.tpex.org.tw/storage/bond_publish/ISSBD5.txt", conf.geturlBondPublish());
    }
}
