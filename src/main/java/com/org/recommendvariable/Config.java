package com.org.recommendvariable;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private final static String resource = "local.properties";
    private final static Properties properties = new Properties();

    public String getClientId() throws Exception {

        InputStream reader = Config.class.getClassLoader().getResource(resource).openStream();
        properties.load(reader);
        reader.close();
        return properties.getProperty("clientId");
    }

    public String getClientSecret() throws Exception {

        InputStream reader = Config.class.getClassLoader().getResource(resource).openStream();
        properties.load(reader);
        reader.close();
        return properties.getProperty("clientSecret");
    }
    public String getUrls() throws Exception {
        InputStream reader = Config.class.getClassLoader().getResource(resource).openStream();
        properties.load(reader);
        reader.close();
        return properties.getProperty("url");
    }

}
