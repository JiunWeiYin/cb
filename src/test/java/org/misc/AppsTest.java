/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.misc.model.Configuration;
import org.misc.util.Apps;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AppsTest {
    private static final Logger LOGGER = Logger.getLogger(AppsTest.class);

    @BeforeClass
    public static void setUpClass() throws Exception {
        LOGGER.setLevel(Level.DEBUG);
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
        assertEquals("http://www.tpex.org.tw/storage/bond_publish/ISSBD5.csv", conf.geturlBondPublish());
    }


    @Test
    public void testGetDays() throws ParseException {
        String s1 = "20170227";
        String s2 = "20170228";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        int days = Apps.getDays(formatter.parse(s1), formatter.parse(s2));
        assertEquals(1, days);
    }







}
