package com.eoxlive.putty.ui;

public class Session {
	
	private static final String DEFAULT_USERNAME = "eoxlive";

	private String puttyConfigName;
	private String userName = DEFAULT_USERNAME;
	private String password;
	
	public Session(String puttyConfigName) {
		this.puttyConfigName = puttyConfigName;
	}

	public Session(String puttyConfigName, String userName, String password) {
		this.puttyConfigName = puttyConfigName;
		this.userName = userName;
		this.password = password;
	}
	
	@Override
	public String toString() {
		return this.puttyConfigName;
	}

	public String getPuttyConfigName() {
		return puttyConfigName;
	}

	public void setPuttyConfigName(String puttyConfigName) {
		this.puttyConfigName = puttyConfigName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
