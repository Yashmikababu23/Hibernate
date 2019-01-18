package com.fnf.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Variable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String payableMonth;
	private BigDecimal variableAmt;
	@Transient
	private int employeeId;

	public Variable() {

	}

	public Variable(String payableMonth) {
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

	public BigDecimal getVariableAmt() {
		return variableAmt;
	}

	public void setVariableAmt(BigDecimal variableAmt) {
		this.variableAmt = variableAmt;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

}
