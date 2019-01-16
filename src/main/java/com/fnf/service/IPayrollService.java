package com.fnf.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.fnf.model.Bonus;
import com.fnf.model.Employee;
import com.fnf.model.EmployeeHistory;
import com.fnf.model.Variable;

public interface IPayrollService {
	
	public void storeData(List<Employee> employees,List<Variable> variable,List<Bonus> bonuses);
	public void createEmployee(Employee employee);
	public void deleteEmployee(int employeeId);
	public void updateEmployee(Employee employee);
	public List<Employee> getEmployeeByMonth(String month);
	public Employee getEmployeeById(int employeeId);
	public List<Employee> getEmployeeDetails();
	public List<EmployeeHistory> getEmployeeHistoryDetails();
	
}
	