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
import entity.RoomType;
import static enums.BookingStatusEnum.PENDING;
import static enums.BookingTypeEnum.ONLINE;
import static enums.BookingTypeEnum.WALKIN;
import static enums.EmployeeTypeEnum.GUESTRELATIONS;
import static enums.EmployeeTypeEnum.SYSTEMADMIN;
import static enums.ExceptionTypeEnum.UNASSIGNED;
import exceptions.BookingNotFoundException;
import exceptions.EmployeeExistException;
import exceptions.GuestExistException;
import exceptions.GuestNotFoundException;
import exceptions.InvalidLoginCredentials;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private Guest guest;

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
                    doGuestLogin();
                } else if (choice == 2) {
                    doRegisterAsGuest();
                } else if (choice == 3) {
                    doSearchRoom();
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
                    doSearchRoom();
                } else if (choice == 2) {
                    doViewMyReservation();
                } else if (choice == 3) {
                    doViewAllMyReservation(guest.getGuestId());
                } else if (choice == 4) {
                    loggedIn = false;
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        }
    }

    private void doGuestLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your email address: ");
        String email = sc.nextLine().trim();
        System.out.println("Please enter password");
            String password = sc.nextLine().trim();
 
        try {
            guest = guestControllerRemote.guestLogin(email, password);
            if (!guest.getIsLogin()) {
                        loggedIn = true;
                        guestControllerRemote.updateGuestLogin(guest, true);
            } else {
                System.out.println("Guest is already logged in.");
            }
        } catch (InvalidLoginCredentials ex) {
                        System.out.println("Invalid password entered. Please try again");      
        } catch (GuestNotFoundException ex) {
            System.out.println("An error has occurred while retrieving guest: " + ex.getMessage() + "\n");
        }
    }

    private void doRegisterAsGuest() {
        try {
            Scanner scanner = new Scanner(System.in);
            Guest newGuest = new Guest();

            System.out.println("*** HoRS :: Reservation Client :: Register As Guest ***\n");
            //maybe modify guest to have first, last name next time
            System.out.print("Enter Email Address> ");
            newGuest.setEmail(scanner.nextLine().trim());
            System.out.print("Enter password> ");
            newGuest.setPassword(scanner.nextLine().trim());
            newGuest.setIsLogin(Boolean.FALSE);
            newGuest = guestControllerRemote.createGuest(newGuest);
            System.out.println("New guest was created successfully!" + "\n");
        } catch (GuestExistException ex) {
            System.out.println("An error has occurred while creating the new guest: " + ex.getMessage() + "!\n");
        }
    }

    private void doSearchRoom() {
        Scanner sc = new Scanner(System.in);
        Date startDate = null, endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int maxRooms = roomControllerRemote.retrieveAllRooms().size();
        System.out.println("Please enter start date (dd/mm/yyyy");
        Boolean again = true;

        while (again) {
            try {
                startDate = formatter.parse(sc.nextLine().trim());
                again = false;
            } catch (ParseException ex) {
                again = true;
                System.out.println("Incorrect date format.");
            }
        }
        System.out.println("Please enter end date (dd/mm/yyyy");
        again = true;
        while (again) {
            try {
                endDate = formatter.parse(sc.nextLine().trim());
                again = false;
            } catch (ParseException ex) {
                again = true;
                System.out.println("Incorrect date format.");
            }
        }

        List<Booking> bookingList = bookingControllerRemote.retrieveAllBookingsWithinDates(startDate, endDate);
        for (Booking booking : bookingList) {
            reservationList.addAll(reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId()));
        }
        int roomsLeft = maxRooms - reservationList.size();
        if (roomsLeft > 0) {
            System.out.println(roomsLeft + " rooms available");
            if (loggedIn) {
                System.out.println("Would you like to make a reservation? (Enter 'Y' to reserve)");
                if (sc.nextLine().trim().equals("Y")) {
                    doReserveRoom(roomsLeft, startDate, endDate);
                }
            } else {
                System.out.println("Login to make a reservation.");
            }
        } else {
            System.out.println("No more rooms are available during this period");
        }
    }

    private void doReserveRoom(int roomsLeft, Date startDate, Date endDate) {
        Scanner sc = new Scanner(System.in);
        int quantity = 0;
        Booking booking = bookingControllerRemote.createNewBooking(new Booking(ONLINE, PENDING, startDate, endDate));
        while (true) {
            System.out.println("How many rooms do you want to reserve? (Maximum: " + roomsLeft + ")");
            quantity = sc.nextInt();
            if (quantity <= 0 || quantity > roomsLeft) {
                System.out.println("Invalid entry. Please try again");
            } else {
                break;
            }
        }

        sc.nextLine();
        for (int i = 0; i < quantity; i++) {
            int choice;
            System.out.println("Select room type to reserve : ");
            List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllEnabledRoomType();
            while (true) {
                for (int j = 1; j <= roomTypeList.size(); j++) {
                    System.out.println(j + ". " + roomTypeList.get(j - 1).getName());
                }
                choice = sc.nextInt();
                if (choice < 1 || choice > roomTypeList.size()) {
                    System.out.println("Invalid entry. Please try again");
                } else {
                    break;
                }
            }
            reservationControllerRemote.createNewReservation(new Reservation(roomTypeList.get(choice - 1), booking, UNASSIGNED));
        }
    }

    private void doViewMyReservation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Reservation Client :: View My Reservation ***\n");
        System.out.println("Which reservation would you like to view?  (Enter reservation id) ");
        Long bookingId = scanner.nextLong();
        try {
            Booking booking = bookingControllerRemote.retrieveBookingById(bookingId);
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Start Date: " + booking.getStartDate());
            System.out.println("End Date: " + booking.getEndDate());
            System.out.println("Booking Type: " + booking.getBookingType());
            System.out.println("Booking Status: " + booking.getBookingStatus());
            System.out.println("Total Cost: " + booking.getCost());
            List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId());
            System.out.println("Number of rooms reserved: " + reservations.size());
            System.out.println("Room Type: " + reservations.get(0).getRoomType().getName());
            System.out.println("*********************************************************************");
            System.out.println();

        } catch (BookingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }
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
                System.out.println("Room Type: " + reservations.get(0).getRoomType().getName());
                System.out.println("*********************************************************************");
                System.out.println();
            }
        }
    }

}
