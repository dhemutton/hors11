package ejb.session.singleton;

import ejb.session.stateless.BookingControllerLocal;
import ejb.session.stateless.EmployeeControllerLocal;
import ejb.session.stateless.GuestControllerLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import entity.Booking;
import entity.Employee;
import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import static enums.BookingStatusEnum.PENDING;
import static enums.BookingTypeEnum.ONLINE;
import static enums.BookingTypeEnum.WALKIN;
import static enums.EmployeeTypeEnum.GUESTRELATIONS;
import static enums.EmployeeTypeEnum.OPERATIONSMANAGER;
import static enums.EmployeeTypeEnum.SALESMANAGER;
import static enums.EmployeeTypeEnum.SYSTEMADMIN;
import static enums.ExceptionTypeEnum.UNASSIGNED;
import static enums.RateTypeEnum.NORMAL;
import static enums.RateTypeEnum.PEAK;
import static enums.RateTypeEnum.PROMO;
import static enums.RateTypeEnum.PUBLISHED;
import exceptions.EmployeeExistException;
import exceptions.GuestExistException;
import exceptions.GuestNotFoundException;
import exceptions.PartnerExistException;
import exceptions.RoomExistException;
import exceptions.RoomRateExistException;
import exceptions.RoomTypeCannotHaveDuplicatePublishedOrNormalException;
import exceptions.RoomTypeExistException;
import exceptions.RoomTypeNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author sleep
 */
@Singleton
@LocalBean
@Startup
public class DataInitialization {

    @EJB
    private RoomTypeControllerLocal roomTypeController;

    @EJB
    private RoomRateControllerLocal roomRateController;

    @EJB
    private RoomControllerLocal roomController;

    @EJB
    private ReservationControllerLocal reservationController;

    @EJB
    private PartnerControllerLocal partnerController;

    @EJB
    private GuestControllerLocal guestController;

    @EJB
    private EmployeeControllerLocal employeeController;

    @EJB
    private BookingControllerLocal bookingController;

