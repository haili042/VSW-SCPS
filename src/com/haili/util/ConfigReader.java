package com.haili.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	
	private Properties properties;
	
	public ConfigReader() {

		// ����������
		InputStream ins = ConfigReader.class
				.getResourceAsStream("../../../config.properties");
		// ����properties����
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