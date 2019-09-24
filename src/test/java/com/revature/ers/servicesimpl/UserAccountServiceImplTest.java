package com.revature.ers.servicesimpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revature.ers.daoimpl.UserAccountDAOimpl;
import com.revature.ers.models.Authority;
import com.revature.ers.models.UserAccount;

public class UserAccountServiceImplTest {

	@Mock
	UserAccountDAOimpl userAccountDAOimpl;
	@InjectMocks
	UserAccountServiceImpl userAccountServiceImpl;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		userAccountServiceImpl = new UserAccountServiceImpl(userAccountDAOimpl);
		List<UserAccount> userAccounts = new ArrayList<UserAccount>();
		Authority managerAuthority = new Authority(1L, "MANAGER");
		userAccounts.add(new UserAccount(1L, "Samuel", "Dorilas", "samuedorilas@outlook.com", "Pass123", null, true, false, null, managerAuthority));
		when(userAccountDAOimpl.findAll()).thenReturn(userAccounts);
	}

	@Test
	public void isValidNameTestTrue() {
		assertTrue(userAccountServiceImpl.isValidName("Samuel"));
	}

	@Test
	public void isValidNameTestFalseNumeric() {
		assertFalse(userAccountServiceImpl.isValidName("Samuel1"));
	}

	@Test
	public void isValidNameTestFalseTooLong() {
		assertFalse(userAccountServiceImpl.isValidName("SamuelSamuelSamuelSamuel"));
	}

	@Test
	public void isValidNameValidTestFalseEmpty() {
		assertFalse(userAccountServiceImpl.isValidName(""));
	}

	@Test
	public void isValidNameTestFalseMostSpecialCharacters() {
		assertFalse(userAccountServiceImpl.isValidName("Samuel$"));
	}

	@Test
	public void isValidEmailTestTrue() {
		assertTrue(userAccountServiceImpl.isValidEmail("samuel@outlook.com"));
	}

	@Test
	public void isValidEmailTestFalseMissingAt() {
		assertFalse(userAccountServiceImpl.isValidEmail("samueloutlook.com"));
	}

	@Test
	public void isValidEmailTestFalseMissingDot() {
		assertFalse(userAccountServiceImpl.isValidEmail("samuel@outlookcom"));
	}

	@Test
	public void emailExistsTestTrue() {
		assertTrue(userAccountServiceImpl.emailDoesNotExist("yinyu@gmail.com"));
	}

	@Test
	public void emailExistsTestFalse() {
		assertFalse(userAccountServiceImpl.emailDoesNotExist("samuedorilas@outlook.com"));
	}
}
