/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.BookingControllerLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Booking;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomType;
import exceptions.BookingNotFoundException;
import exceptions.InvalidLoginCredentials;
import exceptions.PartnerNotFoundException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author matthealoo
 */
@WebService(serviceName = "HolidayWebService")
@Stateless()
public class HolidayWebService {

    @EJB
    private RoomControllerLocal roomControllerLocal;

    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;

    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    @EJB
    private PartnerControllerLocal partnerControllerLocal;

    @EJB
    private BookingControllerLocal bookingControllerLocal;

    /**
     * Web service operation
     */
    //Login
    public Partner loginForPartner(String username, String password) throws InvalidLoginCredentials, PartnerNotFoundException {

        return partnerControllerLocal.partnerLogin(username, password);
    }

    //Search room
    public List<Room> retrieveAllEnabledRooms() {

        List<Room> rooms = roomControllerLocal.retrieveAllEnabledRooms();

        for (Room room : rooms) {
            room.setRoomType(null);
            room.setReservations(null);
        }

        return rooms;
    }

    //Search room
    public List<Booking> retrieveAllBookingsWithinDates(Date startDate, Date endDate) {
        List<Booking> bookings = bookingControllerLocal.retrieveAllBookingsWithinDates(startDate, endDate);

        for (Booking b : bookings) {
            b.setPartner(null);
            b.setReservation(null);
            b.setGuest(null);
        }
        return bookings;
    }

    //Search room & view my reservation
    public List<Reservation> retrieveAllReservationFromBooking(Long bookingId) {

        List<Reservation> list = reservationControllerLocal.retrieveAllReservationFromBooking(bookingId);

        for (Reservation r : list) {
            r.setBooking(null);
            r.setRoom(null);
            r.setInitialRoomType(null);
            r.setFinalRoomType(null);
        }

        return list;
    }

    //reserve room
    public Booking createNewBooking(Booking booking) {
        return bookingControllerLocal.createNewBooking(booking);
    }

    //reserve room
    public List<RoomType> retrieveAllEnabledRoomType() {

        List<RoomType> list = roomTypeControllerLocal.retrieveAllEnabledRoomType();

        for (RoomType rt : list) {
            rt.setRoomRates(null);
            rt.setRooms(null);
        }
        return list;
    }

    //reserve room
    public Reservation createNewReservation(Reservation reservation) {
        Reservation r = reservationControllerLocal.createNewReservation(reservation);
        r.setBooking(null);
        return r;
    }

    //view my reservation
    public Booking retrieveBookingByIdForPartner(Long bookingId, Long partnerId) throws BookingNotFoundException {

        Booking booking = bookingControllerLocal.retrieveBookingByIdForPartner(bookingId, partnerId);
        booking.setPartner(null);
        booking.setReservation(null);
        booking.setGuest(null);

        return booking;
    }

    //view all my reservation
    public List<Booking> retrieveAllBookingsForPartner(Long partnerId) {

        List<Booking> bookings = bookingControllerLocal.retrieveAllBookingsForPartner(partnerId);

        for (Booking b : bookings) {
            b.setPartner(null);
            b.setReservation(null);
            b.setGuest(null);
        }
        return bookings;
    }

    public void updatePartnerLogin(Partner partner, boolean loggedIn) {

        partnerControllerLocal.updatePartnerLogin(partner, loggedIn);
    }

    public BigDecimal calculateReservationCost(Booking booking, RoomType roomType) {
        return roomRateControllerLocal.calculateReservationCost(booking, roomType);
    }

    public void updateBooking(Booking booking) {
        bookingControllerLocal.updateBooking(booking);
    }
    
        public List<RoomType> retrieveAllRoomtype() {
          List<RoomType> list=  roomTypeControllerLocal.retrieveAllRoomtype();
          for (RoomType rt: list) {
              rt.setRoomRates(null);
              rt.setRooms(null);
          }
          return list;
        }
}