    @PostConstruct
    public void PostConstruct() {
        try {
            employeeController.retrieveEmployeeByNric("111");
            System.out.println("Initialization not needed");
        } catch (EmployeeNotFoundException ex) {
            try {
                System.out.println("Initializing data");
                initializeData();
            } catch (EmployeeExistException | GuestExistException | RoomTypeExistException | RoomRateExistException | RoomTypeNotFoundException | RoomExistException | PartnerExistException | GuestNotFoundException | ParseException | RoomTypeCannotHaveDuplicatePublishedOrNormalException ex1) {
                Logger.getLogger(DataInitialization.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    private void initializeData() throws EmployeeExistException, GuestExistException, RoomTypeExistException, RoomRateExistException, RoomTypeNotFoundException, RoomExistException, PartnerExistException, GuestNotFoundException, ParseException, RoomTypeCannotHaveDuplicatePublishedOrNormalException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        employeeController.createNewEmployee(new Employee("Jonathan", "Soh", "111", "111", SYSTEMADMIN));
        employeeController.createNewEmployee(new Employee("Stephanie", "Soh", "222", "222", OPERATIONSMANAGER));
        employeeController.createNewEmployee(new Employee("Alison", "Soh", "333", "333", SALESMANAGER));
        employeeController.createNewEmployee(new Employee("Star", "Soh", "444", "444", GUESTRELATIONS));

        guestController.createGuest(new Guest("1@gmail.com", "guest1", "GUEST1", "1", "guest1"));
        guestController.createGuest(new Guest("2@gmail.com", "guest2", "GUEST2", "2", "guest2"));
        guestController.createGuest(new Guest("3@gmail.com", "guest3", "GUEST3", "3", "guest3"));
        guestController.createGuest(new Guest("4@gmail.com", "guest4", "GUEST4", "4", "guest4"));
        guestController.createGuest(new Guest("5@gmail.com", "guest5", "GUEST5", "5", "guest5"));
        guestController.createGuest(new Guest("6@gmail.com", "guest6", "GUEST6", "6", "guest6"));
        guestController.createGuest(new Guest("7@gmail.com", "guest7", "GUEST7", "7", "guest7"));
        guestController.createGuest(new Guest("8@gmail.com", "guest8", "GUEST8", "8", "guest8"));
        guestController.createGuest(new Guest("9@gmail.com", "guest9", "GUEST9", "9", "guest9"));

        partnerController.createNewPartner(new Partner("Matthea", "password", true));
        partnerController.createNewPartner(new Partner("Jonathan", "password", true));

        roomTypeController.createRoomType(new RoomType("Type A", "Best room with everything", 8, "1 king-size bed", "8-10 people", "Toilet, balcony, kitchen, living room", 1, false, true));
        roomTypeController.createRoomType(new RoomType("Type B", "Luxurious room", 6, "1 queen-size bed", "6-8 people", "Toilet, balcony, kitchen", 2, false, true));
        roomTypeController.createRoomType(new RoomType("Type C", "Standard room", 4, "1 super single bed", "4-6 people", "Toilet, balcony", 3, false, true));
        roomTypeController.createRoomType(new RoomType("Type D", "Budget room", 2, "1 single bed", "2-4 people", "Toilet", 4, false, true));

        Date endDate = new Date();
        endDate = formatter.parse("20/12/2018");

        roomRateController.createRoomRate(new RoomRate("Peak promo for type A", PEAK, new BigDecimal(160.50), new Date(), endDate, true, roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Published promo for type A", PUBLISHED, new BigDecimal(150.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Normal promo for type A", NORMAL, new BigDecimal(140.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Promo promo for type A", PROMO, new BigDecimal(130.50), new Date(), endDate, true, roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());

        roomRateController.createRoomRate(new RoomRate("Peak promo for type B", PEAK, new BigDecimal(120.50), new Date(), endDate, true, roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Published promo for type B", PUBLISHED, new BigDecimal(110.50), new Date(), endDate, true, roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Normal promo for type B", NORMAL, new BigDecimal(100.50), new Date(), endDate, true, roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Promo promo for type B", PROMO, new BigDecimal(90.50), new Date(), endDate, true, roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());

        roomRateController.createRoomRate(new RoomRate("Peak promo for type C", PEAK, new BigDecimal(80.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Published promo for type C", PUBLISHED, new BigDecimal(70.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Normal promo for type C", NORMAL, new BigDecimal(60.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Promo promo for type C", PROMO, new BigDecimal(50.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());

        roomRateController.createRoomRate(new RoomRate("Peak promo for type D", PEAK, new BigDecimal(40.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Published promo for type D", PUBLISHED, new BigDecimal(30.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Normal promo for type D", NORMAL, new BigDecimal(20.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomRateController.createRoomRate(new RoomRate("Promo promo for type D", PROMO, new BigDecimal(10.50), new Date(), new Date(), true, roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());

        roomController.createRoom(new Room("1001", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1002", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1003", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1004", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1005", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1006", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1007", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1008", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1009", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());
        roomController.createRoom(new Room("1010", roomTypeController.retrieveRoomTypeByName("Type A")), roomTypeController.retrieveRoomTypeByName("Type A").getRoomTypeId());

        roomController.createRoom(new Room("2001", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2002", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2003", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2004", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2005", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2006", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2007", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2008", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2009", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());
        roomController.createRoom(new Room("2010", roomTypeController.retrieveRoomTypeByName("Type B")), roomTypeController.retrieveRoomTypeByName("Type B").getRoomTypeId());

        roomController.createRoom(new Room("3001", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3002", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3003", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3004", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3005", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3006", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3007", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3008", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3009", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());
        roomController.createRoom(new Room("3010", roomTypeController.retrieveRoomTypeByName("Type C")), roomTypeController.retrieveRoomTypeByName("Type C").getRoomTypeId());

        roomController.createRoom(new Room("4001", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4002", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4003", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4004", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4005", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4006", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4007", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4008", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4009", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());
        roomController.createRoom(new Room("4010", roomTypeController.retrieveRoomTypeByName("Type D")), roomTypeController.retrieveRoomTypeByName("Type D").getRoomTypeId());

        Booking booking;
        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("05/11/2018"), formatter.parse("10/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("1@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("05/11/2018"), formatter.parse("10/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("2@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("05/11/2018"), formatter.parse("10/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("3@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("05/11/2018"), formatter.parse("10/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("4@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("05/11/2018"), formatter.parse("10/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("5@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("10/11/2018"), formatter.parse("20/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("6@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("10/11/2018"), formatter.parse("20/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("7@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("10/11/2018"), formatter.parse("20/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("8@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("10/11/2018"), formatter.parse("20/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("9@gmail.com"));
        bookingController.updateBooking(booking);
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));

        booking = bookingController.createNewBooking(new Booking(WALKIN, PENDING, formatter.parse("10/11/2018"), formatter.parse("20/11/2018")));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));

        //Day 1 bookings (test 2am function)
        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("13/11/2018"), formatter.parse("24/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("9@gmail.com"));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type B"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type C"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type D"), booking, UNASSIGNED));

        //Day 2 bookings (test 2am function TYPE2 error)
        booking = bookingController.createNewBooking(new Booking(ONLINE, PENDING, formatter.parse("24/11/2018"), formatter.parse("25/11/2018")));
        booking.setGuest(guestController.retrieveGuestByEmail("8@gmail.com"));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));
        reservationController.createNewReservation(new Reservation(roomTypeController.retrieveRoomTypeByName("Type A"), booking, UNASSIGNED));

    }
}
