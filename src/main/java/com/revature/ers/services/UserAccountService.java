package com.revature.ers.services;

import javax.servlet.http.HttpServletRequest;

import com.revature.ers.models.UserAccount;
import com.revature.ers.util.FilterPair;

public interface UserAccountService {

	boolean isValidName(String lastName);
	boolean isValidEmail(String email);
	boolean emailDoesNotExist(String email);
	boolean isValidPassword(String password);
	boolean isSamePassword(String password, String confirmPassword);
	boolean validate(UserAccount userAccount, String confirmPassword);
	boolean areValidCredentials(String email, String password);
	UserAccount createUserAccount(HttpServletRequest request);
	int validationCodes(HttpServletRequest request, UserAccount userAccount);
	String getUserAccountJson(HttpServletRequest request, FilterPair emailPair);
	
}
