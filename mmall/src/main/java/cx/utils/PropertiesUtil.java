package cx.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	private static Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

	private static Properties properties;
	static {
		String fileName = "mmall.properties";
		properties	= new Properties();
		InputStream inputStream = PropertiesUtil.class.getResourceAsStream("/" + fileName);
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			log.info("文件读取异常");
		}
	}

	public static String getProperty(String key) {
		String value = properties.getProperty(key.trim());
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value.trim();
	}

	public static String getProperty(String key ,String defaultValue) {
		String value = properties.getProperty(key.trim());
		if (StringUtils.isBlank(value)) {
			return defaultValue;
		}
		return value.trim();
	}


}
