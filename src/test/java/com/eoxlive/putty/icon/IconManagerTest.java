package com.eoxlive.putty.icon;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IconManagerTest {

	private String[] files;

	@Before
	public void before() {
		files = new String[] { "network-server-16.png",
				"applications-internet-128.png", "emblem-readonly-512.png",
				"emblem-readonly-32.png" };
	}
	
	@Test
	public void matchAllImageFiles() {
		String regex = "([0-9]+)\\.png";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		int counter = 0;
		for (String string : files) {
			if (pattern.matcher(string).find()) {
				counter++;
			}
		}
		Assert.assertTrue("Counter=" + counter, counter == files.length);
	}
	
	@Test
	public void matchOneImageFiles() {
		String iconName = "network-server";
		String regex = iconName + "-" + "([0-9]+)\\.png";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		int counter = 0;
		for (String string : files) {
			if (pattern.matcher(string).find()) {
				counter++;
			}
		}
		Assert.assertTrue("Counter=" + counter, counter == 1);
	}
	
	@Test
	public void matchTwoImageFiles() {
		String iconName = "emblem-readonly";
		String regex = iconName + "-" + "([0-9]+)\\.png";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		int counter = 0;
		for (String string : files) {
			if (pattern.matcher(string).find()) {
				counter++;
			}
		}
		Assert.assertTrue("Counter=" + counter, counter == 2);
	}

}
