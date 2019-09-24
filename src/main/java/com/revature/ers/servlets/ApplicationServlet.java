package com.revature.ers.servlets;

import java.io.IOException;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.revature.ers.util.FileManager;
import com.revature.ers.util.ResourceURIs;

/**
 * Servlet implementation class Index
 */
@WebServlet("/application")
public class ApplicationServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(ApplicationServlet.class);
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ApplicationServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("ApplicationServlet: running doGet");
		try {
			PrintWriter out = response.getWriter();
			out.write(new FileManager().getFileContent(request, ResourceURIs.getHTMLURIS(request, "static").get("application")));
			out.flush();
		} catch (IOException e) {
			LOGGER.error(e);
		}
		
	}
}
