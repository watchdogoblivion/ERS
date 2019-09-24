package com.revature.ers.servlets.api;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revature.ers.security.SecurityHandler;
import com.revature.ers.servlets.api.UserAccountServlet;

public class UserAccountServletTest extends UserAccountServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@InjectMocks
	UserAccountServletTest userAccountServlet;
	@Mock
	HttpServletRequest request;
	@Mock
	HttpServletResponse response;
	@Mock
	SecurityHandler securityHandler = new SecurityHandler();

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);		
		this.userAccountServlet.setSecurityHandler(securityHandler);
	}
	
	@Test
	public void doGetNotAuthorized() throws ServletException, IOException {
		String authority = "NoAuthority";
		when(request.getHeader("Authorization")).thenReturn(authority);
		when(response.getStatus()).thenReturn(400);
		when(securityHandler.isAuthenticatedJWT(authority, request)).thenReturn(false);
		userAccountServlet.doGet(request, response);
		assertEquals(response.getStatus(), 400);
	}
	
	@Test
	public void doGetAuthorizedMANAGER() throws ServletException, IOException {
		String authority = "MANAGER";
		when(request.getHeader("Authorization")).thenReturn(authority);
		when(response.getStatus()).thenReturn(200);
		when(securityHandler.isAuthenticatedJWT(authority, request)).thenReturn(true);
		userAccountServlet.doGet(request, response);
		assertEquals(response.getStatus(), 200);
	}
	
	@Test
	public void doGetAuthorizedEMPLOYEE() throws ServletException, IOException {
		String authority = "EMPLOYEE";
		when(request.getHeader("Authorization")).thenReturn(authority);
		when(response.getStatus()).thenReturn(200);
		when(securityHandler.isAuthenticatedJWT(authority, request)).thenReturn(true);
		userAccountServlet.doGet(request, response);
		assertEquals(response.getStatus(), 200);
	}
	
	@Test
	public void doGetException() throws ServletException, IOException {
		String authority = "EMPLOYEE";
		when(request.getHeader("Authorization")).thenReturn(authority);
		userAccountServlet.doGet(request, response);
	}
}
