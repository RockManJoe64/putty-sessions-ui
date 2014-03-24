package com.eoxlive.putty.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class JavaProperties {

	public static void main(String[] args) {
		Properties properties = System.getProperties();
		List<Object> keys = new ArrayList<Object>(properties.keySet());
		Collections.sort(keys, new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				return ((String) o1).compareTo((String) o2);
			}
		});
		for (Object key : keys) {
			System.out.println(String.format("%s=%s", key, properties.get(key)));
		}
	}
	
}
