package com.revature.ers.daoimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.revature.ers.dao.ReimbursementDAO;
import com.revature.ers.models.Reimbursement;
import com.revature.ers.models.UserAccount;
import com.revature.ers.security.DBCredentials;
import com.revature.ers.util.FilterPair;

public class ReimbursementDAOimpl implements ReimbursementDAO {

	private static final Logger LOGGER = Logger.getLogger(ReimbursementDAOimpl.class);
	private static final String[] databaseColumns = { "r_id", "amount", "status", "date_submitted", "user_accounts_id",
			"manager_accounts_id", "state" };

	@Override
	public Optional<Reimbursement> findById(long id) {
		Optional<Reimbursement> reimbursementOptional = Optional.empty();
		String query = "SELECT * FROM reimbursements JOIN user_accounts e ON e.ua_id = user_accounts_id LEFT OUTER JOIN user_accounts m ON m.ua_id = manager_accounts_id WHERE r_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Double amount = rs.getDouble(2);
				String status = rs.getString(3);
				Timestamp dateSubmitted = rs.getTimestamp(4);
				String state = rs.getString(7);

				Long eId = rs.getLong(8);
				String eFirstName = rs.getString(9);
				String eLastName = rs.getString(10);
				String eEmail = rs.getString(11);

				UserAccount employeeAccount = new UserAccount(eId, eFirstName, eLastName, eEmail, null, null, null,
						null, null, null);

				Long mId = rs.getLong(19);
				String mFirstName = rs.getString(20);
				String mLastName = rs.getString(21);
				String mEmail = rs.getString(22);

				UserAccount managerAccount = new UserAccount(mId, mFirstName, mLastName, mEmail, null, null, null, null,
						null, null);

				Reimbursement reimbursement = new Reimbursement(id, amount, status, state, dateSubmitted,
						employeeAccount, managerAccount);
				reimbursementOptional = Optional.of(reimbursement);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return reimbursementOptional;
	}

