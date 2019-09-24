package com.revature.ers.dao;

import java.util.List;
import java.util.Optional;

import com.revature.ers.models.Authority;

public interface AuthorityDAO {

	Optional<Authority> findById(long id);

	Optional<Authority> findByName(String name);

	List<Authority> findAll();

	Long save(Authority authority);

	void update(Authority authority);

	void delete(Authority authority);
}
