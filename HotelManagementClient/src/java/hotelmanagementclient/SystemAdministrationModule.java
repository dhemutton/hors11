package hotelmanagementclient;

import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import entity.Employee;
import entity.Partner;
import enums.EmployeeTypeEnum;
import exceptions.EmployeeExistException;
import exceptions.PartnerExistException;
import java.util.List;
import java.util.Scanner;

class SystemAdministratorModule {

    private PartnerControllerRemote partnerControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;

    public SystemAdministratorModule() {
    }

    public SystemAdministratorModule(PartnerControllerRemote partnerControllerRemote, EmployeeControllerRemote employeeControllerRemote) {
        this.partnerControllerRemote = partnerControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
    }

    public void runSystemAdminModule(Employee employee) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("*** HoRS :: System Administration ***\n");

            System.out.println("1. Create new employee");
            System.out.println("2. View all employees");
            System.out.println("3. Create new partner");
            System.out.println("4. View all partners");
            System.out.println("5. Logout");
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
                System.out.println("Logging out...");
                System.out.println();
                System.out.println("(╯°□°）╯︵ ┻━┻)");
                System.out.println();

                System.out.println("Goodbye~");
                employeeControllerRemote.updateEmployeeLogin(employee, false);
                break;
            } else {
                System.out.println("Invalid entry. Please try again");
            }
        }
    }

    private void doCreateEmployee() {

        try {
            Scanner scanner = new Scanner(System.in);
            Employee newEmployee = new Employee();

            System.out.println("*** HoRS :: System Administration :: Create New Employee ***\n");
            System.out.print("Enter First Name> ");
            newEmployee.setFirstName(scanner.nextLine().trim());
            System.out.print("Enter Last Name> ");
            newEmployee.setLastName(scanner.nextLine().trim());
            System.out.println("Enter Employee Role " + "\n");
            System.out.print("1. SYSTEMADMIN" + "\n");
            System.out.print("2. OPERATIONSMANAGER" + "\n");
            System.out.print("3. SALESMANAGER" + "\n");
            System.out.print("4. GUESTRELATIONS" + "\n");
            int input = scanner.nextInt();
            while (true) {
                if (input == 1) {
                    newEmployee.setEmployeeType(EmployeeTypeEnum.SYSTEMADMIN);
                    break;
                } else if (input == 2) {
                    newEmployee.setEmployeeType(EmployeeTypeEnum.OPERATIONSMANAGER);
                    break;

                } else if (input == 3) {
                    newEmployee.setEmployeeType(EmployeeTypeEnum.SALESMANAGER);
                    break;

                } else if (input == 4) {
                    newEmployee.setEmployeeType(EmployeeTypeEnum.GUESTRELATIONS);
                    break;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
            scanner.nextLine();
            System.out.print("Enter NRIC> ");
            newEmployee.setNric(scanner.nextLine().trim());
            System.out.print("Enter password> ");
            newEmployee.setPassword(scanner.nextLine().trim());
            newEmployee.setIsLogin(Boolean.FALSE);
            newEmployee = employeeControllerRemote.createNewEmployee(newEmployee);
            System.out.println("New employee " + newEmployee.getFirstName() + " " + newEmployee.getLastName() + " was created successfully!" + "\n");
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
            System.out.printf("%-5s%-20s%-25s\n", "ID", "Name", "Employee Role");
            for (Employee employee : listOfEmployees) {
                System.out.printf("%-5d%-20s%-25s\n", employee.getEmployeeId(), employee.getFirstName() + " " + employee.getLastName(), employee.getEmployeeType());
            }
            System.out.println();
        }
    }

    private void doCreatePartner() {
        try {
            Scanner scanner = new Scanner(System.in);
            Partner newPartner = new Partner();

            System.out.println("*** HoRS :: System Administration :: Create New Partner ***\n");
            System.out.print("Enter Username> ");
            newPartner.setUsername(scanner.nextLine().trim());
            System.out.print("Enter Password> ");
            newPartner.setPassword(scanner.nextLine().trim());
            System.out.print("Is this partner a manager? (Y/N)> ");
            String option = scanner.nextLine().trim();
            if (option.equals("Y") || option.equals("y")) {
                newPartner.setIsManager(Boolean.TRUE);
            }
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
            System.out.printf("%-5s%-20s\n", "ID", "Username");
            for (Partner partner : listOfPartners) {
                System.out.printf("%-5d%-20s\n", partner.getPartnerId(), partner.getUsername());
            }
            System.out.println();
        }
    }
}
