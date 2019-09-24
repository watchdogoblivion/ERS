package com.revature.ers.models;

import java.sql.Timestamp;
import java.time.Instant;

public class UserAccount {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Timestamp lastLogin;
	private Boolean isActive;
	private Boolean blocked;
	private Long failedLogins;
	private Authority authority;
	private String imageUrl;

	public UserAccount() {
		super();
	}

	public Long getId() {
		return id;
	}

	public UserAccount(Long id, String firstName, String lastName, String email, String password, Authority authority) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.lastLogin = Timestamp.from(Instant.now());
		this.isActive = true;
		this.blocked = false;
		this.failedLogins = 0L;
		this.authority = authority;
	}

	public UserAccount(String firstName, String lastName, String email, String password, Authority authority) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.lastLogin = Timestamp.from(Instant.now());
		this.isActive = true;
		this.blocked = false;
		this.failedLogins = 0L;
		this.authority = authority;
	}

	public UserAccount(Long id, String firstName, String lastName, String email, String password, Timestamp lastLogin,
			Boolean isActive, Boolean blocked, Long failedLogins, Authority authority) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.lastLogin = lastLogin;
		this.isActive = isActive;
		this.blocked = blocked;
		this.failedLogins = failedLogins;
		this.authority = authority;
	}

	@Override
	public String toString() {
		return "UserAccount [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", password=" + password + ", lastLogin=" + lastLogin + ", isActive=" + isActive + ", blocked="
				+ blocked + ", failedLogins=" + failedLogins + ", authority=" + authority + "]";
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Boolean isBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	public Long getFailedLogins() {
		return failedLogins;
	}

	public void setFailedLogins(Long failedLogins) {
		this.failedLogins = failedLogins;
	}

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

	public void setFields(UserAccount userAccount) {
		if(userAccount.firstName != null) {
			this.firstName = userAccount.firstName;
		}
		if(userAccount.lastName != null) {
			this.lastName = userAccount.lastName;
		}
		if(userAccount.email != null) {
			this.email = userAccount.email;
		}
		if(userAccount.password != null) {
			this.password = userAccount.password;
		}
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserAccount other = (UserAccount) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
