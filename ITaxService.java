package dao;

import java.util.List;
import entity.Tax;

public interface ITaxService {
    double calculateTax(int employeeId, int taxYear);
    Tax getTaxById(int taxId);
    List<Tax> getTaxesForEmployee(int employeeId);
    List<Tax> getTaxesForYear(int taxYear);
}
