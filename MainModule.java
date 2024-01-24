package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import entity.DatabaseContext;
import entity.Employee;
import entity.EmployeeService;
import entity.FinancialRecord;
import entity.FinancialRecordService;
import entity.Payroll;
import entity.PayrollService;
import entity.Tax;
import entity.TaxService;
import exception.EmployeeNotFoundException;

public class MainModule {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Connection con = null;
        PreparedStatement p = null;
        ResultSet rs = null;

        con = DatabaseContext.getConnection();

        try {
            String sql = "select employeeid, firstname, lastname from employee";
            p = con.prepareStatement(sql);
            rs = p.executeQuery();
			System.out.println("Welcome to PayXpert");
            System.out.println("List of Employees");
            System.out.println("ID\t\tName");

            while (rs.next()) {
                int id = rs.getInt("employeeid");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                System.out.println(id + "\t\t" + firstName + " " + lastName);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Employee Service");
            System.out.println("2. Payroll Service");
            System.out.println("3. Tax Service");
            System.out.println("4. Financial Record Service");
            System.out.println("5. Exit");

            System.out.print("Enter your choice (1-5): ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    handleEmployeeService();
                    break;

                case 2:
                    handlePayrollService();
                    break;

                case 3:
                    handleTaxService();
                    break;

                case 4:
                    handleFinancialRecordService();
                    break;

                case 5:
                    System.out.println("Exiting the PayXpert App - Thank you!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Choose a valid option (1-5).");
            }
        }
    }

    private static void handleEmployeeService() {
        EmployeeService employeeService = new EmployeeService();

        System.out.println("\nEmployee Service Options:");
        System.out.println("1. Add Employee");
        System.out.println("2. Edit Employee Details");
        System.out.println("3. Remove Employee");
        System.out.println("4. List of Employees");
        System.out.println("5. Get Employee Details");
        System.out.println("6. Go back to main menu");

        System.out.print("Enter your choice (1-6): ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                addEmployee(employeeService);
                break;

            case 2:
                editEmployeeDetails(employeeService);
                break;

            case 3:
                removeEmployee(employeeService);
                break;

            case 4:
                listEmployees(employeeService);
                break;

            case 5:
                getEmployeeDetails(employeeService);
                break;

            case 6:
                break;

            default:
                System.out.println("Invalid choice. Choose a valid option (1-6).");
        }
    }

