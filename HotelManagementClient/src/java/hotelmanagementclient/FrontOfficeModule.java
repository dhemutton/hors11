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
import entity.Employee;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCheckInGuest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCheckOutGuest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}