package com.revature.ers.servlets.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.revature.ers.dao.ReimbursementDAO;
import com.revature.ers.dao.UserAccountDAO;
import com.revature.ers.daoimpl.ReimbursementDAOimpl;
import com.revature.ers.daoimpl.UserAccountDAOimpl;
import com.revature.ers.models.Reimbursement;
import com.revature.ers.models.UserAccount;
import com.revature.ers.security.SecurityHandler;
import com.revature.ers.services.ReimbursmentService;
import com.revature.ers.servicesimpl.ReimbursementServiceImpl;
import com.revature.ers.util.AuthorityEnum;
import com.revature.ers.util.StatusEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;

/**
 * Servlet implementation class ReimbursementServlet
 */
@WebServlet("/api/reimbursements")
public class ReimbursementServlet extends HttpServlet {
	private static final Logger LOGGER = Logger.getLogger(ReimbursementServlet.class);
	private static final UserAccountDAO userAccountDAO = new UserAccountDAOimpl();
	private static final ReimbursementDAO ReimbursementDAO = new ReimbursementDAOimpl();
	private static final ReimbursmentService REIMBURSMENT_SERVICE = new ReimbursementServiceImpl();
	private static final long serialVersionUID = 1L;
	

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReimbursementServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.error("INSIDE RSERVE");
		SecurityHandler securityHandler = new SecurityHandler();
		String jwt = request.getHeader("Authorization");
		try {
			if (securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.EMPLOYEE.getName())
					|| securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.MANAGER.getName())) {
				String json = REIMBURSMENT_SERVICE.getReimbursmentRequestJson(request);
				PrintWriter out = response.getWriter();
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				out.print(json);
				out.flush();
			}
		} catch (Exception e) {
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
		LOGGER.info("ReimbursementServlet: running doGet");
		SecurityHandler securityHandler = new SecurityHandler();
		String jwt = request.getHeader("Authorization");
		try {
			if (securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.EMPLOYEE.getName())
					|| securityHandler.isAuthorizedJWT(jwt, AuthorityEnum.MANAGER.getName())) {
				Jws<Claims> claims = securityHandler.getJwsClaims(request.getHeader("Authorization"));
				UserAccount employeeAccount = userAccountDAO.findByEmail((String) claims.getBody().get("email"))
						.orElseThrow(NullPointerException::new);
				UserAccount managerAccount =  new UserAccount();
				managerAccount.setId(0L);
				ReimbursementDAO.save(new Reimbursement(Double.parseDouble(request.getParameter("reimbursment_amount")),
						StatusEnum.PENDING.getName(), null, employeeAccount, managerAccount));
			} else {
				response.sendError(400);
			}
		} catch (NullPointerException | NumberFormatException | ExpiredJwtException e) {
			response.sendError(400);
			LOGGER.error(e);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) {
		Enumeration<String> enumeration = request.getParameterNames();
		if(enumeration.hasMoreElements()) {
			try {
				String stringId = request.getParameter("id");
				String email = request.getParameter("email");
				String state = request.getParameter("state");
				Long id = Long.parseLong(stringId);
				UserAccount managerAccount = userAccountDAO.findByEmail(email).get();
					Reimbursement reimbursement = ReimbursementDAO.findById(id).get();
					if(state != null && state.equalsIgnoreCase(StatusEnum.APPROVED.getName())) {
						reimbursement.setState(StatusEnum.APPROVED.getName());
					}else {
						reimbursement.setState(StatusEnum.DENIED.getName());
					}
					reimbursement.setStatus(StatusEnum.RESOLVED.getName());
					reimbursement.setManagerAccount(managerAccount);
					ReimbursementDAO.update(reimbursement);
				
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

}
