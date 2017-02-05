package org.misc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.misc.Configuration;
import org.misc.ConstVar;

import java.io.IOException;
import java.io.InputStream;

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
}
