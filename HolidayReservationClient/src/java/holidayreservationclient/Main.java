/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceRef;
import ws.session.Booking;
import ws.session.BookingNotFoundException_Exception;
import ws.session.HolidayWebService_Service;
import ws.session.InvalidLoginCredentials_Exception;
import ws.session.Partner;
import ws.session.PartnerNotFoundException_Exception;
import ws.session.Reservation;
import java.util.Date;
import static ws.session.BookingStatusEnum.PENDING;
import static ws.session.BookingTypeEnum.ONLINE;
import static ws.session.ExceptionTypeEnum.UNASSIGNED;
import ws.session.RoomType;


/**
 *
 * @author matthealoo
 */
public class Main {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/localhost_8000/HolidayWebService/HolidayWebService.wsdl")
    private static HolidayWebService_Service service;
    private Boolean loggedIn = false;
    private Partner partner;

    
    /**
     * @param args the command line arguments
     */
    public  void main(String[] args) {

    /**
     * @param args the command line arguments
     */
        System.out.println("*** Welcome to HoRS Partner Client  ***\n");
        Scanner sc = new Scanner(System.in);
        while (true) {
            while (!loggedIn) {
                System.out.println("1. Partner Login");
                System.out.println("3. Search Hotel Room");
                System.out.println("4. Exit");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    doPartnerLogin();
                } else if (choice == 2) {
                   doSearchRoom();
                } else if (choice == 3) {

                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }

            while (loggedIn&&partner.isIsManager()) {

                System.out.println("1. Partner Search Hotel Room");
                System.out.println("2. View Partner Reservation Details");
                System.out.println("3. View All Partner Reservations");
                System.out.println("4. Logout");
                int choice = sc.nextInt();
                sc.nextLine();
                if (choice == 1) {
                    doSearchRoom();
                } else if (choice == 2) {
                    doViewMyReservation(partner.getPartnerId());
                } else if (choice == 3) {
                    doViewAllMyReservation(partner.getPartnerId());
                } else if (choice == 4) {
                    loggedIn = false;
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        }
    }

    private void doPartnerLogin() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = sc.nextLine().trim();
        System.out.println("Please enter password");
        String password = sc.nextLine().trim();

        try {
            partner = loginForPartner(username, password);
//            if (!partner.getIsLogin()) {
            loggedIn = true;
//                      updatePartnerLogin(partner, true);
//            } else {
//                System.out.println("Guest is already logged in.");
//            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving guest: " + ex.getMessage() + "\n");
        } catch (InvalidLoginCredentials_Exception ex) {
            System.out.println("Invalid password entered. Please try again");
        }
    }

    private void doSearchRoom() {
        Scanner sc = new Scanner(System.in);
        Date startDate = null, endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int maxRooms = retrieveAllEnabledRooms().size();
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

        GregorianCalendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        try {
            XMLGregorianCalendar startXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(startCalendar);
            XMLGregorianCalendar endXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(endCalendar);

            List<Booking> bookingList = retrieveAllBookingsWithinDates(startXmlCalendar, endXmlCalendar);
            for (Booking booking : bookingList) {
                reservationList.addAll(retrieveAllReservationFromBooking(booking.getBookingId()));
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

        } catch (DatatypeConfigurationException ex) {
            System.out.println("Data type conversion to XML Gregorian Calendar error!");
        }
    }

    private void doReserveRoom(int roomsLeft, Date startDate, Date endDate) {
        Scanner sc = new Scanner(System.in);
        int quantity = 0;
        Booking booking = new Booking();
        booking.setBookingType(ONLINE);
        booking.setBookingStatus(PENDING);

        GregorianCalendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        GregorianCalendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        try {
            XMLGregorianCalendar startXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(startCalendar);
            XMLGregorianCalendar endXmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(endCalendar);

            booking.setStartDate(startXmlCalendar);
            booking.setEndDate(endXmlCalendar);
            booking = createNewBooking(booking);

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
                List<RoomType> roomTypeList = retrieveAllEnabledRoomType();
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
                Reservation reservation = new Reservation();
                reservation.setInitialRoomType(roomTypeList.get(choice - 1));
                reservation.setBooking(booking);
                reservation.setExceptionType(UNASSIGNED);
                
                reservation = createNewReservation(reservation);
                
            }
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Data type conversion to XML Gregorian Calendar error!");

        }
    }

    private void doViewMyReservation(Long partnerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Reservation Client :: View My Reservation ***\n");
        System.out.println("Which reservation would you like to view?  (Enter reservation id) ");
        Long bookingId = scanner.nextLong();
        try {
            Booking booking = retrieveBookingByIdForPartner(bookingId, partnerId);
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Start Date: " + booking.getStartDate());
            System.out.println("End Date: " + booking.getEndDate());
            System.out.println("Booking Type: " + booking.getBookingType());
            System.out.println("Booking Status: " + booking.getBookingStatus());
            System.out.println("Total Cost: " + booking.getCost());
            List<Reservation> reservations = retrieveAllReservationFromBooking(booking.getBookingId());
            System.out.println("Number of rooms reserved: " + reservations.size());
            System.out.println("Room Type: " + reservations.get(0).getInitialRoomType().getName());
            System.out.println("*********************************************************************");
            System.out.println();

        } catch (BookingNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }
    }

    private void doViewAllMyReservation(Long partnerId) {
        System.out.println("*** HoRS :: Reservation Client :: View All My Reservations ***\n");

        List<Booking> list = retrieveAllBookingsForPartner(partnerId);
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
                List<Reservation> reservations = retrieveAllReservationFromBooking(list.get(i).getBookingId());
                System.out.println("Number of rooms reserved: " + reservations.size());
                System.out.println("Room Type: " + reservations.get(0).getInitialRoomType().getName());
                System.out.println("*********************************************************************");
                System.out.println();
            }
        }
    }

    private static Booking createNewBooking(ws.session.Booking arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.createNewBooking(arg0);
    }

    private static Reservation createNewReservation(ws.session.Reservation arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.createNewReservation(arg0);
    }

    private static Partner loginForPartner(java.lang.String arg0, java.lang.String arg1) throws PartnerNotFoundException_Exception, InvalidLoginCredentials_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.loginForPartner(arg0, arg1);
    }

    private static java.util.List<ws.session.Booking> retrieveAllBookingsForPartner(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllBookingsForPartner(arg0);
    }

    private static java.util.List<ws.session.Booking> retrieveAllBookingsWithinDates(javax.xml.datatype.XMLGregorianCalendar arg0, javax.xml.datatype.XMLGregorianCalendar arg1) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllBookingsWithinDates(arg0, arg1);
    }

    private static java.util.List<ws.session.RoomType> retrieveAllEnabledRoomType() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledRoomType();
    }

    private static java.util.List<ws.session.Room> retrieveAllEnabledRooms() {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllEnabledRooms();
    }

    private static java.util.List<ws.session.Reservation> retrieveAllReservationFromBooking(java.lang.Long arg0) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveAllReservationFromBooking(arg0);
    }

    private static Booking retrieveBookingByIdForPartner(java.lang.Long arg0, java.lang.Long arg1) throws BookingNotFoundException_Exception {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        ws.session.HolidayWebService port = service.getHolidayWebServicePort();
        return port.retrieveBookingByIdForPartner(arg0, arg1);
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
