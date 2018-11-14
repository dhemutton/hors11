package hotelmanagementclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import ejb.session.stateless.SelfInvokeDailyControllerRemote;
import entity.Employee;
import static enums.EmployeeTypeEnum.GUESTRELATIONS;
import static enums.EmployeeTypeEnum.SYSTEMADMIN;
import exceptions.InvalidLoginCredentials;
import java.util.Scanner;
import util.exception.EmployeeNotFoundException;

class MainApp {

    private GuestControllerRemote guestControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private BookingControllerRemote bookingControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;
    private SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote;
    
    private Employee loginEmployee;

    public MainApp(GuestControllerRemote guestControllerRemote, ReservationControllerRemote reservationControllerRemote, BookingControllerRemote bookingControllerRemote, RoomControllerRemote roomControllerRemote,
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, PartnerControllerRemote partnerControllerRemote, EmployeeControllerRemote employeeControllerRemote, SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote) {
        this.guestControllerRemote = guestControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
        this.selfInvokeDailyControllerRemote = selfInvokeDailyControllerRemote;
    }

    public MainApp() {
    }

    public void runApp()  {
        System.out.println("*** Welcome to HoRS Management Client  ***\n");
        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("Please enter your NRIC");
            String nric = sc.nextLine().trim();
            System.out.println("Please enter password");
            String password = sc.nextLine().trim();

            try {

                if (nric.length() > 0 && password.length() > 0) {
                    loginEmployee = employeeControllerRemote.employeeLogin(nric, password);

                    if (loginEmployee.getIsLogin() == false) {
                        employeeControllerRemote.updateEmployeeLogin(loginEmployee, true);
                        System.out.println("Login successful! Redirecting...");
                        if (loginEmployee.getEmployeeType().equals(SYSTEMADMIN)) {
                            SystemAdministratorModule systemAdmin = new SystemAdministratorModule(partnerControllerRemote, employeeControllerRemote);
                            systemAdmin.runSystemAdminModule(loginEmployee);
                            break;
                        } else if (loginEmployee.getEmployeeType().equals(GUESTRELATIONS)) {
                            FrontOfficeModule frontOffice = new FrontOfficeModule(reservationControllerRemote, bookingControllerRemote, roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote,employeeControllerRemote,selfInvokeDailyControllerRemote);
                            frontOffice.runFrontOfficeModule(loginEmployee);
                            break;
                        } else {
                            HotelOperationModule hotelOperations = new HotelOperationModule(roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote, reservationControllerRemote,employeeControllerRemote);
                            hotelOperations.runHotelOperationsModule(loginEmployee);
                            break;
                        }
                    } else {
                        System.out.println("Employee is already logged in.");
                    }
                } else {
                    System.out.println("Invalid inputs. Please try again.");
                }
            } catch (InvalidLoginCredentials ex) {
                System.out.println("Invalid password entered. Please try again");

            } catch (EmployeeNotFoundException ex) {
                System.out.println("Employee does not exist. Please try again");
            }
        }
    }

}
