package com.revature.ers.servlets.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.revature.ers.dao.UserAccountDAO;
import com.revature.ers.daoimpl.UserAccountDAOimpl;
import com.revature.ers.models.UserAccount;
import com.revature.ers.security.SecurityHandler;
import com.revature.ers.services.UserAccountService;
import com.revature.ers.servicesimpl.UserAccountServiceImpl;
import com.revature.ers.util.AuthorityEnum;
import com.revature.ers.util.FileManager;
import com.revature.ers.util.FilterPair;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * Servlet implementation class UserAccount
 */
@WebServlet("/api/user-accounts")
@MultipartConfig
public class UserAccountServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(UserAccountServlet.class);
	private static final long serialVersionUID = 1L;
	private static final UserAccountService USER_ACCOUNT_SERVICE = new UserAccountServiceImpl();
	private static final UserAccountDAO U_ACCOUNT_DAO = new UserAccountDAOimpl();
	private static final Gson GSON = new Gson();
	private SecurityHandler securityHandler = new SecurityHandler();
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserAccountServlet() {
		super();
	}
	
	public void setSecurityHandler(SecurityHandler securityHandler) {
		this.securityHandler = securityHandler;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("UserAccountServlet: running doGet");
		String jwt = request.getHeader("Authorization");
		try {
			if (securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.EMPLOYEE.getName())
					|| securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.MANAGER.getName())) {
				String email = request.getParameter("email");
				FilterPair emailPair = new FilterPair("email", email);

				if (emailPair.getValue() != null && emailPair.getValue().equalsIgnoreCase("ALL")) {
					String userAccountsJson = GSON.toJson(U_ACCOUNT_DAO.findUserAccountEmails(new FilterPair[] {}));
					PrintWriter out = response.getWriter();
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					out.print(userAccountsJson);
					out.flush();
				} else {
					String userAccountsJson = USER_ACCOUNT_SERVICE.getUserAccountJson(request, emailPair);
					PrintWriter out = response.getWriter();
					response.setContentType("application/json");
					response.setCharacterEncoding("UTF-8");
					out.print(userAccountsJson);
					out.flush();
				}
			} else {
				response.sendError(400);
			}
		} catch (IOException | IllegalStateException e) {
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
		LOGGER.info("UserAccountServlet: running doPost");
		String jwt = request.getHeader("Authorization");
		try {
			if (securityHandler.isAuthorizedJWT(jwt, "MANAGER")) {
				UserAccount userAccount = USER_ACCOUNT_SERVICE.createUserAccount(request);
				boolean isValid = USER_ACCOUNT_SERVICE.validate(userAccount, request.getParameter("confirm_password"));
				if (isValid) {
					userAccount.setPassword(new SecurityHandler().hash(userAccount.getPassword()));
					U_ACCOUNT_DAO.save(userAccount);
				} else {
					response.sendError(466);
				}
			} else {
				response.sendError(400);
			}
		} catch (ExpiredJwtException e) {
			LOGGER.info("Session expired");
			response.setHeader("Authorization", null);
			response.sendError(402, "Your session is expired");
		} catch (Exception e) {
			LOGGER.error(e);
		}

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String jwt = request.getHeader("Authorization");
		try {
			if (securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.EMPLOYEE.getName())
					|| securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.MANAGER.getName())) {
				UserAccount userAccount = USER_ACCOUNT_SERVICE.createUserAccount(request);
				int statusCode = USER_ACCOUNT_SERVICE.validationCodes(request, userAccount);
				if (statusCode != 200) {
					response.sendError(statusCode);
				} else {
					Part filePart = request.getPart("file");
					String imageUrl = null;
					if (filePart != null) {
						String filename = filePart.getName();
						LOGGER.info(filename);
						String newFileName = userAccount.getEmail() + "_" +  filename +"_ProfilePicture.jpg";
						imageUrl = FileManager.fileSystemStorageSimulation + '\\' + newFileName;
						new FileManager().saveImageFile(request, filePart, imageUrl);
					}
					UserAccount dbUserAccount = U_ACCOUNT_DAO.findByEmail(request.getParameter("email")).get();
					dbUserAccount.setFields(userAccount);
					if (imageUrl != null) {
						dbUserAccount.setImageUrl(imageUrl);
						U_ACCOUNT_DAO.update(dbUserAccount);
						imageUrl = imageUrl.substring(imageUrl.indexOf(FileManager.staticPath));
						LOGGER.info(imageUrl);
						PrintWriter out = response.getWriter();
						out.print(imageUrl);
						out.flush();
					} else if(userAccount.getImageUrl() != null){
						U_ACCOUNT_DAO.update(dbUserAccount);
						imageUrl = userAccount.getImageUrl().substring(userAccount.getImageUrl().indexOf(FileManager.staticPath));
						LOGGER.info(imageUrl);
						PrintWriter out = response.getWriter();
						out.print(imageUrl);
						out.flush();
					}else {
						LOGGER.info("whu");
						U_ACCOUNT_DAO.update(dbUserAccount);
					}
				}
			} else {
				response.sendError(400);
			}

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}
