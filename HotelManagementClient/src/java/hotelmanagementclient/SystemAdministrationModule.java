/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
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

    public void runSystemAdminModule() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Create new employee");
            System.out.println("2. View all employees");
            System.out.println("3. Creeat new partner");
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

    private void doCreateEmployee() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllEmployees() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCreatePartner() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllPartners() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}