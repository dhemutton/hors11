/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmanagementclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Booking;
import entity.Employee;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import static enums.BookingStatusEnum.PENDING;
import static enums.BookingTypeEnum.WALKIN;
import static enums.ExceptionTypeEnum.UNASSIGNED;
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

    public FrontOfficeModule() {
    }

    public FrontOfficeModule(ReservationControllerRemote reservationControllerRemote, BookingControllerRemote bookingControllerRemote, RoomControllerRemote roomControllerRemote,
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.reservationControllerRemote = reservationControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }

    public void runFrontOfficeModule(Employee loginEmployee) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("1. Make walk in reservation");
            System.out.println("2. Check in guest");
            System.out.println("3. Check out guest");
            System.out.println("4. Exit");
            int choice = sc.nextInt();
            if (choice == 1) {
                doWalkInSearchRoom();
            } else if (choice == 2) {
                doCheckInGuest();
            } else if (choice == 3) {
                doCheckOutGuest();
            } else if (choice == 4) {
                break;
            } else {
                System.out.println("Invalid entry. Please try again");
            }
        }
    }

    private void doWalkInSearchRoom() {
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
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter booking ID");
        Long bookingID = sc.nextLong();
        List<Reservation> reservationList = reservationControllerRemote.retrieveAllReservationFromBooking(bookingID);
        System.out.println("Your allocated rooms are:");
        for (Reservation reservation : reservationList) {
            reservation.setIsCheckedIn(Boolean.TRUE);
            System.out.println("Room number: " + reservation.getRoom().getRoomNumber());
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
        } catch(ReservationNotFoundException ex) {
            System.out.println("Reservation not found!");
        } catch(RoomNotFoundException ex) {
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
            reservationControllerRemote.createNewReservation(new Reservation(roomTypeList.get(choice-1), booking, UNASSIGNED));
        }
    }
}
