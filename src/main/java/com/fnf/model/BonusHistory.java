package com.fnf.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class BonusHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String payableMonth;
	private BigDecimal bonusAmt;
	@Transient
	private int employeeId;

	
	public BonusHistory() {

	}

	public BonusHistory(int installments, String payableMonth) {
		this.payableMonth = payableMonth;
	}

	public String getPayableMonth() {
		return payableMonth;
	}

	public void setPayableMonth(String payableMonth) {
		this.payableMonth = payableMonth;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getBonusAmt() {
		return bonusAmt;
	}

	public void setBonusAmt(BigDecimal bonusAmt) {
		this.bonusAmt = bonusAmt;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}



}
