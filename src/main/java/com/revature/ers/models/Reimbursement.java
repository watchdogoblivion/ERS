package com.revature.ers.models;

import java.sql.Timestamp;
import java.time.Instant;

public class Reimbursement {

	private Long id;
	private Double amount;
	private String status;
	private String state;
	private Timestamp dateSubmitted;
	private UserAccount employeeAccount;
	private UserAccount managerAccount;

	public Reimbursement() {
		super();
	}

	public Reimbursement(Long id, Double amount, String status, String state, Timestamp dateSubmitted,
			UserAccount employeeAccount, UserAccount managerAccount) {
		super();
		this.id = id;
		this.amount = amount;
		this.status = status;
		this.state = state;
		this.dateSubmitted = dateSubmitted;
		this.employeeAccount = employeeAccount;
		this.managerAccount = managerAccount;
	}

	public Reimbursement(Double amount, String status, String state, UserAccount employeeAccount,
			UserAccount managerAccount) {
		super();
		this.amount = amount;
		this.status = status;
		this.state = state;
		this.employeeAccount = employeeAccount;
		this.dateSubmitted = Timestamp.from(Instant.now());
		this.managerAccount = managerAccount;
	}

	@Override
	public String toString() {
		return "Reimbursement [id=" + id + ", amount=" + amount + ", status=" + status + ", state=" + state
				+ ", dateSubmitted=" + dateSubmitted + ", employeeAccount=" + employeeAccount + ", managerAccount="
				+ managerAccount + "]";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Timestamp getDateSubmitted() {
		return dateSubmitted;
	}

	public void setDateSubmitted(Timestamp dateSubmitted) {
		this.dateSubmitted = dateSubmitted;
	}

	public UserAccount getEmployeeAccount() {
		return employeeAccount;
	}

	public void setEmployeeAccount(UserAccount userAccount) {
		this.employeeAccount = userAccount;
	}

	public UserAccount getManagerAccount() {
		return managerAccount;
	}

	public void setManagerAccount(UserAccount managerAccount) {
		this.managerAccount = managerAccount;
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
		Reimbursement other = (Reimbursement) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
