/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmanagementclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import ejb.session.stateless.SelfInvokeDailyControllerRemote;
import entity.Booking;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import static enums.BookingStatusEnum.PENDING;
import static enums.BookingTypeEnum.WALKIN;
import static enums.ExceptionTypeEnum.UNASSIGNED;
import exceptions.BookingNotFoundException;
import exceptions.ReservationNotFoundException;
import exceptions.RoomNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author matthealoo
 */
class FrontOfficeModule {

    private ReservationControllerRemote reservationControllerRemote;
    private BookingControllerRemote bookingControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private EmployeeControllerRemote employeeControllerRemote;
    private SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote;

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(ReservationControllerRemote reservationControllerRemote, BookingControllerRemote bookingControllerRemote, RoomControllerRemote roomControllerRemote,
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, EmployeeControllerRemote employeeControllerRemote,SelfInvokeDailyControllerRemote selfInvokeDailyControllerRemote) {
        this.reservationControllerRemote = reservationControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.employeeControllerRemote = employeeControllerRemote;
        this.selfInvokeDailyControllerRemote = selfInvokeDailyControllerRemote;

    }

    public void runFrontOfficeModule(Employee loginEmployee) {
        System.out.println("*** HoRS :: Front Office ::  Guest Relations Officer ***\n");

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Make walk in reservation");
            System.out.println("2. Check in guest");
            System.out.println("3. Check out guest");
            System.out.println("4. *SECRET METHOD* Self-invoke room allocation");
            System.out.println("5. Logout");
            int choice = sc.nextInt();
            if (choice == 1) {
                doWalkInSearchRoom();
            } else if (choice == 2) {
                doCheckInGuest();
            } else if (choice == 3) {
                doCheckOutGuest();
            } else if (choice == 4) {
                doSecretMethod();
            } else if (choice == 5) {
                employeeControllerRemote.updateEmployeeLogin(loginEmployee, false);

                break;
            } else {
                System.out.println("Invalid entry. Please try again");
            }
        }
    }

    private void doWalkInSearchRoom() {
        System.out.println("*** HoRS :: Front Office Module:: Search Room ***\n");

        Scanner sc = new Scanner(System.in);
        Date startDate = null, endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int maxRooms = roomControllerRemote.retrieveAllRooms().size();
        System.out.println("Please enter start date (dd/mm/yyyy");
        try {
            startDate = formatter.parse(sc.nextLine().trim());
        } catch (ParseException ex) {
            System.out.println("Incorrect date format.");
        }
        System.out.println("Please enter start date (dd/mm/yyyy");
        try {
            endDate = formatter.parse(sc.nextLine().trim());
        } catch (ParseException ex) {
            System.out.println("Incorrect date format.");
        }
        List<Booking> bookingList = bookingControllerRemote.retrieveAllBookingsWithinDates(startDate, endDate);
        for (Booking booking : bookingList) {
            reservationList.addAll(reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId()));
        }
        int roomsLeft = maxRooms - reservationList.size();
        if (roomsLeft > 0) {
            System.out.println(roomsLeft + " rooms available");
            doReserveRoom(roomsLeft, startDate, endDate);
        } else {
            System.out.println("No more rooms are available during this period");
        }

    }

    private void doCheckInGuest() {
        System.out.println("*** HoRS :: Front Office Module:: Check in Guest ***\n");
        Long bookingId;
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("1. Online Booking");
                System.out.println("2. Walk-in Booking");
                System.out.println("3. Partner Booking");
                int choice = sc.nextInt();
                if (choice == 1) {
                    System.out.println("Please enter booking ID");
                    bookingId = sc.nextLong();
                    System.out.println("Please enter guest ID");
                    Long guestId = sc.nextLong();
                    Booking booking = bookingControllerRemote.retrieveBookingByIdForGuest(bookingId, guestId);
                    break;
                } else if (choice == 2) {
                    System.out.println("Please enter booking ID");
                    bookingId = sc.nextLong();
                    Booking booking = bookingControllerRemote.retrieveBookingById(bookingId);
                    break;
                } else if (choice == 3) {
                    System.out.println("Please enter booking ID");
                    bookingId = sc.nextLong();
                    System.out.println("Please enter partner ID");
                    Long partnerId = sc.nextLong();
                    Booking booking = bookingControllerRemote.retrieveBookingByIdForPartner(bookingId, partnerId);
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }

            List<Reservation> reservationList = reservationControllerRemote.retrieveAllReservationFromBooking(bookingId);
            System.out.println("Your allocated rooms are:");
            for (Reservation reservation : reservationList) {
                reservation.setIsCheckedIn(Boolean.TRUE);
                System.out.println("Room number: " + reservation.getRoom().getRoomNumber());
            }
        } catch (BookingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }

    }

    private void doCheckOutGuest() {

        try {
            Scanner sc = new Scanner(System.in);
            Date inputDate = null;
            System.out.println("Please enter room number");
            String number = sc.nextLine().trim();
            Room room = roomControllerRemote.retrieveRoomByRoomNum(number);
            room.setIsVacant(Boolean.TRUE);
            roomControllerRemote.mergeRoom(room);
            System.out.println("Please enter your reservation ID");
            Long reservationID = sc.nextLong();
            Reservation reservation = reservationControllerRemote.retrieveReservationById(reservationID);
            reservation.setIsCheckedOut(Boolean.TRUE);
            reservationControllerRemote.updateReservation(reservation);
        } catch (ReservationNotFoundException ex) {
            System.out.println("Reservation not found!");
        } catch (RoomNotFoundException ex) {
            System.out.println("Room not found!");
        }
    }

    private void doReserveRoom(int roomsLeft, Date startDate, Date endDate) {
        Scanner sc = new Scanner(System.in);
        int quantity = 0;
        Booking booking = bookingControllerRemote.createNewBooking(new Booking(WALKIN, PENDING, startDate, endDate));
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
            System.out.println("Select room type");
            List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllRoomtype();
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

    private void doSecretMethod() {
        selfInvokeDailyControllerRemote.dailyReservationRoomAssignment();
//        selfInvokeDailyControllerRemote.deleteAllRoomRates();
//        selfInvokeDailyControllerRemote.deleteAllRoomTypes();
//        selfInvokeDailyControllerRemote.deleteAllRooms();
        System.out.println("Secret Method done hehe!");
    }

}
