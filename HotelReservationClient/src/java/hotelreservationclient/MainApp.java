/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelreservationclient;

/**
 *
 * @author matthealoo
 */
import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Booking;
import entity.Employee;
import entity.Guest;
import entity.Reservation;
import static enums.EmployeeTypeEnum.GUESTRELATIONS;
import static enums.EmployeeTypeEnum.SYSTEMADMIN;
import exceptions.BookingNotFoundException;
import exceptions.EmployeeExistException;
import exceptions.GuestNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.util.ArrayList;
import java.util.List;
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
    private Boolean loggedIn = false;
    private Long guestId;

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

    public void runApp() throws BookingNotFoundException, RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException, EmployeeExistException, ReservationNotFoundException {
        System.out.println("*** Welcome to HoRS Reservation Client  ***\n");
        Scanner sc = new Scanner(System.in);
        while (true) {
            while (!loggedIn) {
                System.out.println("1. Guest Login");
                System.out.println("2. Register as Guest");
                System.out.println("3. Search Hotel Room");
                System.out.println("4. Exit");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    guestId = doGuestLogin();
                } else if (choice == 2) {
//               register as guest
                } else if (choice == 3) {
//                search hotel room
                } else if (choice == 4) {
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }

            while (loggedIn) {

                System.out.println("1. Search Hotel Room");
                System.out.println("2. View My Reservation Details");
                System.out.println("3. View All My Reservations");
                System.out.println("4. Logout");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) {
                    //Search Hotel Room
                } else if (choice == 2) {
//               View My Reservation Details
                } else if (choice == 3) {
                    doViewAllMyReservation(guestId);
                } else if (choice == 4) {
                    loggedIn = false;
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        }
    }

    private Long doGuestLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your email address: ");
        String email = sc.nextLine().trim();
        Long dummyVal = null;
        int count = 0;
        try {
            Guest guest = guestControllerRemote.retrieveGuestByEmail(email);
            if (!guest.getIsLogin()) {
                while (count < 4) {
                    System.out.println("Please enter password");
                    String password = sc.nextLine().trim();
                    if (password.equals(guest.getPassword())) {
                        guest.setIsLogin(true);
                        loggedIn = true;
                        guestControllerRemote.updateGuestLogin(guest, loggedIn);
                        return guest.getGuestId();
                    } else {
                        System.out.println("Invalid password entered. Please try again");
                        count++;
                    }
                }
            } else {
                System.out.println("Guest is already logged in. Please try again later.");
            }

        } catch (GuestNotFoundException ex) {
            System.out.println("An error has occurred while retrieving guest: " + ex.getMessage() + "\n");
        }
        return dummyVal;
    }
    
    
    private void doRegisterAsGuest() {
        
        
        
    }

    private void doViewAllMyReservation(Long guestId) {
        System.out.println("*** HoRS :: Reservation Client :: View All My Reservations ***\n");

        List<Booking> list = bookingControllerRemote.retrieveAllBookingsForGuest(guestId);
        if (list.size() == 0) {
            System.out.println("No past reservations made.");
        } else {
            for (int i = 0; i <= list.size(); i++) {
                System.out.println((i + 1) + ". Booking ID: " + list.get(i).getBookingId());
                System.out.println("Start Date: " + list.get(i).getStartDate());
                System.out.println("End Date: " + list.get(i).getEndDate());
                System.out.println("Booking Type: " + list.get(i).getBookingType());
                System.out.println("Booking Status: " + list.get(i).getBookingStatus());
                System.out.println("Total Cost: " + list.get(i).getCost());
                List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationFromBooking(list.get(i).getBookingId());
                System.out.println("Number of rooms reserved: " + reservations.size());
                System.out.println("Room Type: " + reservations.get(i).getRoomType().getName());
                System.out.println("*********************************************************************");
                System.out.println();
            }
        }
    }

}
