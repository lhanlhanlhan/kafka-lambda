package cn.hanli.mw.board.data.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取配置文件类
 *
 * @author Han Li
 * Created at 1/6/2021 10:06 下午
 * Modified by Han Li at 1/6/2021 10:06 下午
 */
public class PropertyFileReader {

    private static final Logger logger = Logger.getLogger(PropertyFileReader.class);
    private static final Map<String, Properties> prop = new HashMap<>();


    public static Properties getProperties(String name) throws Exception {
        Properties propFile = prop.get(name);
        if (propFile == null) {
            try (InputStream input = PropertyFileReader
                    .class
                    .getClassLoader()
                    .getResourceAsStream(name + ".properties")) {
                propFile = new Properties();
                propFile.load(input);
                prop.put(name, propFile);
            } catch (IOException ex) {
                logger.error(ex);
                throw ex;
            }
        }
        return propFile;
    }
}
