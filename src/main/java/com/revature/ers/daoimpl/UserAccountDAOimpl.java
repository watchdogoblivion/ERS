package com.revature.ers.daoimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.revature.ers.dao.UserAccountDAO;
import com.revature.ers.models.Authority;
import com.revature.ers.models.UserAccount;
import com.revature.ers.security.DBCredentials;
import com.revature.ers.util.FilterPair;

public class UserAccountDAOimpl implements UserAccountDAO {

	private static final Logger LOGGER = Logger.getLogger(UserAccountDAOimpl.class);
	protected static final String[] databaseColumns = { "ua_id", "first_name", "last_name", "email", "password",
			"last_login", "isactive", "blocked", "failed_logins", "authority_id", "image_url" };

	@Override
	public Optional<UserAccount> findById(long id) {
		Optional<UserAccount> userAccountOptional = Optional.empty();
		String query = "SELECT * FROM user_accounts JOIN authorities ON authority_id = a_id WHERE ua_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String firstName = rs.getString(databaseColumns[1]);
				String lastName = rs.getString(databaseColumns[2]);
				String email = rs.getString(databaseColumns[3]);
				String password = rs.getString(databaseColumns[4]);
				Timestamp lastLogin = rs.getTimestamp(databaseColumns[5]);
				Boolean isactive = rs.getBoolean(databaseColumns[6]);
				Boolean blocked = rs.getBoolean(databaseColumns[7]);
				Long failedLogins = rs.getLong(databaseColumns[8]);
				Long authorityId = rs.getLong(databaseColumns[9]);
				String imageUrl = rs.getString(databaseColumns[10]);
				String authorityName = rs.getString("name");
				Authority authority = new Authority(authorityId, authorityName);
				UserAccount userAccount = new UserAccount(id, firstName, lastName, email, password, lastLogin, isactive,
						blocked, failedLogins, authority);
				userAccount.setImageUrl(imageUrl);
				userAccountOptional = Optional.of(userAccount);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return userAccountOptional;
	}

	@Override
	public Optional<UserAccount> findByEmail(String email) {
		Optional<UserAccount> userAccountOptional = Optional.empty();
		String query = "SELECT * FROM user_accounts JOIN authorities ON authority_id = a_id WHERE email=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Long id = rs.getLong(databaseColumns[0]);
				String firstName = rs.getString(databaseColumns[1]);
				String lastName = rs.getString(databaseColumns[2]);
				String password = rs.getString(databaseColumns[4]);
				Timestamp lastLogin = rs.getTimestamp(databaseColumns[5]);
				Boolean isactive = rs.getBoolean(databaseColumns[6]);
				Boolean blocked = rs.getBoolean(databaseColumns[7]);
				Long failedLogins = rs.getLong(databaseColumns[8]);
				Long authorityId = rs.getLong(databaseColumns[9]);
				String imageUrl = rs.getString(databaseColumns[10]);
				String authorityName = rs.getString("name");
				Authority authority = new Authority(authorityId, authorityName);
				UserAccount userAccount = new UserAccount(id, firstName, lastName, email, password, lastLogin, isactive,
						blocked, failedLogins, authority);
				userAccount.setImageUrl(imageUrl);
				userAccountOptional = Optional.of(userAccount);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return userAccountOptional;
	}

