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
                doWalkInReservation();
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

    private void doWalkInReservation() {
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
        for(Booking booking : bookingList) {
            reservationList.addAll(reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId()));
        }
        int roomsLeft = maxRooms-reservationList.size();
        if(roomsLeft>0) {           
            System.out.println(roomsLeft+" rooms available");
            doSearchRoom();
        }
        else {
            System.out.println("No more rooms are available during this period");
        }
        
    }

    private void doCheckInGuest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCheckOutGuest() throws RoomNotFoundException, ReservationNotFoundException {
        Scanner sc = new Scanner(System.in);
        Date inputDate=null;
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
      
    }

    private void doSearchRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
