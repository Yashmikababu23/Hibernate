package com.fnf.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fnf.Response.ErrorObject;
import com.fnf.Response.ResponseObject;
import com.fnf.Response.StatusObject;
import com.fnf.model.Bonus;
import com.fnf.model.Employee;
import com.fnf.model.EmployeeHistory;
import com.fnf.model.Variable;
import com.fnf.service.IPayrollService;
import com.fnf.util.ExcelUtils;

@RestController
public class PayrollController {
	private static final Logger logger = LoggerFactory.getLogger(PayrollController.class);
	@Autowired
	IPayrollService payrollService;

	@GetMapping("/payroll/{type}")
	public ResponseEntity<?> getEmployees(@PathVariable("type") int type) {
		if (type == 0) {
			List<Employee> employees = payrollService.getEmployeeDetails();
			return new ResponseEntity<List<Employee>>(employees, HttpStatus.OK);

		} else if (type == 1) {
			List<EmployeeHistory> empHis = payrollService.getEmployeeHistoryDetails();
			return new ResponseEntity<List<EmployeeHistory>>(empHis, HttpStatus.OK);
		}
		ResponseObject response = new ResponseObject();
		ErrorObject error = new ErrorObject();
		error.setCode(404);
		error.setMessage("Employee Details Not Found");
		response.setError(error);
		return new ResponseEntity<ResponseObject>(response, HttpStatus.NOT_FOUND);
	}

	@GetMapping("/payroll/payrollByMonth/{month}")
	public ResponseEntity<?> getEmployeesByMonth(@PathVariable("month") String month) {
		List<Employee> employees = payrollService.getEmployeeByMonth(month);
		if (employees != null && !employees.isEmpty()) {
			return new ResponseEntity<List<Employee>>(employees, HttpStatus.OK);
		}
		ResponseObject response = new ResponseObject();
		ErrorObject error = new ErrorObject();
		error.setCode(404);
		error.setMessage("Employee Details Not Found");
		response.setError(error);
		return new ResponseEntity<ResponseObject>(response, HttpStatus.NOT_FOUND);
	}

	@PostMapping("/payroll/upload")
	public ResponseEntity<ResponseObject> uploadData(@RequestParam("file") MultipartFile file) {
		logger.info("inside the upload method");

		try {
			List<Employee> employees = ExcelUtils.readEmployeeSheetExcel(file);
			List<Variable> variables = ExcelUtils.readVariableSheetExcel(file);
			List<Bonus> bonuses = ExcelUtils.readBonusSheetExcel(file);
			payrollService.storeData(employees, variables, bonuses);
		} catch (Exception e) {
			ResponseObject response = new ResponseObject();
			ErrorObject error = new ErrorObject();
			error.setCode(417);
			error.setMessage("Exception occured while uploading the file");
			response.setError(error);
			logger.error("Exception in upload " + e.getMessage());
			return new ResponseEntity<ResponseObject>(response, HttpStatus.EXPECTATION_FAILED);
			

		}
		ResponseObject response = new ResponseObject();
		StatusObject status = new StatusObject();
		status.setCode(202);
		status.setMessage("File Successfully uploaded");
		response.setStatus(status);
		logger.info("File Successfully uploaded");
		return new ResponseEntity<ResponseObject>(response, HttpStatus.ACCEPTED);
	}

	@PostMapping("/payroll")
	public ResponseEntity<ResponseObject> addEmployee(@RequestBody Employee employee, UriComponentsBuilder ucBuilder) {

		logger.info("Inside addEmployee method");

		Employee emp = payrollService.getEmployeeById(employee.getEmployeeId());
		if (emp == null) {
			payrollService.createEmployee(employee);
		} else {
			ResponseObject response = new ResponseObject();
			ErrorObject error = new ErrorObject();
			error.setCode(409);
			error.setMessage("Employee details already exists please do update");
			response.setError(error);
			logger.error("Employee already Exists");
			return new ResponseEntity<ResponseObject>(response, HttpStatus.CONFLICT);
		}

		ResponseObject response = new ResponseObject();
		StatusObject status = new StatusObject();
		status.setCode(201);
		status.setMessage("Employee successfully added");
		response.setStatus(status);
		logger.info("Successfully Completed...");
		return new ResponseEntity<ResponseObject>(response, HttpStatus.CREATED);

	}

	@DeleteMapping("/payroll/{employeeId}")
	public ResponseEntity<ResponseObject> deleteEmployee(@PathVariable("employeeId") int employeeId) {
		logger.info("Inside deleteEmployee method");
		Employee emp = payrollService.getEmployeeById(employeeId);
		if (emp != null) {
			payrollService.deleteEmployee(employeeId);
		} else {
			ResponseObject response = new ResponseObject();
			ErrorObject error = new ErrorObject();
			error.setCode(404);
			error.setMessage("Employee details does not exists");
			response.setError(error);
			logger.error("Unable to delete.Employee with Id " + employeeId + " not found");
			return new ResponseEntity<ResponseObject>(response, HttpStatus.NOT_FOUND);
		}
		ResponseObject response = new ResponseObject();
		StatusObject status = new StatusObject();
		status.setCode(200);
		status.setMessage("Employee successfully deleted");
		response.setStatus(status);
		logger.info("Successfully Completed...");
		return new ResponseEntity<ResponseObject>(response, HttpStatus.OK);

	}

	@PutMapping("/payroll")
	public ResponseEntity<?> updateEmployee(@RequestBody Employee employee) {
		logger.info("Inside updateEmployee method");
		Employee emp = payrollService.getEmployeeById(employee.getEmployeeId());
		if (emp != null) {
			payrollService.updateEmployee(employee);
		} else {
			ResponseObject response = new ResponseObject();
			ErrorObject error = new ErrorObject();
			error.setCode(404);
			error.setMessage("Employee Id does not exists.Please do Add");
			response.setError(error);
			logger.error("Unable to update.Employee with Id " + employee.getEmployeeId() + " not found");
			;
			return new ResponseEntity<ResponseObject>(response, HttpStatus.NOT_FOUND);
		}
		logger.info("Successfully Completed...");
		return new ResponseEntity<Employee>(emp, HttpStatus.OK);
	}

}
