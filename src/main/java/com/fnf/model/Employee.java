package com.fnf.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Employee")
public class Employee {
	@Id
	// @GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="EMPLOYEE_ID")
	private int employeeId;
	private String employeeName;
	private Date dateOfJoining;
	@Transient
	private String doj;
	private Date paymentCycle;
	@Transient
	private String paymentCyc;
	private BigDecimal fixedAmt;
	private BigDecimal totalVariableAmt;
	private int variableInstallments;
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER)
	@JoinColumn(name = "qid")
	@OrderColumn(name = "type")
	private List<Variable> variables;
	private BigDecimal totalBonusAmt;
	private int bonusInstallments;
	@OneToMany(cascade = CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER)
	@JoinColumn(name = "qid")
	@OrderColumn(name = "type")
	private List<Bonus> bonus;
	private BigDecimal medicalInsurance;
	private BigDecimal monthlyAmt;
	private BigDecimal totalAmt;
	public Employee() {

	}

	public Employee(int employeeId, String employeeName) {
		this.employeeId = employeeId;
		this.employeeName = employeeName;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public List<Bonus> getBonus() {
		return bonus;
	}

	public void setBonus(List<Bonus> bonus) {
		this.bonus = bonus;
	}

	public Date getPaymentCycle() {
		return paymentCycle;
	}

	public void setPaymentCycle(Date paymentCycle) {
		this.paymentCycle = paymentCycle;
	}

	public int getVariableInstallments() {
		return variableInstallments;
	}

	public void setVariableInstallments(int variableInstallments) {
		this.variableInstallments = variableInstallments;
	}

	public int getBonusInstallments() {
		return bonusInstallments;
	}

	public void setBonusInstallments(int bonusInstallments) {
		this.bonusInstallments = bonusInstallments;
	}

	public BigDecimal getMedicalInsurance() {
		return medicalInsurance;
	}

	public void setMedicalInsurance(BigDecimal medicalInsurance) {
		this.medicalInsurance = medicalInsurance;
	}

	public Date getDateOfJoining() {
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining) {
		this.dateOfJoining = dateOfJoining;
	}

	public BigDecimal getFixedAmt() {
		return fixedAmt;
	}

	public void setFixedAmt(BigDecimal fixedAmt) {
		this.fixedAmt = fixedAmt;
	}

	public BigDecimal getTotalVariableAmt() {
		return totalVariableAmt;
	}

	public void setTotalVariableAmt(BigDecimal totalVariableAmt) {
		this.totalVariableAmt = totalVariableAmt;
	}

	public BigDecimal getTotalBonusAmt() {
		return totalBonusAmt;
	}

	public void setTotalBonusAmt(BigDecimal totalBonusAmt) {
		this.totalBonusAmt = totalBonusAmt;
	}

	public BigDecimal getMonthlyAmt() {
		return monthlyAmt;
	}

	public void setMonthlyAmt(BigDecimal monthlyAmt) {
		this.monthlyAmt = monthlyAmt;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getPaymentCyc() {
		return paymentCyc;
	}

	public void setPaymentCyc(String paymentCyc) {
		this.paymentCyc = paymentCyc;
	}

	public String getDoj() {
		return doj;
	}

	public void setDoj(String doj) {
		this.doj = doj;
	}

}
