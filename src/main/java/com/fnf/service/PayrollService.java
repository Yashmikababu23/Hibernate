package com.fnf.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fnf.Repository.EmployeeDao;
import com.fnf.Repository.PayrollHisRepository;
import com.fnf.Repository.PayrollRepository;
import com.fnf.model.Bonus;
import com.fnf.model.BonusHistory;
import com.fnf.model.Employee;
import com.fnf.model.EmployeeHistory;
import com.fnf.model.Variable;
import com.fnf.model.VariableHistory;

@Service
public class PayrollService implements IPayrollService {
	private static final Logger logger = LoggerFactory.getLogger(PayrollService.class);
	@Autowired
	PayrollRepository payrollRepository;
	@Autowired
	PayrollHisRepository payrollHisRepository;
	@Autowired
	EmployeeDao employeeDao;

	public Employee getEmployeeById(int employeeId) {
		return payrollRepository.findById(employeeId).orElse(null);
	}

	public List<Employee> getEmployeeDetails() {
		return (List<Employee>) payrollRepository.findAll();
	}

	public List<EmployeeHistory> getEmployeeHistoryDetails() {
		return (List<EmployeeHistory>) payrollHisRepository.findAll();
	}

	public List<Employee> getEmployeeByMonth(String month) {
		List<Employee> employees = new ArrayList<>();
		employees = payrollRepository.findEmpByMonth();
		if (employees != null) {
			boolean variableIndicator = false;
			boolean bonusIndicator = false;
			ListIterator<Employee> itr = employees.listIterator();
			while (itr.hasNext()) {
				Employee emp = (Employee)itr.next();
				emp.setDoj(new SimpleDateFormat("dd/MM/yyyy").format(emp.getDateOfJoining()));
				emp.setPaymentCyc(new SimpleDateFormat("dd/MM/yyyy").format(emp.getPaymentCycle()));
				if (emp.getTotalVariableAmt() != null && emp.getTotalVariableAmt() != BigDecimal.ZERO) {
					ListIterator<Variable> itrvar = emp.getVariables().listIterator();
					while (itrvar.hasNext())  {
						Variable v = itrvar.next();
						if (month.equalsIgnoreCase(v.getPayableMonth())) {
							variableIndicator = true;
							break;
						} else {
							itrvar.remove();
							variableIndicator = false;
						}
					}
				}
				if (emp.getTotalVariableAmt() != null && emp.getTotalVariableAmt() != BigDecimal.ZERO) {
					ListIterator<Bonus> itrbon = emp.getBonus().listIterator();
					while (itrbon.hasNext()) {
						Bonus b = itrbon.next();
						if (month.equalsIgnoreCase(b.getPayableMonth())) {
							bonusIndicator = true;
							break;
						} else {
							itrbon.remove();
							bonusIndicator = false;
						}
					}
				}
				if (variableIndicator || bonusIndicator) {
				} else {
					itr.remove();
				}
			}
		}
		// employees=employeeDao.getEmployee();
		return employees;

	}

	public void storeData(List<Employee> employees, List<Variable> variables, List<Bonus> bonuses) {
		variables.sort((Variable v1, Variable v2) -> v1.getEmployeeId() - v2.getEmployeeId());
		bonuses.sort((Bonus b1, Bonus b2) -> b1.getEmployeeId() - b2.getEmployeeId());
		for (Employee employee : employees) {
			List<Variable> var = new ArrayList<>();
			for (Variable variable : variables) {
				if (employee.getEmployeeId() == variable.getEmployeeId()) {
					var.add(variable);
				} else if (employee.getEmployeeId() > variable.getEmployeeId()) {
					continue;
				} else {
					break;
				}

			}
			employee.setVariables(var);
			List<Bonus> bon = new ArrayList<>();
			for (Bonus bonus : bonuses) {
				if (employee.getEmployeeId() == bonus.getEmployeeId()) {
					bon.add(bonus);
				} else if (employee.getEmployeeId() > bonus.getEmployeeId()) {
					continue;
				} else {
					break;
				}
			}
			employee.setBonus(bon);
		}
		payrollRepository.saveAll(employees);
	}

