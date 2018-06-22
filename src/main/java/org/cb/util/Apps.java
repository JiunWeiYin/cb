/**
 * (C) Copyright Chun-Pei Cheng All Rights Reserved
 * NOTICE:  All information contained herein is, and remains the
 * property of Chun-Pei Cheng. The intellectual and technical
 * concepts contained herein are proprietary to Chun-Pei Cheng
 * and are protected by trade secret, patent law or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Chun-Pei Cheng.
 *
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 *
 **/

package org.cb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.cb.constant.ConstVar;
import org.cb.model.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.cb.constant.ConstVar.*;

public class Apps {

    /**
     * Loads YAML configurations.
     *
     * @return Configuration
     */
    public static Configuration getConfiguration() throws IOException {
        Configuration config;
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ConstVar.CONFIG_FILE_NAME)) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(is, Configuration.class);

        } catch (IOException ex) {
            throw new IOException(ex.getMessage());
        }

        return config;
    }

    /**
     * Setup a connection to daily transaction.
     *
     * @return Connection
     */
    public static Connection getConnection(String url, String userAgent, String referrer, int timeOut) {
        return Jsoup.connect(url).userAgent(userAgent).referrer(referrer).timeout(timeOut);
    }

    /**
     * Looks for an element with the specified features.
     *
     * @return Element
     */
    public static Element searchTable(Elements elements, String attribute, String value) {
        Element element = null;

        switch (attribute) {
            case CLASS:
                for (Element e : elements) {
                    if (e.hasClass(value)) {
                        element = e;
                    }
                }
                break;

            case BGCOLOR:
                for (Element e : elements) {
                    if (e.hasAttr(BGCOLOR) && e.attr(BGCOLOR).equals(value)) {
                        element = e;
                    }
                }
                break;

            default:
                break;
        }

        return element;

    }

    /**
     * Gets the first index of the specified tag.
     *
     * @return int
     */
    public static int indexOfRecord(Elements elements, String tag) {
        for (int i = 0; i < elements.size(); i++) {
            if (!elements.get(i).select(tag).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets value as a String format.
     *
     * @return String
     */
    public static String getValueAsString(Elements elements, String feature) {
        return elements.get(FEATURE.valueOf(feature).ordinal()).text();
    }

    /**
     * Gets value as a String format.
     *
     * @return String
     */
    public static String getValueAsString(Elements elements, int index) {
        return elements.get(index).text().trim();
    }

    /**
     * Gets value as a float format.
     *
     * @return float
     */
    public static float getValueAsFloat(Elements elements, String feature) {
        String val = elements.get(FEATURE.valueOf(feature).ordinal()).text();
        return val.equals(PLACEHOLDER) ? Float.MIN_VALUE : Float.parseFloat(val);
    }

    /**
     * Gets value as an int format.
     *
     * @return int
     */
    public static int getValueAsInt(Elements elements, String feature) {
        String val;
        try {
            val = elements.get(FEATURE.valueOf(feature).ordinal()).text();
        } catch (Exception e) {
            val = "";
        }
        return val.equals(PLACEHOLDER) ? Integer.MIN_VALUE : Integer.parseInt(val.replace(",", ""));
    }
    /**
     * Gets value as a int format.
     *
     * @return float
     */
    public static float getValueAsInt(Elements elements, int index) {
        String val;
        try {
            val = elements.get(index).text().trim();
        } catch (Exception e) {
            val = "";
        }
        return val.isEmpty() || val.equals(PLACEHOLDER) ? 0 : Integer.parseInt(val.replace(",", ""));
    }

    /**
     * Read text file.
     *
     * @return BufferedReader
     */
    public static BufferedReader readAsBufferedReader(String url) throws IOException {
        InputStreamReader isr = new InputStreamReader(new URL(url).openStream(), BIG5);
        return new BufferedReader(isr);
    }

    /**
     * Read text file.
     *
     * @return CSVParser
     */
    public static CSVParser readAsCSVParser(String url) throws Exception {
        CSVParser parser = CSVParser.parse(new URL(url), Charset.forName(BIG5), CSVFormat.RFC4180.withHeader());
        return parser;
    }

    /**
     * Convert String to Date. eg. 2017/02/28
     *
     * @return Date
     */
    public static Date formatDate(String dateInString, SimpleDateFormat formatter) throws ParseException {
        return formatter.parse(dateInString);
    }

    /**
     * Convert Date to String. eg. 2017/02/28
     *
     * @return Date
     */
    public static String printDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.format(date);
    }

    /**
     * get how many days.
     *
     * @return int
     */
    public static int getDays(Date start, Date end) {
        return (int) ((end.getTime() - start.getTime()) / (1000 * 24 * 60 * 60));
    }

}
