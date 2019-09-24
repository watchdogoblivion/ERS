package com.revature.ers.frontcontroller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.revature.ers.security.SecurityHandler;

public class Dispatcher {
	static final Logger LOGGER = Logger.getLogger(Dispatcher.class);

	public void dispatch(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("Dispatcher: running dispatch method");
		String path = request.getRequestURI().substring(request.getContextPath().length());
		SecurityHandler securityHandler = new SecurityHandler();
		String jwt = request.getHeader("Authorization");
		
		if (path.isEmpty() || "/".equals(path)) {
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/application");
			requestDispatcher.forward(request, response);

		} else if (path.equals("/login")) {
			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/login");
			requestDispatcher.forward(request, response);
		} else if (path.startsWith("/api")) {
			RequestDispatcher requestDispatcher;
			if (path.equals("/api/resources")) {
				requestDispatcher = request.getRequestDispatcher("/api/resources");
				requestDispatcher.forward(request, response);
			} else {
				if (jwt != null && securityHandler.isAuthenticatedJWT(jwt, request)) {
					switch (path) {
					case "/api/user-accounts":
						LOGGER.info("yyy");
						requestDispatcher = request.getRequestDispatcher("/api/user-accounts");
						requestDispatcher.forward(request, response);
						break;
					case "/api/authorities":
						requestDispatcher = request.getRequestDispatcher("/api/authorities");
						requestDispatcher.forward(request, response);
						break;
					case "/api/reimbursements":
						requestDispatcher = request.getRequestDispatcher("/api/reimbursements");
						requestDispatcher.forward(request, response);
						break;
					}
				} else {
					response.setHeader("Authorization", null);
					response.sendError(401, "Authentication required");
				}
			}

		}

	}
}
