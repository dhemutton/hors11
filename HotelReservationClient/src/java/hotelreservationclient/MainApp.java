package hotelreservationclient;

import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.GuestControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Booking;
import entity.Guest;
import entity.Reservation;
import entity.RoomType;
import static enums.BookingStatusEnum.PENDING;
import static enums.BookingTypeEnum.ONLINE;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class MainApp {

    private GuestControllerRemote guestControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;
    private BookingControllerRemote bookingControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private Boolean loggedIn = false;
    private Guest guest;

    public MainApp(GuestControllerRemote guestControllerRemote, ReservationControllerRemote reservationControllerRemote, BookingControllerRemote bookingControllerRemote, RoomControllerRemote roomControllerRemote,
            RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.guestControllerRemote = guestControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }

    public MainApp() {
    }

    public void runApp() throws BookingNotFoundException, RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException, EmployeeExistException, ReservationNotFoundException {
        Scanner sc = new Scanner(System.in);
        outerloop:
        while (true) {
            try {
                while (!loggedIn) {
                    System.out.println("*** Welcome to HoRS Reservation Client  ***\n");

                    System.out.println("1. Guest Login");
                    System.out.println("2. Register as Guest");
                    System.out.println("3. Search Hotel Room");
                    System.out.println("4. Exit");
                    System.out.println();
                    int choice = sc.nextInt();
                    sc.nextLine();

                    if (choice == 1) {
                        doGuestLogin();
                    } else if (choice == 2) {
                        doRegisterAsGuest();
                    } else if (choice == 3) {
                        doSearchRoom();
                    } else if (choice == 4) {
                        System.out.println("(╯°□°）╯︵ ┻━┻)");

                        System.out.println("Goodbye~");

                        break outerloop;
                    } else {
                        System.out.println("Invalid entry. Please try again");
                    }
                }

                while (loggedIn) {
                    System.out.println("***   Welcome, " + guest.getFirstName() + "!  ***\n");
                    System.out.println("*** What would you like to do?  ***\n");

                    System.out.println("1. Search Hotel Room");
                    System.out.println("2. View My Reservation Details");
                    System.out.println("3. View All My Reservations");
                    System.out.println("4. Logout");
                    int choice = sc.nextInt();
                    sc.nextLine();
                    if (choice == 1) {
                        doSearchRoom();
                    } else if (choice == 2) {
                        doViewMyReservation(guest.getGuestId());
                    } else if (choice == 3) {
                        doViewAllMyReservation(guest.getGuestId());
                    } else if (choice == 4) {
                        loggedIn = false;
                        guestControllerRemote.updateGuestLogin(guest, false);
                        System.out.println("Logging out...");
                        System.out.println();
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
                System.out.println();
                System.out.println("Login successful! Redirecting...");
                System.out.println();

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
            System.out.println("Enter Email Address> ");
            newGuest.setEmail(scanner.nextLine().trim());
            System.out.println("Enter password> ");
            newGuest.setPassword(scanner.nextLine().trim());
            System.out.println("Enter First Name> ");
            newGuest.setFirstName(scanner.nextLine().trim());
            System.out.println("Enter Last Name> ");
            newGuest.setLastName(scanner.nextLine().trim());
            System.out.println("Enter Contact Number> ");
            newGuest.setContactNumber(scanner.nextLine().trim());

            newGuest.setIsLogin(Boolean.FALSE);
            newGuest = guestControllerRemote.createGuest(newGuest);
            System.out.println("New guest was created successfully!" + "\n");
        } catch (GuestExistException ex) {
            System.out.println("An error has occurred while creating the new guest: " + ex.getMessage() + "!\n");
        }
    }

    private void doSearchRoom() {
        System.out.println("*** HoRS :: Reservation Client :: Search Room ***\n");
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        Scanner sc = new Scanner(System.in);
        Date startDate = null, endDate = null;
        List<Reservation> reservationList = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        while (true) {
            System.out.println("Please enter start date (dd/mm/yyyy):");
            String start = sc.nextLine().trim();
            if (start.length() == 10) {
                try {
                    startDate = formatter.parse(start);
                    if(startDate.after(today)) {
                        break;
                    }
                    else {
                        System.out.println("Please enter a date starting after "+today);
                    }
                    
                } catch (ParseException ex) {
                    System.out.println("Incorrect date format.");
                }
            } else {
                System.out.println("Incorrect date format.");
            }
        }
        
        while (true) {
            System.out.println("Enter end date (format: dd/mm/yyyy):");
            String end = sc.nextLine().trim();
            if (end.length() == 10) {
                try {
                    endDate = formatter.parse(end);
                    if (startDate.before(endDate)) {
                        break;
                    } else {
                        System.out.println("End date is before or equal to start date! Please re-enter end date.");
                    }
                } catch (ParseException ex) {;
                    System.out.println("Incorrect date format.");
                }
            } else {
                System.out.println("Incorrect date format.");
            }
        }

        List<Booking> bookingList = bookingControllerRemote.retrieveAllBookingsWithinDates(startDate, endDate);
        for (Booking booking : bookingList) {
            reservationList.addAll(reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId()));
        }
        
        List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllEnabledAndIsUsedRoomType();

        //EDITED to show room type inventory
        HashMap<Long, Integer> map = new HashMap<>();
        for (RoomType rt : roomTypeList) {
            int maxRoomInventory = roomControllerRemote.retrieveAllEnabledRoomsFromRoomType(rt).size();
            map.put(rt.getRoomTypeId(), maxRoomInventory);
        }

        for (Reservation r : reservationList) {
            Long id = r.getInitialRoomType().getRoomTypeId();
            map.put(id, map.get(id) - 1);
        }
        int roomsLeft = 0;

        List<Long> availableRoomTypeIds = new ArrayList<>();
        for (RoomType rt : roomTypeList) {
            int vacancy = map.get(rt.getRoomTypeId());
            if (vacancy > 0) {
                roomsLeft = roomsLeft + vacancy;
                availableRoomTypeIds.add(rt.getRoomTypeId());
            }
        }
        System.out.println();
        System.out.println("Displaying Available Room Types...");
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %30s %25s %23s ", "ID", "ROOM TYPE NAME", "AVAILABLE ROOMS", "TOTAL COST/ROOM");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------------------------");
        for (RoomType rt : roomTypeList) {
            Booking booking = new Booking(ONLINE, PENDING, startDate, endDate);

            try {
                System.out.format("%-5s %30s %17s %27.2f ",
                        rt.getRoomTypeId(), rt.getName(), map.get(rt.getRoomTypeId()), roomRateControllerRemote.calculateReservationCost(booking, roomTypeControllerRemote.retrieveRoomTypeById(rt.getRoomTypeId())));
                System.out.println();
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room Type not found!");
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------");

        if (availableRoomTypeIds.size() > 0) {
            if (loggedIn) {
                System.out.println("Would you like to make a reservation? (Enter 'Y' to reserve)");
                if (sc.nextLine().trim().equals("Y")) {
                    doReserveRoom(map, startDate, endDate, roomsLeft);
                } else {
                    System.out.println();
                }
            } else {
                System.out.println("Login to make a reservation.");
            }
        } else {
            System.out.println("No more rooms are available during this period");
        }
    }

    private void doReserveRoom(HashMap<Long, Integer> map, Date startDate, Date endDate, int roomsLeft) {
        System.out.println("*** HoRS :: Reservation Client :: Room Reservation ***\n");
        HashMap<Long, Integer> choiceMap = new HashMap<>();
        for (Long potentialChoiceId : map.keySet()) {
            choiceMap.put(potentialChoiceId, 0);
        }
        List<Reservation> choiceReservationList = new ArrayList<>();
        BigDecimal totalCost = new BigDecimal(0);
        Scanner sc = new Scanner(System.in);
        int quantity = 0;
        Booking booking = new Booking(ONLINE, PENDING, startDate, endDate);
        booking.setGuest(guest);
        while (true) {
            try {
                System.out.println("How many rooms do you want to reserve? (Maximum: " + roomsLeft + ")");
                quantity = sc.nextInt();
                if (quantity <= 0 || quantity > roomsLeft) {
                    System.out.println("Invalid entry. Please try again");
                } else {
                    break;
                }
            } catch (InputMismatchException ex) {
                System.out.println("Invalid entry. Please try again");
                sc.nextLine();
            }
        }

        sc.nextLine();
        List<RoomType> roomTypeList = roomTypeControllerRemote.retrieveAllEnabledAndIsUsedRoomType();

        for (int i = 0; i < quantity; i++) {
            int choice;
            while (true) {
                try {
                    System.out.println("Select room type to reserve for reservation (" + (i + 1) + "/" + quantity + "): ");

                    for (int j = 1; j <= roomTypeList.size(); j++) {
                        System.out.println(j + ". " + roomTypeList.get(j - 1).getName());
                    }
                    choice = sc.nextInt();
                    sc.nextLine();
                    if (choice < 1 || choice > roomTypeList.size()) {
                        System.out.println("Invalid entry. Please try again");
                    } else if (map.get(roomTypeList.get(choice - 1).getRoomTypeId()) == 0) {
                        System.out.println("Room Type " + roomTypeList.get(choice - 1).getName() + " is fully booked during this period. Please reserve a different room.");
                    } else {
                        System.out.println("Making a reservation for " + roomTypeList.get(choice - 1).getName() + ".  Cost : $" + roomRateControllerRemote.calculateReservationCost(booking, roomTypeList.get(choice - 1)));
                        System.out.println("Add to cart? (Enter 'Y' to confirm)");
                        if (sc.nextLine().trim().equals("Y")) {
                            map.put(roomTypeList.get(choice - 1).getRoomTypeId(), map.get(roomTypeList.get(choice - 1).getRoomTypeId()) - 1); //deduct room type inventory on map
                            choiceMap.put(roomTypeList.get(choice - 1).getRoomTypeId(), choiceMap.get(roomTypeList.get(choice - 1).getRoomTypeId()) + 1); //add to the choice cart
                            choiceReservationList.add(new Reservation(roomTypeList.get(choice - 1), booking, UNASSIGNED)); //add to reservation list
                            totalCost = totalCost.add(roomRateControllerRemote.calculateReservationCost(booking, roomTypeList.get(choice - 1)));
                            break;
                        }
                    }
                } catch (InputMismatchException ex) {
                    System.out.println("Invalid entry. Please try again");
                    sc.nextLine();

                }
            }
        }

        System.out.println();

        System.out.println(
                "Displaying Your Cart...");
        System.out.println(
                "-----------------------------------------------------------------------------------------------");
        System.out.printf(
                "%-30s %25s %23s ", "ROOM TYPE NAME", "NO. OF BOOKED ROOMS", "TOTAL COST/ROOM");
        System.out.println();

        System.out.println(
                "-----------------------------------------------------------------------------------------------");
        for (Long choiceId
                : choiceMap.keySet()) {
            try {
                if (choiceMap.get(choiceId) != 0) {
                    System.out.format("%-30s %18s %23.2f ",
                            roomTypeControllerRemote.retrieveRoomTypeById(choiceId).getName(), choiceMap.get(choiceId), roomRateControllerRemote.calculateReservationCost(booking, roomTypeControllerRemote.retrieveRoomTypeById(choiceId)));
                    System.out.println();
                }
            } catch (RoomTypeNotFoundException ex) {
                System.out.println("Room Type not found!");
            }
        }

        System.out.println(
                "-----------------------------------------------------------------------------------------------");

        System.out.println(
                "Final Cost: " + totalCost);
        System.out.println();

        System.out.println(
                "Confirm reservation? (Enter 'Y' to confirm)");
        if (sc.nextLine()
                .trim().equals("Y")) {
            booking.setCost(totalCost);
            booking = bookingControllerRemote.createNewBookingForGuest(booking); //input to database
            for (Reservation r : choiceReservationList) {
                r.setBooking(booking);
                reservationControllerRemote.createNewReservation(r); //input to database
            }
            System.out.println("Reservation created! Reservation id : " + booking.getBookingId());
        }
    }

    private void doViewMyReservation(Long guestId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS :: Reservation Client :: View My Reservation ***\n");
        System.out.println("Which reservation would you like to view?  (Enter reservation id) ");
        Long bookingId = scanner.nextLong();
        try {
            Booking booking = bookingControllerRemote.retrieveBookingByIdForGuest(bookingId, guestId);
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Start Date: " + booking.getStartDate());
            System.out.println("End Date: " + booking.getEndDate());
            System.out.println("Booking Type: " + booking.getBookingType());
            System.out.println("Booking Status: " + booking.getBookingStatus());
            System.out.println("Total Cost: " + booking.getCost());
            List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationFromBooking(booking.getBookingId());
            List<RoomType> ranking = roomTypeControllerRemote.retrieveAllRoomtype();
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

        } catch (BookingNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the reservation " + ex.getMessage() + "!\n");
        }
    }

    private void doViewAllMyReservation(Long guestId) {
        System.out.println("*** HoRS :: Reservation Client :: View All My Reservations ***\n");

        List<Booking> list = bookingControllerRemote.retrieveAllBookingsForGuest(guestId);
        if (list.size() == 0) {
            System.out.println("No past reservations made.");
            System.out.println();
        } else {
            for (int i = 0; i < list.size(); i++) {
                System.out.println((i + 1) + ". Booking ID: " + list.get(i).getBookingId());
                System.out.println("Start Date: " + list.get(i).getStartDate());
                System.out.println("End Date: " + list.get(i).getEndDate());
                System.out.println("Booking Type: " + list.get(i).getBookingType());
                System.out.println("Booking Status: " + list.get(i).getBookingStatus());
                System.out.println("Total Cost: " + list.get(i).getCost());
                List<Reservation> reservations = reservationControllerRemote.retrieveAllReservationFromBooking(list.get(i).getBookingId());

                List<RoomType> ranking = roomTypeControllerRemote.retrieveAllRoomtype();
                int[] quantityEach = new int[ranking.size()];
                for (int j = 0; j < quantityEach.length; j++) {
                    quantityEach[j] = 0;
                }
                for (Reservation reservation : reservations) {
                    int rank = reservation.getInitialRoomType().getRanking();
                    rank--;
                    quantityEach[rank]++;
                }
                System.out.println("\nRooms reserved:");
                for (int k = 0; k < quantityEach.length; k++) {
                    System.out.println(ranking.get(k).getName() + ": " + quantityEach[k]);
                }
                System.out.println("\nTotal number of rooms reserved: " + reservations.size());
                System.out.println("*********************************************************************");
                System.out.println();

            }
        }
    }

}
