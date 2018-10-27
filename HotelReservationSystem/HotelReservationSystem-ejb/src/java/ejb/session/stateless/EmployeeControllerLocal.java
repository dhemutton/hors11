/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import exceptions.EmployeeExistException;
import java.util.List;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author matthealoo
 */
public interface EmployeeControllerLocal {
     public Employee createNewEmployee(Employee employee) throws EmployeeExistException;
    
    public Employee retrieveEmployeeByNric(String nric) throws EmployeeNotFoundException;
     
    public Employee retrieveEmployeeById(Long employeeId)throws EmployeeNotFoundException;
    
     public List<Employee> retrieveAllEmployee();

}