    private static void addEmployee(EmployeeService employeeService) {
        System.out.print("Enter First Name: ");
        String firstName = sc.next();
        System.out.print("Enter Last Name: ");
        String lastName = sc.next();
        System.out.print("Enter Date of birth(YYYY-MM-DD): ");
		String dateOfBirthStr = sc.next();
		LocalDate dateOfBirth = LocalDate.parse(dateOfBirthStr);
        System.out.print("Enter Gender(M/F): ");
		String genderStr = sc.next();
		char gender = ' ';
        if (genderStr.length() == 1) {
			gender = genderStr.charAt(0);
		}
        else {
            System.out.println("Invalid input. Please enter a single character.");
        }
        System.out.print("Enter Email: ");
		String email = sc.next();
        System.out.print("Enter Phone Number: ");
		String phoneNumber = sc.next();
        System.out.print("Enter Address: ");
		String address = sc.next();
        System.out.print("Enter Position: ");
		String position = sc.next();
        System.out.print("Enter Joining Date(YYYY-MM-DD): ");
		String joiningDateStr = sc.next();
		LocalDate joiningDate = LocalDate.parse(joiningDateStr);
        System.out.print("Enter Termination Date(YYYY-MM-DD): ");
		String terminationDateStr = sc.next();
		LocalDate terminationDate = LocalDate.now();
		if (terminationDateStr.equalsIgnoreCase("null")) {
            terminationDateStr = null;
        }
		else{
			terminationDate = LocalDate.parse(terminationDateStr);
		}

		Employee newEmployee = new Employee();
		newEmployee.setFirstName(firstName);
		newEmployee.setLastName(lastName);
		newEmployee.setDateOfBirth(dateOfBirth);
		newEmployee.setGender(gender);
		newEmployee.setAddress(address);
		newEmployee.setEmail(email);
		newEmployee.setJoiningDate(joiningDate);
		newEmployee.setPhoneNumber(phoneNumber);
		newEmployee.setPosition(position);
		newEmployee.setTerminationDate(terminationDate);
		employeeService.addEmployee(newEmployee);
		try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "INSERT INTO employee (FirstName, LastName, DateOfBirth, Gender, Email, PhoneNumber, Address, Position, JoiningDate, TerminationDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, newEmployee.getFirstName());
                preparedStatement.setString(2, newEmployee.getLastName());
                preparedStatement.setDate(3, Date.valueOf(newEmployee.getDateOfBirth()));
                preparedStatement.setString(4, String.valueOf(newEmployee.getGender()));
                preparedStatement.setString(5, newEmployee.getEmail());
                preparedStatement.setString(6, newEmployee.getPhoneNumber());
                preparedStatement.setString(7, newEmployee.getAddress());
                preparedStatement.setString(8, newEmployee.getPosition());
                preparedStatement.setDate(9, Date.valueOf(newEmployee.getJoiningDate()));
                preparedStatement.setDate(10, Date.valueOf(newEmployee.getTerminationDate()));
				if (terminationDateStr != null) {
					preparedStatement.setDate(10, Date.valueOf(newEmployee.getTerminationDate()));
				} else {
					preparedStatement.setNull(10, Types.DATE);
				}
                preparedStatement.executeUpdate();
                System.out.println("Employee added successfully to the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error adding employee to the database.");
        }
        // System.out.println("Employee added successfully.");
    }

    private static void editEmployeeDetails(EmployeeService employeeService) {
		System.out.print("Enter employee ID: ");
        int employeeId = sc.nextInt();
        sc.nextLine();
        Employee updatedEmployee = new Employee();

        try {
            employeeService.updateEmployee(employeeId, updatedEmployee);
            System.out.println("Employee details updated successfully.");
        } catch (EmployeeNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeEmployee(EmployeeService employeeService) {
        System.out.print("Enter employee ID to remove: ");
        int employeeId = sc.nextInt();
        sc.nextLine();
		employeeService.removeEmployee(employeeId);

		try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "DELETE FROM Employee WHERE EmployeeID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Employee with EmployeeID " + employeeId + " removed successfully.");
                } else {
                    System.out.println("No employee found with EmployeeID " + employeeId + ". No changes made.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error removing employee from the database.");
        }

    }

    private static void listEmployees(EmployeeService employeeService) {
        
        System.out.println("List of Employees:");
		try(Connection connection = DatabaseContext.getConnection()) {
            String sql = "select employeeid, firstname, lastname from employee";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
			ResultSet rs = preparedStatement.executeQuery();

            System.out.println("ID\t\tName");

            while (rs.next()) {
                int id = rs.getInt("employeeid");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                System.out.println(id + "\t\t" + firstName + " " + lastName);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
	}

    private static void getEmployeeDetails(EmployeeService employeeService) {
        System.out.print("Enter employee ID: ");
        int employeeId = sc.nextInt();
        sc.nextLine();
		try(Connection connection = DatabaseContext.getConnection()) {
            String sql = "select * from employee where employeeId = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, employeeId);
			ResultSet rs = preparedStatement.executeQuery();


            while (rs.next()) {
                int id = rs.getInt("employeeid");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
				String dateOfBirth = rs.getString("dateOfBirth");
				String gender = rs.getString("gender");
				String email = rs.getString("email");
				String phoneNumber = rs.getString("phoneNumber");
				String address = rs.getString("address");
				String position = rs.getString("position");
				String joiningDate = rs.getString("joiningDate");
				String terminationDate = rs.getString("terminationDate");
				// System.out.println("");
                // System.out.println(id + "\t" + firstName + " " + lastName + "\t" + dateOfBirth + "\t" + gender + "\t" + email + "\t" + phoneNumber + "\t" + address + "\t" + position + "\t" + joiningDate + "\t" + terminationDate);

				System.out.println("ID: " + id);
				System.out.println("Name: " + firstName+" "+lastName);
				System.out.println("Date of Birth: " + dateOfBirth);
                System.out.println("Gender: " + gender);
                System.out.println("Email: " + email);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Address: " + address);
                System.out.println("Position: " + position);
                System.out.println("Joining Date: " + joiningDate);
                System.out.println("Termination Date: " + terminationDate);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static void handlePayrollService() {
        PayrollService payrollService = new PayrollService();
		System.out.println("\nPayroll Service Options:");
        System.out.println("1. Generate Payroll");
        System.out.println("2. Get Payroll");
        System.out.println("3. Get Payrolls of Employee");
        System.out.println("4. Get payrolls of Custom Date");
        System.out.println("5. Go back to main menu");

        System.out.print("Enter your choice (1-5): ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                generatePayroll(payrollService);
                break;

            case 2:
                getPayrollById(payrollService);
                break;

            case 3:
                getPayrollsForEmployee(payrollService);
                break;

            case 4:
                getPayrollsForPeriod(payrollService);
                break;

            case 5:
                break;
				
				
            default:
			System.out.println("Invalid choice. Choose a valid option (1-6).");
        }
    }
	
	
	
	private static void generatePayroll(PayrollService payrollService) {
		System.out.print("Enter employee ID: ");
        int employeeId = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDateStr = sc.nextLine();
        LocalDate startDate = LocalDate.parse(startDateStr);
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDateStr = sc.nextLine();
        LocalDate endDate = LocalDate.parse(endDateStr);
		System.out.print("Enter Basic Salary: ₹");
		double basicSalary = sc.nextDouble();
		System.out.print("Enter Overtime Pay: ₹");
		double overtimePay = sc.nextDouble();
		System.out.print("Enter Deductions: ₹");
		double deductions = sc.nextDouble();
		double netSalary = basicSalary + overtimePay - deductions;
        Payroll payroll = new Payroll(basicSalary, overtimePay, deductions, netSalary);
        payroll.setBasicSalary(basicSalary);
        payroll.setOvertimePay(overtimePay);
        payroll.setDeductions(deductions);
        payroll.setNetSalary(netSalary);
        payrollService.generatePayroll(employeeId, startDate, endDate);
		try (Connection connection = DatabaseContext.getConnection()) {
            String sql = "INSERT INTO Payroll (EmployeeID, PayPeriodStartDate, PayPeriodEndDate, BasicSalary, OvertimePay, Deductions, NetSalary) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, employeeId);
                preparedStatement.setDate(2, Date.valueOf(startDate));
                preparedStatement.setDate(3, Date.valueOf(endDate));
                preparedStatement.setDouble(4, basicSalary);
                preparedStatement.setDouble(5, overtimePay);
                preparedStatement.setDouble(6, deductions);
                preparedStatement.setDouble(7, netSalary);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Payroll details stored in the database for employee ID " + employeeId +
                            " for the period from " + startDate + " to " + endDate);
                } else {
                    System.out.println("Failed to store payroll details in the database for employee ID " + employeeId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error storing payroll details in the database.");
        }
    }
    
	private static void getPayrollById(PayrollService payrollService) {
		System.out.print("Enter Payroll ID: ");
		int payrollId = sc.nextInt();
		Payroll payroll = payrollService.getPayrollById(payrollId);

        if (payroll != null) {
            System.out.println("Payroll Details for PayrollID " + payrollId + ":");
            System.out.println("EmployeeID: " + payroll.getEmployeeID());
            System.out.println("Pay Period Start Date: " + payroll.getPayPeriodStartDate());
            System.out.println("Pay Period End Date: " + payroll.getPayPeriodEndDate());
            System.out.println("Basic Salary: ₹" + payroll.getBasicSalary());
            System.out.println("Overtime Pay: ₹" + payroll.getOvertimePay());
            System.out.println("Deductions: ₹" + payroll.getDeductions());
            System.out.println("Net Salary: ₹" + payroll.getNetSalary());
        } else {
            System.out.println("Payroll with PayrollID " + payrollId + " not found.");
        }

	}
	private static void getPayrollsForEmployee(PayrollService payrollService) {
		System.out.print("Enter Employee ID: ");
        int employeeId = sc.nextInt();
		List<Payroll> payrolls = payrollService.getPayrollsForEmployee(employeeId);
		if (!payrolls.isEmpty()) {
            System.out.println("Payrolls for Employee ID " + employeeId + ":");
			System.out.println("----------------------------");
            for (Payroll payroll : payrolls) {
                System.out.println("Payroll ID: " + payroll.getPayrollID());
                System.out.println("Start Date: " + payroll.getPayPeriodStartDate());
                System.out.println("End Date: " + payroll.getPayPeriodEndDate());
                System.out.println("Basic Salary: ₹" + payroll.getBasicSalary());
                System.out.println("Overtime Pay: ₹" + payroll.getOvertimePay());
                System.out.println("Deductions: ₹" + payroll.getDeductions());
                System.out.println("Net Salary: ₹" + payroll.getNetSalary());
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No payrolls found for Employee ID " + employeeId);
        }
	}
	private static void getPayrollsForPeriod(PayrollService payrollService) {
		System.out.print("Enter Start Date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(sc.nextLine());

        System.out.print("Enter End Date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(sc.nextLine());

        List<Payroll> payrolls = payrollService.getPayrollsForPeriod(startDate, endDate);

        if (!payrolls.isEmpty()) {
			System.out.println("Payrolls for the period from " + startDate + " to " + endDate + ":");
			System.out.println("----------------------------");
            for (Payroll payroll : payrolls) {
                System.out.println("Payroll ID: " + payroll.getPayrollID());
                System.out.println("Employee ID: " + payroll.getEmployeeID());
                System.out.println("Start Date: " + payroll.getPayPeriodStartDate());
                System.out.println("End Date: " + payroll.getPayPeriodEndDate());
                System.out.println("Basic Salary: ₹" + payroll.getBasicSalary());
                System.out.println("Overtime Pay: ₹" + payroll.getOvertimePay());
                System.out.println("Deductions: ₹" + payroll.getDeductions());
                System.out.println("Net Salary: ₹" + payroll.getNetSalary());
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No payrolls found for the period from " + startDate + " to " + endDate);
        }
	}


    private static void handleTaxService() {
        TaxService taxService = new TaxService();
		System.out.println("\nTax Service Options:");
        System.out.println("1. Calculate Tax");
        System.out.println("2. Get Tax");
        System.out.println("3. Get Tax details of Employee");
        System.out.println("4. Get Tax Records of a specific year");
        System.out.println("5. Go back to main menu");

        System.out.print("Enter your choice (1-5): ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                calculateTax(taxService);
                break;

            case 2:
                getTaxById(taxService);
                break;

            case 3:
                getTaxesForEmployee(taxService);
                break;

            case 4:
                getTaxesForYear(taxService);
                break;

            case 5:
                break;
				
				
            default:
			System.out.println("Invalid choice. Choose a valid option (1-6).");
        }
    }

	private static void calculateTax(TaxService taxService) {
		System.out.print("Enter employee ID: ");
        int employeeId = sc.nextInt();

        System.out.print("Enter tax year: ");
        int taxYear = sc.nextInt();

        double calculatedTax = taxService.calculateTax(employeeId, taxYear);

        System.out.println("Calculated Tax for employee ID " + employeeId +
                " for the tax year " + taxYear + ": " + calculatedTax);

	}

	private static void getTaxById(TaxService taxService) {
		System.out.print("Enter Tax Id: ");
		int taxId = sc.nextInt();
		Tax tax = taxService.getTaxById(taxId);
		if (tax != null) {
			System.out.println("Tax Details: " + tax.toString());
		} else {
			System.out.println("Tax not found with the given ID.");
		}

	}

	private static void getTaxesForEmployee(TaxService taxService) {
		System.out.print("Enter Employee ID: ");
		int employeeId = sc.nextInt();
		List<Tax> taxes = taxService.getTaxesForEmployee(employeeId);
		if (!taxes.isEmpty()) {
			System.out.println("Taxes for Employee ID " + employeeId + ":");
			for (Tax tax : taxes) {
				System.out.println(tax.toString());
			}
		} else {
			System.out.println("No taxes found for the given Employee ID.");
		}

	}

	private static void getTaxesForYear(TaxService taxService) {
		System.out.print("Enter Tax Year: ");
		int taxYear = sc.nextInt();
		// TaxService taxService = new TaxService();
		List<Tax> taxesFor2023 = taxService.getTaxesForYear(taxYear);

		for (Tax tax : taxesFor2023) {
			System.out.println(tax.toString());
		}

	}

	private static void handleFinancialRecordService() {
		FinancialRecordService financialRecordService = new FinancialRecordService();
        System.out.println("Financial Record Service Options:");
        System.out.println("1. Add Financial Record");
        System.out.println("2. Get Financial Record by ID");
        System.out.println("3. Get Financial Records for Employee");
        System.out.println("4. Get Financial Records for Date");

        System.out.print("Enter your choice (1-4): ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                addFinancialRecord(financialRecordService);
                break;

            case 2:
                getFinancialRecordById(financialRecordService);
                break;

            case 3:
                getFinancialRecordsForEmployee(financialRecordService);
                break;

            case 4:
                getFinancialRecordsForDate(financialRecordService);
                break;

			case 5:
                break;
				
            default:
                System.out.println("Invalid choice for Financial Record Service.");
        }
    }

    private static void addFinancialRecord(FinancialRecordService financialRecordService) {
		System.out.print("Enter Employee ID: ");
        int employeeId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Description: ");
        String description = sc.nextLine();

        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();

        System.out.print("Enter Record Type: ");
        String recordType = sc.next();

        financialRecordService.addFinancialRecord(employeeId, description, amount, recordType);
    }


    private static void getFinancialRecordById(FinancialRecordService financialRecordService) { 
		System.out.print("Enter Record ID: ");
        int recordId = sc.nextInt();
        FinancialRecord financialRecord = financialRecordService.getFinancialRecordById(recordId);
        if (financialRecord != null) {
            System.out.println("Financial Record Details:");
            System.out.println("ID: " + financialRecord.getRecordID());
            System.out.println("Employee ID: " + financialRecord.getEmployeeID());
            System.out.println("Description: " + financialRecord.getDescription());
            System.out.println("Amount: " + financialRecord.getAmount());
            System.out.println("Record Type: " + financialRecord.getRecordType());
            System.out.println("Record Date: " + financialRecord.getRecordDate());
        } else {
            System.out.println("Financial Record not found for ID: " + recordId);
        }
    }

    private static void getFinancialRecordsForEmployee(FinancialRecordService financialRecordService) {
        System.out.print("Enter Employee ID: ");
        int employeeId = sc.nextInt();
        List<FinancialRecord> financialRecords = financialRecordService.getFinancialRecordsForEmployee(employeeId);
        if (!financialRecords.isEmpty()) {
            System.out.println("Financial Records for Employee ID " + employeeId + ":");
			System.out.println("----------------------------");
            for (FinancialRecord financialRecord : financialRecords) {
                System.out.println("ID: " + financialRecord.getRecordID());
                System.out.println("Description: " + financialRecord.getDescription());
                System.out.println("Amount: " + financialRecord.getAmount());
                System.out.println("Record Type: " + financialRecord.getRecordType());
                System.out.println("Record Date: " + financialRecord.getRecordDate());
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No financial records found for Employee ID: " + employeeId);
        }
    }

    private static void getFinancialRecordsForDate(FinancialRecordService financialRecordService) {
		System.out.print("Enter Record Date (YYYY-MM-DD): ");
        String dateString = sc.next();
        Date recordDate = Date.valueOf(dateString);
        List<FinancialRecord> financialRecords = financialRecordService.getFinancialRecordsForDate(recordDate);
        if (!financialRecords.isEmpty()) {
            System.out.println("Financial Records for Date " + recordDate + ":");
			System.out.println("----------------------------");
            for (FinancialRecord financialRecord : financialRecords) {
                System.out.println("ID: " + financialRecord.getRecordID());
                System.out.println("Employee ID: " + financialRecord.getEmployeeID());
                System.out.println("Description: " + financialRecord.getDescription());
                System.out.println("Amount: " + financialRecord.getAmount());
                System.out.println("Record Type: " + financialRecord.getRecordType());
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No financial records found for Date: " + recordDate);
        }
    
    }
}
