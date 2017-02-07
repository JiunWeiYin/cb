package org.misc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.misc.Configuration;
import org.misc.ConstVar;

import java.io.IOException;
import java.io.InputStream;

import static org.misc.ConstVar.REFERRER;
import static org.misc.ConstVar.USER_AGENT;

public class Apps {
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
            throw new IOException(ex.getMessage());
        }

        return config;
    }

    /**
     * Setup a connection to daily transaction.
     *
     * @return a Connection instance
     * */
    public static Connection getConnection(String url, String userAgent, String referrer, int timeOut) {
        return Jsoup.connect(url).userAgent(userAgent).referrer(referrer).timeout(timeOut);
    }










}
