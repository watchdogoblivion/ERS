package com.revature.ers.servlets.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.revature.ers.models.Resource;
import com.revature.ers.util.FileManager;

/**
 * Servlet implementation class ResourcesServlet
 */
@WebServlet("/api/resources")
public class ResourcesServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(ResourcesServlet.class);
	private static final long serialVersionUID = 1L;
	private static final Gson GSON = new Gson();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ResourcesServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("ResourcesServlet: running doGet");
		FileManager fileManager = new FileManager();
		String[] subdirectories = {"html", "css", "js"};
		Map<String, Resource> resources = fileManager.getHTMLCSSJSResources(request, "static", subdirectories);
		String resourcesJson = GSON.toJson(resources);
		try {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(resourcesJson);
			out.flush();
		} catch (IOException | IllegalStateException e) {
			LOGGER.error(e);
		}
		
	}

}
