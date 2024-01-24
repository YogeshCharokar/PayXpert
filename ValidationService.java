package entity;

import java.util.Date;

public class ValidationService {
    public static boolean isNotEmpty(String name) {
        return name != null && !name.trim().isEmpty();
    }
    
    public static boolean isPositiveNumber(double number) {
        return number > 0;
    }

    public static boolean isValidEmployeeData(String name, double salary) {
        return isNotEmpty(name) && isPositiveNumber(salary);
    }

    public static boolean validRecordId(int recordId) {
        return recordId > 0;
    }
    public static boolean validEmployeeData(String name, double salary) {
        return isNotEmpty(name) && isPositiveNumber(salary);
    }
    public static boolean validEmployeeId(int employeeId) {
        return employeeId > 0;
    }

    public static boolean validRecordDate(Date recordDate) {
        return recordDate != null && !recordDate.after(new Date());
    }
}
