package com.eoxlive.putty.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PuttyRegistry {

	public List<Session> getRegistrySessions() {
		List<Session> sessions = new ArrayList<Session>();
		
		String command = new StringBuilder("REG QUERY").append(" ")
			.append("HKCU\\Software\\SimonTatham\\PuTTY").append(" ")
			.append("/s").append(" ")
			.append("/se ;").append(" ")
			.append("/f HostName")
			.toString();
		
		try {
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line = reader.readLine();
				while (line != null) {
					if (line.contains("HKEY")) {
						String[] split = line.split("\\s");
						String[] sessionNames = split[split.length - 1].split("\\\\");
						sessions.add(new Session(sessionNames[sessionNames.length - 1]));
					}
					
					line = reader.readLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sessions;
	}
	
}