	@Override
	public void createEmployee(Employee employee) {
		// calculatePayableMonth(employee);
		try {
			employee.setDateOfJoining(new SimpleDateFormat("dd/MM/yyyy").parse(employee.getDoj()));
			employee.setPaymentCycle(new SimpleDateFormat("dd/MM/yyyy").parse(employee.getPaymentCyc()));
		} catch (Exception e) {
			logger.error("Exception in createEmployee " + e.getMessage());
		}
		payrollRepository.save(employee);
	}

	@Transactional
	@Override
	public void deleteEmployee(int employeeId) {
		Employee employee = payrollRepository.findById(employeeId).get();
		EmployeeHistory history = new EmployeeHistory();
		history.setEmployeeId(employee.getEmployeeId());
		history.setEmployeeName(employee.getEmployeeName());
		history.setDateOfJoining(employee.getDateOfJoining());
		history.setPaymentCycle(employee.getPaymentCycle());
		history.setFixedAmt(employee.getFixedAmt());
		history.setTotalVariableAmt(employee.getTotalVariableAmt());
		history.setVariableInstallments(employee.getVariableInstallments());
		if (employee.getVariables() != null && !employee.getVariables().isEmpty()) {
			List<Variable> variables = employee.getVariables();
			List<VariableHistory> varHistory = new ArrayList<>();
			for (Variable v : variables) {
				VariableHistory hist = new VariableHistory();
				hist.setVariableAmt(v.getVariableAmt());
				hist.setPayableMonth(v.getPayableMonth());
				varHistory.add(hist);
			}
			history.setVariables(varHistory);
		}
		history.setTotalBonusAmt(employee.getTotalBonusAmt());
		history.setBonusInstallments(employee.getBonusInstallments());
		if (employee.getBonus() != null && !employee.getBonus().isEmpty()) {
			List<Bonus> bonus = employee.getBonus();
			List<BonusHistory> bonHistory = new ArrayList<>();
			for (Bonus b : bonus) {
				BonusHistory bonhist = new BonusHistory();
				bonhist.setPayableMonth(b.getPayableMonth());
				bonhist.setBonusAmt(b.getBonusAmt());
				bonHistory.add(bonhist);
			}
			history.setBonus(bonHistory);
		}
		history.setMedicalInsurance(employee.getMedicalInsurance());
		history.setMonthlyAmt(employee.getMonthlyAmt());
		history.setTotalAmt(employee.getTotalAmt());
		payrollHisRepository.save(history);
		payrollRepository.deleteById(employeeId);
	}

