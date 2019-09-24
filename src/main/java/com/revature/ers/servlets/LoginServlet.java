package com.revature.ers.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.revature.ers.dao.UserAccountDAO;
import com.revature.ers.daoimpl.UserAccountDAOimpl;
import com.revature.ers.models.UserAccount;
import com.revature.ers.security.SecurityHandler;
import com.revature.ers.services.UserAccountService;
import com.revature.ers.servicesimpl.UserAccountServiceImpl;
import com.revature.ers.util.FileManager;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	UserAccountService userAccountService = new UserAccountServiceImpl();
	UserAccountDAO userAccountDAO = new UserAccountDAOimpl();
	private static final Logger LOGGER = Logger.getLogger(LoginServlet.class);
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("LoginServlet: running doPost");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		if (userAccountService.areValidCredentials(email, password)) {
			UserAccount userAccount = userAccountDAO.findByEmail(email).get();
			String authorityName = userAccount.getAuthority().getName();
			SecurityHandler securityHandler = new SecurityHandler();
			String IP = request.getRemoteAddr();
			String subject = "User Token";
			Map<String, String> claims = new HashMap<>();
			claims.put("email", userAccount.getEmail());
			claims.put("authority", authorityName);
			claims.put("IP", IP);
			Long amountToAdd = 7L;
			ChronoUnit unit = ChronoUnit.DAYS;
			String jwt = securityHandler.createJWT(subject, claims, amountToAdd, unit);
			response.setHeader("Authorization", jwt);
			
			String imageUri = userAccount.getImageUrl();
			if(imageUri != null) {
				imageUri = imageUri.substring(imageUri.indexOf(FileManager.staticPath));
				LOGGER.info(imageUri);
				try {
					PrintWriter out = response.getWriter();
					out.print(imageUri);
					out.flush();
				} catch (Exception e) {
					LOGGER.error(e);
				}
			}
		} else {			
			response.setHeader("Authorization", null);
		}

	}

}