	@Override
	public List<UserAccount> findAllByParams(FilterPair[] pairs) {
		List<UserAccount> userAccounts = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT * FROM user_accounts JOIN authorities ON authority_id = a_id");
		final String offset = " OFFSET ";
		final String limit = " LIMIT ";
		for (int i = 0; i < pairs.length; i++) {
			if (!offset.trim().equalsIgnoreCase(pairs[i].getKey()) && !limit.trim().equalsIgnoreCase(pairs[i].getKey())
					&& !"ORDER BY".equalsIgnoreCase(pairs[i].getKey())) {
				if (i == 0) {
					query.append(" WHERE " + pairs[i].getKey() + "='" + pairs[i].getValue() + "'");
				} else {
					query.append(" AND " + pairs[i].getKey() + "='" + pairs[i].getValue() + "'");
				}
			} else if (!offset.trim().equalsIgnoreCase(pairs[i].getKey()) && !limit.trim().equalsIgnoreCase(pairs[i].getKey())) {
				query.append(" ORDER BY " + pairs[i].getValue());
			} else if (!offset.trim().equalsIgnoreCase(pairs[i].getKey())) {
				query.append(limit + pairs[i].getValue());
			} else {
				query.append(offset + pairs[i].getValue());
			}
		}
		LOGGER.info(query);
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query.toString());) {
			while (rs.next()) {
				Long id = rs.getLong(databaseColumns[0]);
				String firstName = rs.getString(databaseColumns[1]);
				String lastName = rs.getString(databaseColumns[2]);
				String email = rs.getString(databaseColumns[3]);
				String password = rs.getString(databaseColumns[4]);
				Timestamp lastLogin = rs.getTimestamp(databaseColumns[5]);
				Boolean isactive = rs.getBoolean(databaseColumns[6]);
				Boolean blocked = rs.getBoolean(databaseColumns[7]);
				Long failedLogins = rs.getLong(databaseColumns[8]);
				Long authorityId = rs.getLong(databaseColumns[9]);
				String imageUrl = rs.getString(databaseColumns[10]);
				String authorityName = rs.getString("name");
				Authority authority = new Authority(authorityId, authorityName);
				UserAccount userAccount = new UserAccount(id, firstName, lastName, email, password, lastLogin, isactive,
						blocked, failedLogins, authority);
				userAccount.setImageUrl(imageUrl);
				userAccounts.add(userAccount);
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return userAccounts;
	}

	@Override
	public List<String> findUserAccountEmails(FilterPair[] pairs) {
		List<String> emails = new ArrayList<>();
		StringBuilder query = new StringBuilder("SELECT email FROM user_accounts");
		for (int i = 0; i < pairs.length; i++) {
			if (i == 0) {
				query.append(" WHERE " + pairs[i].getKey() + "='" + pairs[i].getValue()  + "'");
			} else {
				query.append(" AND " + pairs[i].getKey() + "='" + pairs[i].getValue()  + "'");
			}
		}
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query.toString());) {
			while (rs.next()) {
				emails.add(rs.getString(databaseColumns[3]));
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return emails;
	}
	
	@Override
	public List<UserAccount> findAll() {
		List<UserAccount> userAccounts = new ArrayList<>();
		String query = "SELECT * FROM user_accounts JOIN authorities ON authority_id = a_id";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query);) {
			while (rs.next()) {
				Long id = rs.getLong(databaseColumns[0]);
				String firstName = rs.getString(databaseColumns[1]);
				String lastName = rs.getString(databaseColumns[2]);
				String email = rs.getString(databaseColumns[3]);
				String password = rs.getString(databaseColumns[4]);
				Timestamp lastLogin = rs.getTimestamp(databaseColumns[5]);
				Boolean isactive = rs.getBoolean(databaseColumns[6]);
				Boolean blocked = rs.getBoolean(databaseColumns[7]);
				Long failedLogins = rs.getLong(databaseColumns[8]);
				Long authorityId = rs.getLong(databaseColumns[9]);
				String imageUrl = rs.getString(databaseColumns[10]);
				String authorityName = rs.getString("name");
				Authority authority = new Authority(authorityId, authorityName);
				UserAccount userAccount = new UserAccount(id, firstName, lastName, email, password, lastLogin, isactive,
						blocked, failedLogins, authority);
				userAccount.setImageUrl(imageUrl);
				userAccounts.add(userAccount);
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return userAccounts;
	}

	@Override
	public Long save(UserAccount userAccount) {
		String query = "INSERT INTO user_accounts values(default,?,?,?,?,?,?,?,?,?,?)";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
			stmt.setString(1, userAccount.getFirstName());
			stmt.setString(2, userAccount.getLastName());
			stmt.setString(3, userAccount.getEmail());
			stmt.setString(4, userAccount.getPassword());
			stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setBoolean(6, true);
			stmt.setBoolean(7, false);
			stmt.setLong(8, 0L);
			stmt.setLong(9, userAccount.getAuthority().getId());
			stmt.setString(10, userAccount.getImageUrl());
			int i = stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					userAccount.setId(generatedKeys.getLong("ua_id"));
					LOGGER.info(i + " records inserted");
				} else {
					throw new SQLException("Creating UserAccount failed, no ID obtained.");
				}
			}

		} catch (SQLException e) {
			LOGGER.error(e);
		}

		return userAccount.getId();
	}

	@Override
	public void update(UserAccount userAccount) {
		StringBuilder query = new StringBuilder("UPDATE user_accounts SET ");
		int length = databaseColumns.length;
		for (int i = 1; i < length; i++) {
			if(i == length - 1) {
				query.append(databaseColumns[i] + " = ? ");
			}else {
				query.append(databaseColumns[i] + " = ?, ");
			}
		}
		query.append("WHERE " + databaseColumns[0] + " = ?");
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query.toString());) {

			stmt.setString(1, userAccount.getFirstName());
			stmt.setString(2, userAccount.getLastName());
			stmt.setString(3, userAccount.getEmail());
			stmt.setString(4, userAccount.getPassword());
			stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
			stmt.setBoolean(6, true);
			stmt.setBoolean(7, false);
			stmt.setLong(8, 0L);
			stmt.setLong(9, userAccount.getAuthority().getId());
			stmt.setString(10, userAccount.getImageUrl());
			stmt.setLong(11, userAccount.getId());
			int i = stmt.executeUpdate();
			LOGGER.info(i + " records updated");
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void delete(UserAccount userAccount) {
		String query = "DELETE FROM user_accounts WHERE ua_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setLong(1, userAccount.getId());
			int i = stmt.executeUpdate();
			LOGGER.info(i + " records deleted");
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public Long getTotalRows(FilterPair[] pairs) {
		Long count = null;
		StringBuilder query = new StringBuilder("Select Count(*) FROM user_accounts");
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
