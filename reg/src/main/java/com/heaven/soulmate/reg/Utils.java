package com.heaven.soulmate.reg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by ChenJie3 on 2015/10/23.
 */
public class Utils {

    static public Properties readProperties(String filename){
        Properties props = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream(filename);
        try {
            props.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return props;
    }
}