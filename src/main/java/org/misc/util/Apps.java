/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.constant.ConstVar;
import org.misc.model.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.misc.constant.ConstVar.*;

public class Apps {
    private static final Logger LOGGER = LogManager.getLogger(Apps.class);

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
            LOGGER.debug(String.format("IOException: '%s'.", ex.getMessage()));
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
                LOGGER.debug(String.format("The attribute '%s' does not exist.", attribute));
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
        String val = elements.get(FEATURE.valueOf(feature).ordinal()).text();
        return val.equals(PLACEHOLDER) ? Integer.MIN_VALUE : Integer.parseInt(val.replace(",", ""));
    }

    /**
     * Read text file.
     *
     * @return BufferedReader
     */
    public static BufferedReader readFileAsBufferedReader(String url) throws IOException {
        InputStreamReader isr = new InputStreamReader(new URL(url).openStream(), BIG5);
        LOGGER.debug(String.format("The encoding type is %s.", isr.getEncoding()));
        return new BufferedReader(isr);
    }

    /**
     * Convert String to Date. eg. 2017/02/28
     *
     * @return Date
     */
    public static Date formatDate(String dateInString, SimpleDateFormat formatter) throws ParseException {
        LOGGER.debug(String.format("The data-in-string is %s.", dateInString));
        return formatter.parse(dateInString);
    }

    /**
     * get how many days.
     *
     * @return int
     */
    public static int getDays(Date start, Date end) {
        LOGGER.debug(String.format("The start date is %s.", start));
        LOGGER.debug(String.format("The end date %s.", end));
        return (int) ((end.getTime() - start.getTime()) / (1000 * 24 * 60 * 60));
    }

}
