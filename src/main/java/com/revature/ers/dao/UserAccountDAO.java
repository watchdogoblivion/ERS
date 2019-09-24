package com.revature.ers.dao;

import java.util.List;
import java.util.Optional;

import com.revature.ers.models.UserAccount;
import com.revature.ers.util.FilterPair;

public interface UserAccountDAO {

	Optional<UserAccount> findById(long id);

	Optional<UserAccount> findByEmail(String emailAddress);
	
	List<String> findUserAccountEmails(FilterPair[] pairs);

	List<UserAccount> findAllByParams(FilterPair[] args);
	
	List<UserAccount> findAll();

	Long save(UserAccount userAccount);

	void update(UserAccount userAccount);

	void delete(UserAccount userAccount);
	
	Long getTotalRows(FilterPair[] pairs);
}
