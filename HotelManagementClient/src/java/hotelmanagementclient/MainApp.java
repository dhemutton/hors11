/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmanagementclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import static enums.EmployeeTypeEnum.GUESTRELATIONS;
import static enums.EmployeeTypeEnum.SYSTEMADMIN;
import exceptions.EmployeeExistException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.util.Scanner;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author matthealoo
 */
class MainApp {

    private GuestControllerRemote guestControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private BookingControllerRemote bookingControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;

    public MainApp(GuestControllerRemote guestControllerRemote, ReservationControllerRemote reservationControllerRemote, BookingControllerRemote bookingControllerRemote, RoomControllerRemote roomControllerRemote,
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, PartnerControllerRemote partnerControllerRemote, EmployeeControllerRemote employeeControllerRemote) {
        this.guestControllerRemote = guestControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
    }

    public MainApp() {
    }

    public void runApp() throws EmployeeNotFoundException, RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException, EmployeeExistException {
        System.out.println("*** Welcome to HoRS Management Client  ***\n");
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your NRIC");
        String nric = sc.nextLine().trim();
        
        try {
        Employee loginEmployee = employeeControllerRemote.retrieveEmployeeByNric(nric);
        
        while (true) {
            System.out.println("Please enter password");
            String password = sc.nextLine().trim();
            if (password.equals(loginEmployee.getPassword())) {
                loginEmployee.setIsLogin(true);
                if(loginEmployee.getEmployeeType().equals(SYSTEMADMIN)) {
                    SystemAdministratorModule systemAdmin = new SystemAdministratorModule(partnerControllerRemote, employeeControllerRemote);
                    systemAdmin.runSystemAdminModule();
                    break;
                }
                else if(loginEmployee.getEmployeeType().equals(GUESTRELATIONS)) {
                    FrontOfficeModule frontOffice = new FrontOfficeModule(reservationControllerRemote, bookingControllerRemote, roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote);
                    frontOffice.runFrontOfficeModule(loginEmployee);
                    break;
                }
                else {
                    HotelOperationModule hotelOperations = new HotelOperationModule(roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote);
                    hotelOperations.runHotelOperationsModule(loginEmployee);
                    break;
                }
            }
            else {
                System.out.println("Invalid password entered. Please try again");
            }
        }
        loginEmployee.setIsLogin(false);
    }
        catch(EmployeeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving customer: " + ex.getMessage() + "\n");
        }
    }

}