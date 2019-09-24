package com.revature.ers.frontcontroller;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class FrontControllerServlet
 */
@WebFilter("/*")
public class FrontControllerFilter extends HttpFilter {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(FrontControllerFilter.class);
	private Dispatcher dispatcher = new Dispatcher();


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FrontControllerFilter() {
		super();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		LOGGER.info("FrontControllerFilter: running doFilter");
		
		String path = request.getRequestURI().substring(request.getContextPath().length());
		if (path.startsWith("/static/")) {
			filterChain.doFilter(request, response);
		} else {
			if (path.startsWith("/upload")) {
				filterChain.doFilter(request, response);
			} else {
				dispatcher.dispatch(request, response);
			}
		}
	}

}
