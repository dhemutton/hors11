/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Remote;

/**
 *
 * @author matthealoo
 */
@Remote
public interface EmployeeControllerRemote {
    
    public Employee createNewEmployee(Employee employee);
    
    public Employee retrieveEmployeeByNric(String nric);
    
    public Employee retrieveEmployeeById(Long employeeId);

    
}
