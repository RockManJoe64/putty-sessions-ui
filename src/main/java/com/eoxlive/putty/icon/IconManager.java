package com.eoxlive.putty.icon;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class IconManager {

	private static final String ICON_RESOURCE_LOCATION = IconManager.class
			.getPackage().getName().replace('.', '/');

	private static IconManager instance;

	private Map<String, Map<Integer, Image>> iconMap = new HashMap<String, Map<Integer, Image>>();

	public static IconManager getInstance() {
		if (instance == null) {
			instance = new IconManager();
		}

		return instance;
	}

	public Image getIcon(String iconName, int size) {
		Image iconImage = null;

		if (!iconMap.containsKey(iconName)) {
			Map<Integer, Image> iconSet = loadIcons(iconName);
			this.iconMap.put(iconName, iconSet);
		}

		Map<Integer, Image> map = iconMap.get(iconName);
		iconImage = map.get(size);

		return iconImage;
	}

	public List<Image> getIconSet(String iconName) {
		if (!iconMap.containsKey(iconName)) {
			Map<Integer, Image> iconSet = loadIcons(iconName);
			this.iconMap.put(iconName, iconSet);
		}

		Map<Integer, Image> map = iconMap.get(iconName);

		return new ArrayList<Image>(map.values());
	}

	private Map<Integer, Image> loadIcons(final String iconName) {
		Map<Integer, Image> icons = new HashMap<Integer, Image>();
		
		try {
			Enumeration<URL> resources = IconManager.class.getClassLoader()
					.getResources(ICON_RESOURCE_LOCATION);
			URL url = resources.nextElement();
			if ("file".equals(url.getProtocol())) {
				loadFromDir(iconName, icons, url);
			} else {
				loadFromJar(iconName, icons, url);
			}
		} catch (IOException | URISyntaxException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		
		return icons;
	}

	private void loadFromJar(final String iconName, Map<Integer, Image> icons, URL url)
			throws IOException {
		JarURLConnection urlcon = (JarURLConnection) (url.openConnection());
		try (JarFile jarFile = urlcon.getJarFile();) {
			Enumeration<JarEntry> entries = jarFile.entries();
			String regex = ICON_RESOURCE_LOCATION + "/" + iconName + "-([0-9]+)\\.png";
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				if (pattern.matcher(jarEntry.getName()).find()) {
					BufferedImage bufferedImage = ImageIO.read(jarFile
							.getInputStream(jarEntry));
					String filename = jarEntry.getName();
					String[] tokens = filename.split("\\.")[0].split("-");
					int size = Integer.valueOf(tokens[tokens.length - 1]);
					icons.put(size, bufferedImage);
				}
			}
		}
	}

	private void loadFromDir(final String iconName,
			Map<Integer, Image> icons, URL url) throws IOException, URISyntaxException {
		File resourceDir = new File(url.toURI());
		File[] files = resourceDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.contains(iconName);
			}
		});
		for (File file : files) {
			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				String filename = file.getName();
				String[] tokens = filename.split("\\.")[0].split("-");
				int size = Integer.valueOf(tokens[tokens.length - 1]);
				icons.put(size, bufferedImage);
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

}
