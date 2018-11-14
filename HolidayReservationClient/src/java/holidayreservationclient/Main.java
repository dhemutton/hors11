/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationclient;

import ejb.session.stateless.WebServiceSessionBeanRemote;
import java.math.BigDecimal;
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
import java.util.InputMismatchException;
import javax.ejb.EJB;
import static ws.session.BookingStatusEnum.PENDING;
import static ws.session.BookingTypeEnum.ONLINE;
import static ws.session.ExceptionTypeEnum.UNASSIGNED;
import ws.session.RoomType;

/**
 *
 * @author matthealoo
 */
public class Main {

    @EJB
    private static WebServiceSessionBeanRemote webServiceSessionBean;

    
    private static Boolean loggedIn = false;
    private static Partner partner;

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runApp();
    }

    public static void runApp() {
        /**
         * @param args the command line arguments
         */
        System.out.println("*** Welcome to Holiday Reservation Client  ***\n");
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                while (!loggedIn) {
                    System.out.println("1. Partner Login");
                    System.out.println("2. Search Hotel Room");
                    System.out.println("3. Exit");
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

                while (loggedIn && partner.isIsManager()) {

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
                        webServiceSessionBean.updatePartnerLoginWS(partner, false);
                        break;
                    } else {
                        System.out.println("Invalid entry. Please try again");
                    }
                }
            } catch (InputMismatchException ex) {
                System.out.println("Input mismatch. Please try again.");
            }
        }
    }

    private static void doPartnerLogin() {
        System.out.println("*** HoRS :: Holiday Reservation Client :: Login As Partner ***\n");

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username: ");
        String username = sc.nextLine().trim();
        System.out.println("Please enter password");
        String password = sc.nextLine().trim();

        try {
            partner = webServiceSessionBean.loginForPartnerWS(username, password);
            if (!partner.isIsLogin()) {
                loggedIn = true;
                webServiceSessionBean.updatePartnerLoginWS(partner, true);
                System.out.println("Login successful! Redirecting...");

            } else {
                System.out.println("Partner employee is already logged in.");
            }
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving guest: " + ex.getMessage() + "\n");
        } catch (InvalidLoginCredentials_Exception ex) {
            System.out.println("Invalid password entered. Please try again");
        }
    }

    private static void doSearchRoom() {
        System.out.println("*** HoRS :: Holiday Reservation Client :: Search Room ***\n");

        Scanner sc = new Scanner(System.in);
        Date startDate = null, endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        int maxRooms = webServiceSessionBean.retrieveAllEnabledRoomsWS().size();
        System.out.println("Please enter start date (dd/mm/yyyy");
        Boolean again = true;

        while (again) {
            String start = sc.nextLine().trim();
            if (start.length() == 10) {
                try {
                    startDate = formatter.parse(start);
                    again = false;
                } catch (ParseException ex) {
                    again = true;
                    System.out.println("Incorrect date format.");
                }
            } else {
                again = true;
                System.out.println("Incorrect date format.");
            }
        }

        System.out.println("Enter end date (format: dd/mm/yyyy) >");
        again = true;
        while (again) {
            String end = sc.nextLine().trim();
            if (end.length() == 10) {
                try {
                    endDate = formatter.parse(end);
                    if (startDate.before(endDate) || startDate.equals(endDate)) {
                        again = false;
                    } else {
                        again = true;
                        System.out.println("End date is before start date! Please re-enter end date.");
                    }
                } catch (ParseException ex) {
                    again = true;
                    System.out.println("Incorrect date format.");
                }
            } else {
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

            List<Booking> bookingList = webServiceSessionBean.retrieveAllBookingsWithinDatesWS(startXmlCalendar, endXmlCalendar);
            for (Booking booking : bookingList) {
                reservationList.addAll(webServiceSessionBean.retrieveAllReservationFromBookingWS(booking.getBookingId()));
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

    private static void doReserveRoom(int roomsLeft, Date startDate, Date endDate) {
        BigDecimal totalCost = new BigDecimal(0);
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
            booking = webServiceSessionBean.createNewBookingWS(booking);

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
                System.out.println("Select room type to reserve for reservation " + (i + 1) + ": ");
                List<RoomType> roomTypeList = webServiceSessionBean.retrieveAllEnabledRoomTypeWS();
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

                reservation = webServiceSessionBean.createNewReservationWS(reservation);
                booking.getReservation().add(reservation);
                totalCost.add(webServiceSessionBean.calculateReservationCostWS(booking, reservation.getInitialRoomType()));
            }
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Data type conversion to XML Gregorian Calendar error!");

        }
        System.out.println("Total Cost: " + totalCost);
        booking.setCost(totalCost);
        webServiceSessionBean.updateBookingWS(booking);
        System.out.println("Reservation created! Reservation id : " + booking.getBookingId());
    }

    private static void doViewMyReservation(Long partnerId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Holiday Reservation Client :: View My Reservation ***\n");
        System.out.println("Which reservation would you like to view?  (Enter reservation id) ");
        Long bookingId = scanner.nextLong();
        try {
            Booking booking = webServiceSessionBean.retrieveBookingByIdForPartnerWS(bookingId, partnerId);
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Start Date: " + booking.getStartDate());
            System.out.println("End Date: " + booking.getEndDate());
            System.out.println("Booking Type: " + booking.getBookingType());
            System.out.println("Booking Status: " + booking.getBookingStatus());
            System.out.println("Total Cost: " + booking.getCost());
            List<Reservation> reservations = webServiceSessionBean.retrieveAllReservationFromBookingWS(booking.getBookingId());
            List<RoomType> ranking = webServiceSessionBean.retrieveAllRoomtypeWS();
            int[] quantityEach = new int[ranking.size()];
            for (int i = 0; i < quantityEach.length; i++) {
                quantityEach[i] = 0;
            }
            for (Reservation reservation : reservations) {
                int rank = reservation.getInitialRoomType().getRanking();
                rank--;
                quantityEach[rank]++;
            }
            System.out.println("\nRooms reserved:");
            for (int i = 0; i < quantityEach.length; i++) {
                System.out.println(ranking.get(i).getName() + ": " + quantityEach[i]);
            }
            System.out.println("\nTotal number of rooms reserved: " + reservations.size());
            System.out.println("*********************************************************************");
            System.out.println();

        } catch (BookingNotFoundException_Exception ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }
    }

    private static void doViewAllMyReservation(Long partnerId) {
        System.out.println("*** HoRS :: Holiday Reservation Client :: View All My Reservations ***\n");

        List<Booking> list = webServiceSessionBean.retrieveAllBookingsForPartnerWS(partnerId);
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
                List<Reservation> reservations = webServiceSessionBean.retrieveAllReservationFromBookingWS(list.get(i).getBookingId());
                System.out.println("Number of rooms reserved: " + reservations.size());
                for (int j = 0; j < reservations.size(); j++) {
                    System.out.println("Room Type: " + reservations.get(j).getInitialRoomType().getName());
                }
                System.out.println("*********************************************************************");
                System.out.println();
            }
        }
    }

   
}