	/*
	 * private void calculatePayableMonth(Employee employee) { String[] months = {
	 * "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
	 * "DEC" }; String dateofJoining = employee.getDateofJoining(); Date date =
	 * null; try { date = new SimpleDateFormat("dd/MM/yyyy").parse(dateofJoining); }
	 * catch (ParseException e) {
	 * logger.error("Exception in calculatePayableMonth "+e.getMessage()); }
	 * employee.setDOJ(date); Date DOJ = employee.getDOJ(); Calendar cal =
	 * Calendar.getInstance(); cal.setTime(DOJ); int day =
	 * cal.get(Calendar.DAY_OF_MONTH); int month = cal.get(Calendar.MONTH); int year
	 * = cal.get(Calendar.YEAR); int installments = employee.getInstallments();
	 * String type = employee.getType(); List<Variable> variables = new
	 * ArrayList<Variable>(); List<Bonus> bonuses = new ArrayList<Bonus>();
	 * 
	 * if (type != null) { if (installments == 0)
	 * System.out.println("installments cannot be empty");
	 * 
	 * if (type.equalsIgnoreCase("V")) { if (installments == 4) { if (month + 2 <=
	 * 11) { Variable v1 = new Variable(installments, months[month + 2]);
	 * variables.add(v1); } else if (month + 2 > 11) { int mon = (month + 2) - 12;
	 * Variable v1 = new Variable(installments, months[mon]); variables.add(v1); }
	 * if (month + 5 <= 11) { Variable v2 = new Variable(installments, months[month
	 * + 5]); variables.add(v2); } else if (month + 5 > 11) { int mon = (month + 5)
	 * - 12; Variable v2 = new Variable(installments, months[mon]);
	 * variables.add(v2); } if (month + 8 <= 11) { Variable v3 = new
	 * Variable(installments, months[month + 8]); variables.add(v3); } else if
	 * (month + 8 > 11) { int mon = (month + 8) - 12; Variable v3 = new
	 * Variable(installments, months[mon]); variables.add(v3); } if (month + 11 <=
	 * 11) { Variable v4 = new Variable(installments, months[month + 11]);
	 * variables.add(v4); } else if (month + 11 > 11) { int mon = (month + 11) - 12;
	 * Variable v4 = new Variable(installments, months[mon]); variables.add(v4); }
	 * employee.setVariables(variables); } else if (installments == 3) { if (month +
	 * 3 <= 11) { Variable v1 = new Variable(installments, months[month + 3]);
	 * variables.add(v1); } else if (month + 3 > 11) { int mon = (month + 3) - 12;
	 * Variable v1 = new Variable(installments, months[mon]); variables.add(v1); }
	 * if (month + 7 <= 11) { Variable v2 = new Variable(installments, months[month
	 * + 7]); variables.add(v2); } else if (month + 7 > 11) { int mon = (month + 7)
	 * - 12; Variable v2 = new Variable(installments, months[mon]);
	 * variables.add(v2); } if (month + 11 <= 11) { Variable v3 = new
	 * Variable(installments, months[month + 11]); variables.add(v3); } else if
	 * (month + 11 > 11) { int mon = (month + 11) - 12; Variable v3 = new
	 * Variable(installments, months[mon]); variables.add(v3); }
	 * employee.setVariables(variables); } else if (installments == 2) { if (month +
	 * 5 <= 11) { Variable v1 = new Variable(installments, months[month + 5]);
	 * variables.add(v1); } else if (month + 5 > 11) { int mon = (month + 5) - 12;
	 * Variable v1 = new Variable(installments, months[mon]); variables.add(v1); }
	 * if (month + 11 <= 11) { Variable v2 = new Variable(installments, months[month
	 * + 11]); variables.add(v2); } else if (month + 11 > 11) { int mon = (month +
	 * 11) - 12; Variable v2 = new Variable(installments, months[mon]);
	 * variables.add(v2); } employee.setVariables(variables); } } else if
	 * (type.equalsIgnoreCase("B")) { if (installments == 4) { if (month + 2 <= 11)
	 * { Bonus v1 = new Bonus(installments, months[month + 2]); bonuses.add(v1); }
	 * else if (month + 2 > 11) { int mon = (month + 2) - 12; Bonus v1 = new
	 * Bonus(installments, months[mon]); bonuses.add(v1); } if (month + 5 <= 11) {
	 * Bonus v2 = new Bonus(installments, months[month + 5]); bonuses.add(v2); }
	 * else if (month + 5 > 11) { int mon = (month + 5) - 12; Bonus v2 = new
	 * Bonus(installments, months[mon]); bonuses.add(v2); } if (month + 8 <= 11) {
	 * Bonus v3 = new Bonus(installments, months[month + 8]); bonuses.add(v3); }
	 * else if (month + 8 > 11) { int mon = (month + 8) - 12; Bonus v3 = new
	 * Bonus(installments, months[mon]); bonuses.add(v3); } if (month + 11 <= 11) {
	 * Bonus v4 = new Bonus(installments, months[month + 11]); bonuses.add(v4); }
	 * else if (month + 11 > 11) { int mon = (month + 11) - 12; Bonus v4 = new
	 * Bonus(installments, months[mon]); bonuses.add(v4); }
	 * employee.setBonus(bonuses); } else if (installments == 3) { if (month + 3 <=
	 * 11) { Bonus v1 = new Bonus(installments, months[month + 3]); bonuses.add(v1);
	 * } else if (month + 3 > 11) { int mon = (month + 3) - 12; Bonus v1 = new
	 * Bonus(installments, months[mon]); bonuses.add(v1); } if (month + 7 <= 11) {
	 * Bonus v2 = new Bonus(installments, months[month + 7]); bonuses.add(v2); }
	 * else if (month + 7 > 11) { int mon = (month + 7) - 12; Bonus v2 = new
	 * Bonus(installments, months[mon]); bonuses.add(v2); } if (month + 11 <= 11) {
	 * Bonus v3 = new Bonus(installments, months[month + 11]); bonuses.add(v3); }
	 * else if (month + 11 > 11) { int mon = (month + 11) - 12; Bonus v3 = new
	 * Bonus(installments, months[mon]); bonuses.add(v3); }
	 * employee.setBonus(bonuses); } else if (installments == 2) { if (month + 5 <=
	 * 11) { Bonus v1 = new Bonus(installments, months[month + 5]); bonuses.add(v1);
	 * } else if (month + 5 > 11) { int mon = (month + 5) - 12; Bonus v1 = new
	 * Bonus(installments, months[mon]); bonuses.add(v1); } if (month + 11 <= 11) {
	 * Bonus v2 = new Bonus(installments, months[month + 11]); bonuses.add(v2); }
	 * else if (month + 11 > 11) { int mon = (month + 11) - 12; Bonus v2 = new
	 * Bonus(installments, months[mon]); bonuses.add(v2); }
	 * employee.setBonus(bonuses); } } }
	 * 
	 * }
	 */
	@Override
	public void updateEmployee(Employee employee) {
		Employee emp = payrollRepository.findById(employee.getEmployeeId()).get();
		if (employee.getFixedAmt() != null && !employee.getFixedAmt().equals(BigDecimal.ZERO))
			emp.setFixedAmt(employee.getFixedAmt());
		if (employee.getTotalVariableAmt() != null && !employee.getTotalVariableAmt().equals(BigDecimal.ZERO))
			emp.setTotalVariableAmt(employee.getTotalVariableAmt());
		if (employee.getVariableInstallments() != 0)
			emp.setVariableInstallments(employee.getVariableInstallments());
		if (employee.getVariables() != null && !employee.getVariables().isEmpty()) {
			// emp.getVariables().clear();
			emp.getVariables().addAll(employee.getVariables());
		}
		if (employee.getTotalBonusAmt() != null && !employee.getTotalBonusAmt().equals(BigDecimal.ZERO))
			emp.setTotalBonusAmt(employee.getTotalBonusAmt());
		if (employee.getBonusInstallments() != 0)
			emp.setBonusInstallments(employee.getBonusInstallments());
		if (employee.getBonus() != null && !employee.getBonus().isEmpty()) {
			emp.getBonus().clear();
			emp.getBonus().addAll(employee.getBonus());
		}
		if (employee.getMedicalInsurance() != null && !employee.getMedicalInsurance().equals(BigDecimal.ZERO))
			emp.setMedicalInsurance(employee.getMedicalInsurance());
		if (employee.getMonthlyAmt() != null && !employee.getMonthlyAmt().equals(BigDecimal.ZERO))
			emp.setMonthlyAmt(employee.getMonthlyAmt());
		if (employee.getTotalAmt() != null && !employee.getTotalAmt().equals(BigDecimal.ZERO))
			emp.setTotalAmt(employee.getMonthlyAmt());
		payrollRepository.save(emp);
	}

}