	@Override
	public List<Reimbursement> findByParams(FilterPair[] pairs) {
		List<Reimbursement> reimbursements = new ArrayList<>();
		final String offset = " OFFSET ";
		final String limit = " LIMIT ";
		StringBuilder query = new StringBuilder(
				"SELECT * FROM reimbursements JOIN user_accounts e ON e.ua_id = user_accounts_id LEFT OUTER JOIN user_accounts m ON m.ua_id = manager_accounts_id");
		for (int i = 0; i < pairs.length; i++) {
			if (!offset.trim().equalsIgnoreCase(pairs[i].getKey()) && !limit.trim().equalsIgnoreCase(pairs[i].getKey())
					&& !"ORDER BY".equalsIgnoreCase(pairs[i].getKey())) {
				if (i == 0) {
					query.append(" WHERE " + pairs[i].getKey() + "=" + "'" + pairs[i].getValue() + "'");
				} else {
					query.append(" AND " + pairs[i].getKey() + "=" + "'" + pairs[i].getValue() + "'");
				}
			} else if (!offset.trim().equalsIgnoreCase(pairs[i].getKey())
					&& !limit.trim().equalsIgnoreCase(pairs[i].getKey())) {
				query.append(" ORDER BY " + pairs[i].getValue());
			} else if (!offset.trim().equalsIgnoreCase(pairs[i].getKey())) {
				query.append(limit + pairs[i].getValue());
			} else {
				query.append(offset + pairs[i].getValue());
			}
		}

		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query.toString());) {

			while (rs.next()) {
				Long id = rs.getLong(1);
				Double amount = rs.getDouble(2);
				String status = rs.getString(3);
				Timestamp dateSubmitted = rs.getTimestamp(4);
				String state = rs.getString(7);

				Long eId = rs.getLong(8);
				String eFirstName = rs.getString(9);
				String eLastName = rs.getString(10);
				String eEmail = rs.getString(11);

				UserAccount employeeAccount = new UserAccount(eId, eFirstName, eLastName, eEmail, null, null, null,
						null, null, null);

				Long mId = rs.getLong(19);
				String mFirstName = rs.getString(20);
				String mLastName = rs.getString(21);
				String mEmail = rs.getString(22);

				UserAccount managerAccount = new UserAccount(mId, mFirstName, mLastName, mEmail, null, null, null, null,
						null, null);

				Reimbursement reimbursement = new Reimbursement(id, amount, status, state, dateSubmitted,
						employeeAccount, managerAccount);
				reimbursements.add(reimbursement);

			}

		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return reimbursements;
	}

	@Override
	public List<Reimbursement> findAll() {
		List<Reimbursement> reimbursements = new ArrayList<>();
		String query = "SELECT * FROM reimbursements JOIN user_accounts e ON e.ua_id = user_accounts_id LEFT OUTER JOIN user_accounts m ON m.ua_id = manager_accounts_id";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query);) {
			while (rs.next()) {
				Long id = rs.getLong(1);
				Double amount = rs.getDouble(2);
				String status = rs.getString(3);
				Timestamp dateSubmitted = rs.getTimestamp(4);
				String state = rs.getString(7);

				Long eId = rs.getLong(8);
				String eFirstName = rs.getString(9);
				String eLastName = rs.getString(10);
				String eEmail = rs.getString(11);

				UserAccount employeeAccount = new UserAccount(eId, eFirstName, eLastName, eEmail, null, null, null,
						null, null, null);

				Long mId = rs.getLong(19);
				String mFirstName = rs.getString(20);
				String mLastName = rs.getString(21);
				String mEmail = rs.getString(22);

				UserAccount managerAccount = new UserAccount(mId, mFirstName, mLastName, mEmail, null, null, null, null,
						null, null);

				Reimbursement reimbursement = new Reimbursement(id, amount, status, state, dateSubmitted,
						employeeAccount, managerAccount);
				reimbursements.add(reimbursement);
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return reimbursements;
	}

	@Override
	public Long save(Reimbursement reimbursement) {
		String query = "INSERT INTO reimbursements values(default,?,?,?,?,?,?)";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
			stmt.setDouble(1, reimbursement.getAmount());
			stmt.setString(2, reimbursement.getStatus());
			stmt.setTimestamp(3, reimbursement.getDateSubmitted());
			stmt.setLong(4, reimbursement.getEmployeeAccount().getId());
			stmt.setLong(5, reimbursement.getManagerAccount().getId());
			stmt.setString(6, reimbursement.getState());
			int i = stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					reimbursement.setId(generatedKeys.getLong("r_id"));
					LOGGER.info(i + " records inserted");
				} else {
					throw new SQLException("Creating UserAccount failed, no ID obtained.");
				}
			}

		} catch (SQLException e) {
			LOGGER.error(e);
		}

		return reimbursement.getId();
	}

	@Override
	public void update(Reimbursement reimbursement) {
		StringBuilder query = new StringBuilder("UPDATE reimbursements SET ");
		int length = databaseColumns.length;
		for (int i = 1; i < length; i++) {
			if (i == length - 1) {
				query.append(databaseColumns[i] + " = ? ");
			} else {
				query.append(databaseColumns[i] + " = ?, ");
			}
		}
		query.append("WHERE " + databaseColumns[0] + " = ?");

		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query.toString());) {

			stmt.setDouble(1, reimbursement.getAmount());
			stmt.setString(2, reimbursement.getStatus());
			stmt.setTimestamp(3, reimbursement.getDateSubmitted());
			stmt.setLong(4, reimbursement.getEmployeeAccount().getId());
			stmt.setLong(5, reimbursement.getManagerAccount().getId());
			stmt.setString(6, reimbursement.getState());
			stmt.setLong(7, reimbursement.getId());
			int i = stmt.executeUpdate();
			LOGGER.info(i + " records updated");
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void delete(Reimbursement reimbursement) {
		String query = "DELETE FROM reimbursements WHERE r_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setLong(1, reimbursement.getId());
			int i = stmt.executeUpdate();
			LOGGER.info(i + " records deleted");
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public Long getTotalRows(FilterPair[] pairs) {
		Long count = null;
		StringBuilder query = new StringBuilder("Select Count(*) FROM reimbursements JOIN user_accounts e ON e.ua_id = user_accounts_id LEFT OUTER JOIN user_accounts m ON m.ua_id = manager_accounts_id");
		for (int i = 0; i < pairs.length; i++) {
			if (i == 0) {
				query.append(" WHERE " + pairs[i].getKey() + "=" + "'" + pairs[i].getValue() + "'");
			} else {
				query.append(" AND " + pairs[i].getKey() + "=" + "'" + pairs[i].getValue() + "'");
			}
		}

		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query.toString());) {
			rs.next();
			count = rs.getLong(1);
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return count;
	}

}
