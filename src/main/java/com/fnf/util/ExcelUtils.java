package com.fnf.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.fnf.model.Bonus;
import com.fnf.model.Employee;
import com.fnf.model.Variable;

public class ExcelUtils {
	private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

	private static final String[] columns = new String[] { "EmployeeId", "EmployeeName", "Department", "DOJ", "CTC",
			"Type", "Installments", "Variable", "Bonus" };
	/*
	 * public static Resource writeExcel(List<Employee> employees) { Workbook
	 * workbook = new XSSFWorkbook(); CreationHelper createHelper =
	 * workbook.getCreationHelper(); Sheet sheet = workbook.createSheet("Employee");
	 * // create a font for styling header Font headerFont = workbook.createFont();
	 * headerFont.setBold(true); headerFont.setFontHeightInPoints((short) 14);
	 * headerFont.setColor(IndexedColors.RED.getIndex()); // create a cellstyle with
	 * font CellStyle headerCellStyle = workbook.createCellStyle();
	 * headerCellStyle.setFont(headerFont); // create row Row headerRow =
	 * sheet.createRow(0);
	 * 
	 * // Create cells for (int i = 0; i < columns.length; i++) { Cell cell =
	 * headerRow.createCell(i); cell.setCellValue(columns[i]);
	 * cell.setCellStyle(headerCellStyle); }
	 * 
	 * // Create Cell Style for formatting Date CellStyle dateCellStyle =
	 * workbook.createCellStyle();
	 * dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(
	 * "dd-MM-yyyy"));
	 * 
	 * // Create Other rows and cells with employees data int rowNum = 1; for
	 * (Employee employee : employees) { Row row = sheet.createRow(rowNum++);
	 * 
	 * row.createCell(0).setCellValue(employee.getEmployeeId());
	 * 
	 * row.createCell(1).setCellValue(employee.getEmployeeName());
	 * 
	 * row.createCell(2).setCellValue(employee.getDepartment());
	 * 
	 * Cell dateOfBirthCell = row.createCell(3);
	 * dateOfBirthCell.setCellValue(employee.getDOJ());
	 * dateOfBirthCell.setCellStyle(dateCellStyle); if(employee.getCtc()!=null)
	 * row.createCell(4).setCellValue(employee.getCtc().doubleValue());
	 * row.createCell(5).setCellValue(employee.getType());
	 * row.createCell(6).setCellValue(employee.getInstallments());
	 * row.createCell(7).setCellValue(employee.getVariableIndicator());
	 * row.createCell(8).setCellValue(employee.getBonusIndicator());
	 * 
	 * 
	 * // Resize all columns to fit the content size for (int i = 0; i <
	 * columns.length; i++) { sheet.autoSizeColumn(i); }
	 * 
	 * } FileOutputStream fileOut = null; try { fileOut = new
	 * FileOutputStream("D:\\poi-generated-file.xlsx"); workbook.write(fileOut); }
	 * catch (IOException e) {
	 * logger.error("Error in writeExcel method "+e.getMessage()); } finally { try {
	 * fileOut.close(); workbook.close(); } catch (IOException e) {
	 * logger.error("Error in writeExcel method "+e.getMessage()); } }
	 * 
	 * URL url = null; try { url = new URL("file:D:/poi-generated-file.xlsx"); }
	 * catch (MalformedURLException e) {
	 * logger.error("Error in writeExcel method "+e.getMessage()); } Resource
	 * resource = new UrlResource(url);
	 * 
	 * return resource;
	 * 
	 * }
	 */

	public static List<Employee> readEmployeeSheetExcel(MultipartFile file) throws Exception {
		List<Employee> employeeList = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
			Employee employee = new Employee();

			XSSFRow row = worksheet.getRow(i);

			employee.setEmployeeId((int) row.getCell(0).getNumericCellValue());
			employee.setEmployeeName(row.getCell(1).getStringCellValue());

			String dateofJoining = row.getCell(2).getStringCellValue();
			String paymentCycle = row.getCell(3).getStringCellValue();
			employee.setDateOfJoining(new SimpleDateFormat("yyyy-MM-dd").parse(dateofJoining));
			employee.setPaymentCycle(new SimpleDateFormat("yyyy-MM-dd").parse(paymentCycle));
			if (row.getCell(4) != null)
				employee.setFixedAmt(new BigDecimal(row.getCell(4).getNumericCellValue()));
			if (row.getCell(5) != null)
				employee.setTotalVariableAmt(new BigDecimal(row.getCell(5).getNumericCellValue()));
			if (row.getCell(6) != null)
				employee.setVariableInstallments((int) row.getCell(6).getNumericCellValue());
			if (row.getCell(7) != null)
				employee.setTotalBonusAmt(new BigDecimal(row.getCell(7).getNumericCellValue()));
			if (row.getCell(8) != null)
				employee.setBonusInstallments((int) row.getCell(8).getNumericCellValue());
			if (row.getCell(9) != null)
				employee.setMedicalInsurance(new BigDecimal(row.getCell(9).getNumericCellValue()));
			if (row.getCell(10) != null)
				employee.setMonthlyAmt(new BigDecimal(row.getCell(10).getNumericCellValue()));
			if (row.getCell(11) != null)
				employee.setTotalAmt(new BigDecimal(row.getCell(11).getNumericCellValue()));
			employeeList.add(employee);
		}
		workbook.close();
		return employeeList;
	}

	public static List<Variable> readVariableSheetExcel(MultipartFile file) throws IOException {
		List<Variable> variableList = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(1);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
			Variable variable = new Variable();

			XSSFRow row = worksheet.getRow(i);
			variable.setEmployeeId((int) row.getCell(0).getNumericCellValue());
			variable.setPayableMonth(row.getCell(1).getStringCellValue());
			variable.setVariableAmt(new BigDecimal(row.getCell(2).getNumericCellValue()));
			variableList.add(variable);
		}
		workbook.close();
		return variableList;
	}

	public static List<Bonus> readBonusSheetExcel(MultipartFile file) throws IOException {
		List<Bonus> bonusList = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(2);

		for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
			Bonus bonus = new Bonus();

			XSSFRow row = worksheet.getRow(i);
			bonus.setEmployeeId((int) row.getCell(0).getNumericCellValue());
			bonus.setPayableMonth(row.getCell(1).getStringCellValue());
			bonus.setBonusAmt(new BigDecimal(row.getCell(2).getNumericCellValue()));
			bonusList.add(bonus);
		}
		workbook.close();
		return bonusList;
	}

}
