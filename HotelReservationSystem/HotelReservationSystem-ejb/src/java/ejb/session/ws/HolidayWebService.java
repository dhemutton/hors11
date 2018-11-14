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
import entity.RoomRate;
import entity.RoomType;
import static enums.RateTypeEnum.NORMAL;
import static enums.RateTypeEnum.PEAK;
import static enums.RateTypeEnum.PROMO;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    /**
     * Web service operation
     */
    //Login
    public Partner loginForPartner(String username, String password) throws InvalidLoginCredentials, PartnerNotFoundException {

        Partner partner = partnerControllerLocal.partnerLogin(username, password);

//        em.detach(partner);
        partner.getBookings().clear();

        return partner;
    }

    //Search room
    public List<Room> retrieveAllEnabledRooms() {

        List<Room> rooms = roomControllerLocal.retrieveAllEnabledRooms();

        for (Room room : rooms) {
//            em.detach(room);
            room.setRoomType(null);
            room.setReservations(null);
        }

        return rooms;
    }

    //Search room
    public List<Booking> retrieveAllBookingsWithinDates(Date startDate, Date endDate) {
        List<Booking> bookings = bookingControllerLocal.retrieveAllBookingsWithinDates(startDate, endDate);
        
        for (Booking b : bookings) {
//            em.detach(b);
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
//            em.detach(r);
            r.setBooking(null);
            r.setRoom(null);      
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
//            em.detach(rt);
            rt.setRoomRates(null);
            rt.setRooms(null);
        }
        return list;
    }

    //reserve room
    public Reservation createNewReservation(Reservation reservation) {
        Reservation r = reservationControllerLocal.createNewReservation(reservation);
//        em.detach(r);
        r.setBooking(null);
        return r;
    }

    //view my reservation
    public Booking retrieveBookingByIdForPartner(Long bookingId, Long partnerId) throws BookingNotFoundException {

        Booking booking = bookingControllerLocal.retrieveBookingByIdForPartner(bookingId, partnerId);
//        em.detach(booking);
        booking.setPartner(null);
        booking.setReservation(null);
        booking.setGuest(null);

        return booking;
    }

    //view all my reservation
    public List<Booking> retrieveAllBookingsForPartner(Long partnerId) {

        List<Booking> bookings = bookingControllerLocal.retrieveAllBookingsForPartner(partnerId);

        for (Booking b : bookings) {
//            em.detach(b);
            b.setPartner(null);
            b.setReservation(null);
//            b.setGuest(null);
        }
        return bookings;
    }

    public void updatePartnerLogin(Partner partner, boolean loggedIn) {

        partnerControllerLocal.updatePartnerLogin(partner, loggedIn);
    }

    public BigDecimal calculateReservationCost(Long bookingId, Long roomTypeId) {
        Booking booking = em.find(Booking.class, bookingId);
        RoomType roomType = em.find(RoomType.class, roomTypeId);
        BigDecimal total = new BigDecimal(0);
        Date startDate = booking.getStartDate();
        Date endDate = booking.getEndDate();
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType=:roomType AND rr.isEnabled = true");
        query.setParameter("roomType", roomType);
        List<RoomRate> roomRates = query.getResultList();
        System.out.println("Number of room rates: " + roomRates.size());
        Date checkDate = startDate;
        BigDecimal promoRate = new BigDecimal(0);
        BigDecimal peakRate = new BigDecimal(0);
        BigDecimal normalRate = new BigDecimal(0);
        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRateType().equals(PROMO)) {
                promoRate = roomRate.getRatePerNight();
                System.out.println("Promo rate: " + promoRate);
            }
            if (roomRate.getRateType().equals(PEAK)) {
                peakRate = roomRate.getRatePerNight();
                System.out.println("Peak rate: " + peakRate);
            }
            if (roomRate.getRateType().equals(NORMAL)) {
                normalRate = roomRate.getRatePerNight();
                System.out.println("Normal rate: " + normalRate);
            }
        }
        while (checkDate.before(endDate) || checkDate.equals(endDate)) {
            System.out.println("Checking reservation on " + checkDate);
            boolean promo = false;
            boolean peak = false;
            for (RoomRate roomRate : roomRates) {
                if (roomRate.getRateType().equals(PROMO)) {
                    if (roomRate.getStartDate().before(checkDate) && roomRate.getEndDate().after(checkDate) || roomRate.getStartDate().equals(checkDate) || roomRate.getEndDate().equals(checkDate)) { //checking isValid
                        promo = true;
                    }
                }
                if (roomRate.getRateType().equals(PEAK)) {
                    if (roomRate.getStartDate().before(checkDate) && roomRate.getEndDate().after(checkDate) || roomRate.getStartDate().equals(checkDate) || roomRate.getEndDate().equals(checkDate)) { //checking isValid
                        peak = true;
                    }
                }
            }
            if (peak == true && promo == true) {
                total = total.add(promoRate);
                System.out.println("Promotion rate was added");
            } else if (peak == true && promo == false) {
                total = total.add(peakRate);
                System.out.println("Peak rate was added");
            } else if (peak == false && promo == true) {
                total = total.add(promoRate);
                System.out.println("Promotion rate was added");
            } else {
                total = total.add(normalRate);
                System.out.println("Normal rate was added");
            }
            Date temp = checkDate;
            checkDate.setDate(temp.getDate() + 1);
        }
        System.out.println("Total cost: " + total);
        return total;

    }

    public void updateBooking(Booking booking) {
        bookingControllerLocal.updateBooking(booking);
    }

    public List<RoomType> retrieveAllRoomtype() {
        List<RoomType> list = roomTypeControllerLocal.retrieveAllRoomtype();
        for (RoomType rt : list) {
            em.detach(rt);
            rt.setRoomRates(null);
            rt.setRooms(null);
        }
        return list;
    }

    public void updatePartner(Partner partner) {
        partnerControllerLocal.updatePartner(partner);
    }

    public void persist(Object object) {
        em.persist(object);
    }
}
