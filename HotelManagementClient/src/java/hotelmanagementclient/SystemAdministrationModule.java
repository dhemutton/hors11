/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package hotelmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import entity.Employee;
import entity.Partner;
import exceptions.EmployeeExistException;
import exceptions.PartnerExistException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author matthealoo
 */
class SystemAdministratorModule {
    
    private PartnerControllerRemote partnerControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;
    
    public SystemAdministratorModule() {
    }
    
    public SystemAdministratorModule(PartnerControllerRemote partnerControllerRemote, EmployeeControllerRemote employeeControllerRemote) {
        this.partnerControllerRemote = partnerControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
    }
    
    public void runSystemAdminModule() throws EmployeeExistException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create new employee");
            System.out.println("2. View all employees");
            System.out.println("3. Create new partner");
            System.out.println("4. View all partners");
            System.out.println("5. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                doCreateEmployee();
            } else if (choice == 2) {
                doViewAllEmployees();
            } else if (choice == 3) {
                doCreatePartner();
            } else if (choice == 4) {
                doViewAllPartners();
            } else if (choice == 5) {
                break;
            } else {
                System.out.println("Invalid entry. Please try again");
            }
        }
    }
    
    private void doCreateEmployee() throws EmployeeExistException {
        
        try {
            Scanner scanner = new Scanner(System.in);
            Employee newEmployee= new Employee();
            
            System.out.println("*** HoRS :: System Administration :: Create New Employee ***\n");
            System.out.print("Enter First Name> ");
            newEmployee.setFirstName(scanner.nextLine().trim());
            System.out.print("Enter Last Name> ");
            newEmployee.setLastName(scanner.nextLine().trim());
            
            
            System.out.print("Enter NRIC> ");
            newEmployee.setNric(scanner.nextLine().trim());
            System.out.print("Enter password> ");
            newEmployee.setPassword(scanner.nextLine().trim());
            newEmployee.setIsLogin(Boolean.FALSE);
            newEmployee = employeeControllerRemote.createNewEmployee(newEmployee);
            System.out.println("New employee " + newEmployee.getFirstName() + " "+ newEmployee.getLastName()+ " was created successfully!" + "\n");
        } catch (EmployeeExistException ex) {
            System.out.println("An error has occurred while creating the new employee: " + ex.getMessage() + "!\n");
            
        }
        
    }
    
    private void doViewAllEmployees() {
        System.out.println("*** HoRS :: System Administration :: View All Employees ***\n");
        
        List<Employee> listOfEmployees = employeeControllerRemote.retrieveAllEmployee();
        if (listOfEmployees.size() == 0) {
            System.out.println("No employees to view");
        } else {
            for (Employee employee: listOfEmployees) {
                System.out.println("Id: " + employee.getEmployeeId() + " Name: " + employee.getFirstName() + " " + employee.getLastName() + "/n");
            }
        }
    }
    
    private void doCreatePartner() {
        try {
            Scanner scanner = new Scanner(System.in);
            Partner newPartner= new Partner();
            
            System.out.println("*** HoRS :: System Administration :: Create New Partner ***\n");
            System.out.print("Enter Username> ");
            newPartner.setUsername(scanner.nextLine().trim());
            System.out.print("Enter Password> ");
            newPartner.setPassword(scanner.nextLine().trim());
            
            newPartner.setIsLogin(Boolean.FALSE);
            newPartner = partnerControllerRemote.createNewPartner(newPartner);
            System.out.println("New partner " + newPartner.getUsername() + " was created successfully!" + "\n");
        } catch (PartnerExistException ex) {
            System.out.println("An error has occurred while creating the new partner: " + ex.getMessage() + "!\n");
            
        }
    }
    
    private void doViewAllPartners() {
        System.out.println("*** HoRS :: System Administration :: View All Partners ***\n");
        
        List<Partner> listOfPartners = partnerControllerRemote.retrieveAllPartner();
        if (listOfPartners.size() == 0) {
            System.out.println("No partners to view");
        } else {
            for (Partner partner: listOfPartners) {
                System.out.println("Id: " + partner.getPartnerId()+ " Username: " + partner.getUsername()+ "/n");
            }
        }
    }
}