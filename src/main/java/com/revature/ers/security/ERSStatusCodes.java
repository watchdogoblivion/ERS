package com.revature.ers.security;

public class ERSStatusCodes {

	protected static final String[] statusCodes = { "401: Not authorized JWT", "460: Invalid Password.", "461: Passwords do not match",
			"462: Invalid Email.", "463: Email already exists", "464: Invalid first name.", "465: Invalid last name", "466: Invalid Credentials"};

	private ERSStatusCodes() {}
}
