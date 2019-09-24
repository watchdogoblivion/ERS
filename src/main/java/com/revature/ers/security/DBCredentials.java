package com.revature.ers.security;

public class DBCredentials {

	static final String driver = "org.postgresql.Driver";
	static final String db = "ERS_DB";
	static final String url = System.getenv("ERS_URL") + db;
	static final String user = System.getenv("ERS_USERNAME");
	static final String pass = System.getenv("ERS_PASS");

	public static String getDriver() {
		return driver;
	}

	public static String getDb() {
		return db;
	}

	public static String getUrl() {
		return url;
	}

	public static String getUser() {
		return user;
	}

	public static String getPass() {
		return pass;
	}

}
