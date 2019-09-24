package com.revature.ers.servicesimpl;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.revature.ers.dao.ReimbursementDAO;
import com.revature.ers.daoimpl.ReimbursementDAOimpl;
import com.revature.ers.models.Reimbursement;
import com.revature.ers.models.UserAccount;
import com.revature.ers.services.ReimbursmentService;
import com.revature.ers.util.FilterPair;

public class ReimbursementServiceImpl implements ReimbursmentService {

	private static final ReimbursementDAO ReimbursementDAO = new ReimbursementDAOimpl();
	private static final Gson GSON = new Gson();

	public String getReimbursmentRequestJson(HttpServletRequest request) {
		FilterPair idPair = new FilterPair("e.ua_id", request.getParameter("e_id"));
		FilterPair emailPair = new FilterPair("e.email", request.getParameter("email"));
		FilterPair statusPair = new FilterPair("status", request.getParameter("status"));
		FilterPair orderByPair = new FilterPair("ORDER BY", request.getParameter("ORDERBY"));
		FilterPair limitPair = new FilterPair("LIMIT", request.getParameter("LIMIT"));
		FilterPair offsetPair = new FilterPair("OFFSET", request.getParameter("OFFSET"));

		FilterPair[] pairs = { idPair, emailPair, statusPair, orderByPair, limitPair, offsetPair };
		pairs = Arrays.stream(pairs).filter(p -> p.getValue() != null && !"".equals(p.getValue()))
				.toArray(FilterPair[]::new);
		FilterPair[] pairsCount = { idPair, emailPair, statusPair };
		pairsCount = Arrays.stream(pairsCount).filter(p -> p.getValue() != null && !"".equals(p.getValue()))
				.toArray(FilterPair[]::new);

		List<Reimbursement> reimbursements = ReimbursementDAO.findByParams(pairs);
		reimbursements.forEach(r -> {
			nullifyNPF(r.getEmployeeAccount());
			nullifyNPF(r.getManagerAccount());
		});
		Long totalRowsInDB = ReimbursementDAO.getTotalRows(pairsCount);
		Object[] toJsonArray = { totalRowsInDB, reimbursements };
		return GSON.toJson(toJsonArray);
	}

	private void nullifyNPF(UserAccount ua) {
		ua.setPassword(null);
		ua.setActive(null);
		ua.setBlocked(null);
		ua.setFailedLogins(null);
		ua.setImageUrl(null);
	}
}
