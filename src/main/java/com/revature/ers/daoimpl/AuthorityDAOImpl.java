package com.revature.ers.daoimpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.revature.ers.dao.AuthorityDAO;
import com.revature.ers.models.Authority;
import com.revature.ers.security.DBCredentials;

public class AuthorityDAOImpl implements AuthorityDAO{
	private static final Logger LOGGER = Logger.getLogger(AuthorityDAOImpl.class);
	
	public Optional<Authority> findById(long id) {
		Optional<Authority> authorityOptional = Optional.empty();
		String query = "SELECT * FROM authorities WHERE a_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				Authority authority = new Authority(id, name);
				authorityOptional = Optional.of(authority);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return authorityOptional;
	}

	@Override
	public Optional<Authority> findByName(String name) {
		Optional<Authority> authorityOptional = Optional.empty();
		String query = "SELECT * FROM authorities WHERE name=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Long id = rs.getLong("a_id");
				Authority authority = new Authority(id, name);
				authorityOptional = Optional.of(authority);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e);
		} 
		return authorityOptional;
	}
	
	@Override
	public List<Authority> findAll() {
		List<Authority> authorities = new ArrayList<>();
		String query = "SELECT * FROM authorities";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.TYPE_SCROLL_SENSITIVE);
				ResultSet rs = stmt.executeQuery(query);) {
			while (rs.next()) {
				Long id = rs.getLong("a_id");
				String name = rs.getString("name");
				Authority authority = new Authority(id, name);
				authorities.add(authority);
			}
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		return authorities;
	}

	@Override
	public Long save(Authority authority) {
		String query = "INSERT INTO authorities values(default,?)";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());
				PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);) {
			stmt.setString(1, authority.getName());

			int i = stmt.executeUpdate();

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					authority.setId(generatedKeys.getLong("a_id"));
					LOGGER.info(i + " records inserted");
				} else {
					throw new SQLException("Creating UserAccount failed, no ID obtained.");
				}
			}

		} catch (SQLException e) {
			LOGGER.error(e);
		}

		return authority.getId();
	}

	@Override
	public void update(Authority authority) {
		String query = "UPDATE authorities SET name=? WHERE a_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass()); PreparedStatement stmt = conn.prepareStatement(query);) {

			stmt.setString(1, authority.getName());
			stmt.setLong(2, authority.getId());
			int i = stmt.executeUpdate();
			LOGGER.info(i + " records updated");
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}

	@Override
	public void delete(Authority authority) {
		String query  = "DELETE FROM authorities WHERE a_id=?";
		try (Connection conn = DriverManager.getConnection(DBCredentials.getUrl(), DBCredentials.getUser(),
				DBCredentials.getPass());PreparedStatement stmt = conn.prepareStatement(query);) {
			stmt.setLong(1, authority.getId());
			int i = stmt.executeUpdate();
			LOGGER.info(i + " records deleted");
		} catch (SQLException e) {
			LOGGER.error(e);
		}
	}

}
