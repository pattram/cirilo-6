package org.emile.client.utils;

import java.io.File;
import java.util.UUID;

public class UserProfile {

	private static String[] paths = {System.getenv("USERPROFILE"), System.getenv("TEMP"), System.getProperty("user.home"), System.getProperty("java.io.tmpdir")};

	private String user_home = null;
	
	public UserProfile() {
		
		File file = null;
		
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") >- 1) {
			for (String path: paths) {
				if (path != null) {
					try {
						file = new File(path);
						if (file.canWrite()) {
							file = new File(path + System.getProperty("file.separator") + "cirilo.properties");
							break;
						}
					} catch (Exception e) {}
				}
			}
		} else {
			file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "cirilo.properties");
		}
		
		if (file != null) { 
			try {
				file.createNewFile();
				user_home = file.getAbsolutePath();
				System.setProperty("user.home", user_home);
			} catch (Exception e) {}
		}
	}
	
	public File getTempFile() {
		
		File file = null;
			
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") >- 1) {
			for (String path: paths) {
				if (path != null) {
					try {
						file = new File(path);
						if (file.canWrite()) {
							file = new File(path + System.getProperty("file.separator") + UUID.randomUUID().toString());
							break;
						}
					} catch (Exception e) {}
				}
			}
		} else {
			file = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + UUID.randomUUID().toString());
		}
		
		return file;
		
	}
	
	public String get() {
		return user_home;
	}

}
