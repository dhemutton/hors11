/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import exceptions.EmployeeExistException;
import exceptions.InvalidLoginCredentials;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author matthealoo
 */
@Stateless
@Local(EmployeeControllerLocal.class)
@Remote(EmployeeControllerRemote.class)

public class EmployeeController implements EmployeeControllerRemote, EmployeeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Employee createNewEmployee(Employee employee) throws EmployeeExistException {
        try {
            em.persist(employee);
            em.flush();

            return employee;
        } catch (PersistenceException ex) {

            throw new EmployeeExistException("Employee with same NRIC already exists");
        }
    }

    @Override
    public Employee retrieveEmployeeByNric(String nric) throws EmployeeNotFoundException {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.nric = :innric");
        query.setParameter("innric", nric);

        try {
            return (Employee) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new EmployeeNotFoundException("Employee " + nric + " does not exist!");
        }
    }

    @Override
    public Employee retrieveEmployeeById(Long employeeId) throws EmployeeNotFoundException {
        Employee employee = em.find(Employee.class, employeeId);

        if (employee != null) {
            return employee;
        } else {
            throw new EmployeeNotFoundException("Employee ID " + employeeId + " does not exist");
        }
    }

    @Override
    public List<Employee> retrieveAllEmployee() {
        Query query = em.createQuery("SELECT e FROM Employee e ORDER BY e.employeeId ASC");

        return query.getResultList();
    }

    @Override
    public Employee employeeLogin(String nric, String password) throws InvalidLoginCredentials, EmployeeNotFoundException {

        Employee employeeEntity;
        try {
            employeeEntity = retrieveEmployeeByNric(nric);
        } catch (EmployeeNotFoundException ex) {

            throw new EmployeeNotFoundException("Employee NRIC " + nric + " does not exist");
        }
            if (employeeEntity.getPassword().equals(password)) {
                return employeeEntity;
            }
            throw new InvalidLoginCredentials("Invalid password!");
     }

}
