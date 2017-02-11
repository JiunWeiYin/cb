/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.Configuration;
import org.misc.ConstVar;

import java.io.IOException;
import java.io.InputStream;

import static org.misc.ConstVar.*;

public class Apps {
    private static final Logger LOGGER = Logger.getLogger(Apps.class);

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


}
