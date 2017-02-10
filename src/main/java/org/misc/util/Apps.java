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

import static org.misc.ConstVar.BGCOLOR;
import static org.misc.ConstVar.CLASS;

public class Apps {
    private static final Logger LOGGER = Logger.getLogger(Apps.class);

    /**
     * Loads YAML configurations.
     *
     * @return a Configuration instance
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
     * @return a Connection instance
     */
    public static Connection getConnection(String url, String userAgent, String referrer, int timeOut) {
        return Jsoup.connect(url).userAgent(userAgent).referrer(referrer).timeout(timeOut);
    }

    /**
     * @return
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


}
