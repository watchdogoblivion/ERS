package com.revature.ers.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.log4j.Logger;

@WebServlet("/static/*")
public class StaticServlet extends DefaultServlet {

	private static final Logger LOGGER = Logger.getLogger(StaticServlet.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("StaticServlet: running doGet");
		try {
			super.doGet(request, response);
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
	}
}
