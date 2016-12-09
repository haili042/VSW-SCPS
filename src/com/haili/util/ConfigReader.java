package com.haili.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	
	private Properties properties;
	
	public ConfigReader() {

		// 生成输入流
		InputStream ins = ConfigReader.class
				.getResourceAsStream("../../../config.properties");
		// 生成properties对象
		properties = new Properties();
		try {
			properties.load(ins);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Properties getProperties() {
		return properties;
	}
	
}