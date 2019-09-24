package com.revature.ers.servicesimpl;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.revature.ers.dao.AuthorityDAO;
import com.revature.ers.dao.UserAccountDAO;
import com.revature.ers.daoimpl.AuthorityDAOImpl;
import com.revature.ers.daoimpl.UserAccountDAOimpl;
import com.revature.ers.models.Authority;
import com.revature.ers.models.UserAccount;
import com.revature.ers.security.SecurityHandler;
import com.revature.ers.services.UserAccountService;
import com.revature.ers.util.FileManager;
import com.revature.ers.util.FilterPair;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class UserAccountServiceImpl implements UserAccountService {

	private static final Logger LOGGER = Logger.getLogger(UserAccountServiceImpl.class);
	private UserAccountDAO userAccountDAO = new UserAccountDAOimpl();
	private AuthorityDAO authorityDAO = new AuthorityDAOImpl();
	private static final Gson GSON = new Gson();
	

	public UserAccountServiceImpl() {
	}
	
	public UserAccountServiceImpl(UserAccountDAO userAccountDAO) {
		this.userAccountDAO = userAccountDAO;
	}
	@Override
	public boolean areValidCredentials(String email, String password) {
		try {
			UserAccount userAccount = userAccountDAO.findByEmail(email).get();
			SecurityHandler securityHandler = new SecurityHandler();
			if (!securityHandler.hashMatches(password, userAccount.getPassword())) {
				return false;
			}
		} catch (NoSuchElementException e) {
			LOGGER.error(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean isValidName(String name) {
		String regex = "[A-Za-z-']{2,20}";
		if (!name.matches(regex)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isValidEmail(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		if (!email.matches(regex)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean emailDoesNotExist(String email) {
		List<UserAccount> userAccounts = userAccountDAO.findAll();
		for (UserAccount userAccount : userAccounts) {
			if (userAccount.getEmail().equals(email)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isValidPassword(String password) {
		String regex = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])\\w{6,}";
		if (!password.matches(regex)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSamePassword(String password, String confirmPassword) {
		return password.equals(confirmPassword);
	}
	
	@Override
	public boolean validate(UserAccount userAccount, String confirmPassword) {
		boolean[] validation = { isValidName(userAccount.getFirstName()),
				isValidName(userAccount.getLastName()),
				isValidEmail(userAccount.getEmail()),
				emailDoesNotExist(userAccount.getEmail()),
				isValidPassword(userAccount.getPassword()),
				isSamePassword(userAccount.getPassword(), confirmPassword) };
		boolean isValid = true;
		for (boolean b : validation) {
			isValid = isValid && b;
		}
		return isValid;
	}
	
	@Override
	public UserAccount createUserAccount(HttpServletRequest request) {
		UserAccount userAccount = null;
		try {
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String authorityName = request.getParameter("authority");
			Authority authority = authorityDAO.findByName(authorityName).orElseThrow(NullPointerException::new);
			return new UserAccount(firstName, lastName, email, password, authority);
		} catch (NullPointerException e) {
			LOGGER.error(e);
		}
		return userAccount;
	}
	
	@Override
	public int validationCodes(HttpServletRequest request, UserAccount userAccount) {
		SecurityHandler securityHandler = new SecurityHandler();
	    if(userAccount.getPassword() != null) {
	    	if(!isValidPassword(userAccount.getPassword())) {return 460;}
	    	if(!isSamePassword(userAccount.getPassword(), request.getParameter("confirm_password"))) {return 461;}
	    	userAccount.setPassword(securityHandler.hash(userAccount.getPassword()));
	    }
	    Jws<Claims> claims = securityHandler.getJwsClaims(request.getHeader("Authorization"));
	    if(!claims.getBody().get("email").equals(userAccount.getEmail())) {
	    	if(!isValidEmail(userAccount.getEmail())) {return 462;}
	    	if(!emailDoesNotExist(userAccount.getEmail())) {return 463;}
	    }
	    if(!isValidName(userAccount.getFirstName())) {return 464;}
    	if(!isValidName(userAccount.getLastName())) {return 465;}
    	return 200;
	}
	
	public String getUserAccountJson(HttpServletRequest request, FilterPair emailPair) {
		FilterPair firstNamePair = new FilterPair("first_name", request.getParameter("first_name"));
		FilterPair lastNamePair = new FilterPair("last_name", request.getParameter("last_name"));				
		String authorityName = request.getParameter("authority");
		FilterPair orderByPair = new FilterPair("ORDER BY", request.getParameter("ORDERBY"));
		FilterPair limitPair = new FilterPair("LIMIT", request.getParameter("LIMIT"));
		FilterPair offsetPair = new FilterPair("OFFSET", request.getParameter("OFFSET"));
		FilterPair authority_id = new FilterPair("empty", null);
		if(authorityName != null) {
			 authority_id = new FilterPair("authority_id", authorityDAO.findByName(authorityName).get().getId().toString());
		}
		FilterPair[] pairs = {authority_id, emailPair, firstNamePair, lastNamePair, orderByPair, limitPair, offsetPair };
		pairs = Arrays.stream(pairs).filter(p -> p.getValue() != null && !"".equals(p.getValue()))
				.toArray(FilterPair[]::new);
		FilterPair[] pairsCount = { authority_id, emailPair, firstNamePair, lastNamePair };
		pairsCount = Arrays.stream(pairsCount).filter(p -> p.getValue() != null && !"".equals(p.getValue()))
				.toArray(FilterPair[]::new);
		
		LOGGER.info(Arrays.toString(pairs));
		List<UserAccount> userAccounts = userAccountDAO.findAllByParams(pairs);
		userAccounts.forEach(ua -> {
			ua.setPassword(null);
			ua.setActive(null);
			ua.setBlocked(null);
			ua.setLastLogin(null);
			ua.setFailedLogins(null);
			String imageUri = ua.getImageUrl();
			if (imageUri != null) {
				ua.setImageUrl(imageUri.substring(imageUri.indexOf(FileManager.staticPath)));
			}
		});
		Long totalRowsInDB = userAccountDAO.getTotalRows(pairsCount);
		LOGGER.info((userAccounts));
		Object[] toJsonArray = {totalRowsInDB, userAccounts};
		return GSON.toJson(toJsonArray);
	}
}
