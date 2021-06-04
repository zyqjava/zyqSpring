package com.zyqSpring.springframework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Enzo Cotter on 2021/6/4.
 */
public class PropertiesUtils {

    public static void onLoadProperties(String locations, Properties config, ClassLoader classLoader) {
        try(
                InputStream inputStream = classLoader.
                        getResourceAsStream(locations.replace("classpath:", ""))) {
            //加载，保存为properties
            config.load(inputStream);
        } catch (IOException e) {
            System.out.println("读取文件失败" + e.getMessage());
        }
    }
}
