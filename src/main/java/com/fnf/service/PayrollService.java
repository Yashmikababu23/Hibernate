package com.fnf.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	public Employee getEmployeeById(int employeeId) {
		return payrollRepository.findById(employeeId).orElse(null);
	}

	public List<Employee> getEmployeeDetails() {

		List<Employee> employees = (List<Employee>) payrollRepository.findAll();
		for (Employee employee : employees) {
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			int currentMonth = cal.get(Calendar.MONTH);
			Date payCyc = employee.getPaymentCycle();
			Calendar calc = Calendar.getInstance();
			calc.setTime(payCyc);
			int payMonth = calc.get(Calendar.MONTH);
			Date doj = employee.getDateOfJoining();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(doj);
			int joiningMonth = calendar.get(Calendar.MONTH);

			if (currentMonth == payMonth) {

				if (joiningMonth == payMonth) {
					// calculate diff btw doj and paycyc
					int days = Days.daysBetween(new LocalDate(doj.getTime()), new LocalDate(payCyc.getTime())).getDays()
							+ 1;
					// calculate no of days in paymonth
					int payMonthDays = calc.getActualMaximum(Calendar.DAY_OF_MONTH);
					// calcualte the salary to be paid for that month
					float dueSalary = (employee.getMonthlyAmt().floatValue() / payMonthDays) * days;
					BigDecimal monthAmt = new BigDecimal(dueSalary);
					monthAmt.setScale(3, BigDecimal.ROUND_HALF_UP);
					employee.setMonthlyAmt(monthAmt);

				} else if (joiningMonth + 1 == payMonth) {
					// calculate no of days in paymonth
					int payMonthDays = calc.getActualMaximum(Calendar.DAY_OF_MONTH);
					// calculate no of days in join month
					int joinMonthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
					// calculate the no of days btw doj and payday
					int days = Days.daysBetween(new LocalDate(doj.getTime()), new LocalDate(payCyc.getTime())).getDays()
							+ 1;
					// no of days btw doj and payday - no of days in the payable month
					int dueDays = days - payMonthDays;
					// calculate the amount to be paid for the joining month
					float dueSalary = (employee.getMonthlyAmt().floatValue() / joinMonthDays) * dueDays;

					// calculate total amount to be paid for payable month
					float totalSalary = dueSalary + employee.getMonthlyAmt().floatValue();
					BigDecimal monthAmt = new BigDecimal(totalSalary);
					monthAmt.setScale(3, BigDecimal.ROUND_HALF_UP);
					employee.setMonthlyAmt(monthAmt);

				}

			}

		}
		return (List<Employee>) payrollRepository.findAll();
	}

	public List<EmployeeHistory> getEmployeeHistoryDetails() {
		return (List<EmployeeHistory>) payrollHisRepository.findAll();
	}

	public List<Employee> getEmployeeByMonth(String month) {
		List<Employee> employees = new ArrayList<>();
		employees = payrollRepository.findEmpByMonth();
		if (employees != null) {
			ListIterator<Employee> itr = employees.listIterator();
			while (itr.hasNext()) {
				boolean variableIndicator = false;
				boolean bonusIndicator = false;
				Employee emp = (Employee) itr.next();
				calculateMonth(emp);
				if (emp.getTotalVariableAmt() != null && emp.getTotalVariableAmt() != BigDecimal.ZERO) {
					ListIterator<Variable> itrvar = emp.getVariables().listIterator();
					while (itrvar.hasNext()) {
						Variable v = itrvar.next();
						if (month.equalsIgnoreCase(v.getPayableMonth())) {
							variableIndicator = true;
						} else {
							itrvar.remove();
						}
					}
				}
				if (emp.getTotalBonusAmt() != null && emp.getTotalBonusAmt() != BigDecimal.ZERO) {
					ListIterator<Bonus> itrbon = emp.getBonus().listIterator();
					while (itrbon.hasNext()) {
						Bonus b = itrbon.next();
						if (month.equalsIgnoreCase(b.getPayableMonth())) {
							bonusIndicator = true;
						} else {
							itrbon.remove();
						}
					}
				}
				if (variableIndicator || bonusIndicator) {
				} else {
					itr.remove();
				}
			}
		}
		return employees;

	}
private void calculateMonth(Employee employee) {

	Date today = new Date();
	Calendar cal = Calendar.getInstance();
	cal.setTime(today);
	int currentMonth = cal.get(Calendar.MONTH);
	Date payCyc = employee.getPaymentCycle();
	Calendar calc = Calendar.getInstance();
	calc.setTime(payCyc);
	int payMonth = calc.get(Calendar.MONTH);
	Date doj = employee.getDateOfJoining();
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(doj);
	int joiningMonth = calendar.get(Calendar.MONTH);

	if (currentMonth == payMonth) {

		if (joiningMonth == payMonth) {
			// calculate diff btw doj and paycyc
			int days = Days.daysBetween(new LocalDate(doj.getTime()), new LocalDate(payCyc.getTime())).getDays()
					+ 1;
			// calculate no of days in paymonth
			int payMonthDays = calc.getActualMaximum(Calendar.DAY_OF_MONTH);
			// calcualte the salary to be paid for that month
			float dueSalary = (employee.getMonthlyAmt().floatValue() / payMonthDays) * days;
			BigDecimal monthAmt = new BigDecimal(dueSalary);
			monthAmt.setScale(3, BigDecimal.ROUND_HALF_UP);
			employee.setMonthlyAmt(monthAmt);

		} else if (joiningMonth + 1 == payMonth) {
			// calculate no of days in paymonth
			int payMonthDays = calc.getActualMaximum(Calendar.DAY_OF_MONTH);
			// calculate no of days in join month
			int joinMonthDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			// calculate the no of days btw doj and payday
			int days = Days.daysBetween(new LocalDate(doj.getTime()), new LocalDate(payCyc.getTime())).getDays()
					+ 1;
			// no of days btw doj and payday - no of days in the payable month
			int dueDays = days - payMonthDays;
			// calculate the amount to be paid for the joining month
			float dueSalary = (employee.getMonthlyAmt().floatValue() / joinMonthDays) * dueDays;

			// calculate total amount to be paid for payable month
			float totalSalary = dueSalary + employee.getMonthlyAmt().floatValue();
			BigDecimal monthAmt = new BigDecimal(totalSalary);
			monthAmt.setScale(3, BigDecimal.ROUND_HALF_UP);
			employee.setMonthlyAmt(monthAmt);

		}

	}


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
