/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hotelmanagementclient;

import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import static enums.EmployeeTypeEnum.OPERATIONSMANAGER;
import enums.RateTypeEnum;
import exceptions.RoomExistException;
import exceptions.RoomNotFoundException;
import exceptions.RoomRateExistException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeExistException;
import exceptions.RoomTypeNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matthealoo
 */
class HotelOperationModule {

    private RoomControllerRemote roomControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;

    public HotelOperationModule() {
    }

    public HotelOperationModule(RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote) {
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
    }

    public void runHotelOperationsModule(Employee employee) throws RoomTypeNotFoundException, RoomNotFoundException, RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        if (employee.getEmployeeType().equals(OPERATIONSMANAGER)) { //Operations Manager
            while (true) {
                System.out.println("*** HoRS :: Hotel Operations :: Operations Manager ***\n");

                System.out.println("Room Type Management");
                System.out.println();
                System.out.println("1. Create new room type");
                System.out.println("2. View room type details");
                System.out.println("3. View all room types");

                System.out.println("******************************************************************");
                System.out.println();

                System.out.println("Room Management");
                System.out.println();
                System.out.println("4. Create new room");
                System.out.println("5. Update room");
                System.out.println("6. Delete room");
                System.out.println("7. View all rooms");

                System.out.println("******************************************************************");
                System.out.println();

                System.out.println("8. View room allocation Exception Report"); //show type 1 and type 2
                System.out.println("9. Exit");

                int choice = sc.nextInt();
                if (choice == 1) {
                    doCreateRoomType();
                } else if (choice == 2) {
                    doViewRoomTypeDetails();
                } else if (choice == 3) {
                    doViewAllRoomTypes();

                } else if (choice == 4) {
                    doCreateRoom();
                } else if (choice == 5) {
                    doUpdateRoomDetails();
                } else if (choice == 6) {
                    doDeleteRoom();
                } else if (choice == 7) {
                    doViewAllRoom();
                } else if (choice == 8) {
                    doRoomAllocationExceptionReport();
                } else if (choice == 9) {
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        } else {                //Sales Manager
            while (true) {
                System.out.println("*** HoRS :: Hotel Operations :: Sales Manager ***\n");

                System.out.println("1. Create new room rate");
                System.out.println("2. View room rate details");
                System.out.println("3. View all room rates");

                System.out.println("4. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doCreateRoomRate();
                } else if (choice == 2) {
                    doViewAllRoomRate();
                } else if (choice == 3) {
                    doViewRoomRateDetails(Long.MIN_VALUE);
                } else if (choice == 4) {
                    break;
                } else {
                    System.out.println("Invalid entry. Please try again");
                }
            }
        }
    }

    //when we create room type we don't attach room rate yet
    private void doCreateRoomType() {
        RoomType roomType = new RoomType();
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("*** HoRS ::Hotel Operations :: Create New Room Type ***\n");
            System.out.print("Enter room type name> ");
            roomType.setName(scanner.nextLine().trim());
            System.out.print("Enter description> ");
            roomType.setDescription(scanner.nextLine().trim());
            System.out.print("Enter size of room type> ");
            roomType.setSize(scanner.nextInt());
            System.out.print("Enter bed details> ");
            roomType.setBed(scanner.nextLine().trim());
            System.out.print("Enter capacity> ");
            roomType.setCapacity(scanner.nextLine().trim());
            System.out.print("Enter amenities> ");
            roomType.setAmenities(scanner.nextLine().trim());
            roomType.setIsEnabled(Boolean.FALSE); //since no room rate yet
            roomType.setIsUsed(Boolean.FALSE); //since no one can book yet

            roomType = roomTypeControllerRemote.createRoomType(roomType);

            System.out.println("New room type:  " + roomType.getName() + " created successfully!" + "\n");

        } catch (RoomTypeExistException ex) {
            System.out.println("An error has occurred while creating the new room type: " + ex.getMessage() + "!\n");
        }
    }

    private void doViewRoomTypeDetails() throws RoomTypeNotFoundException {
        System.out.println("*** HoRS ::Hotel Operations :: View Room Type Details ***\n");
        System.out.println("Enter the room type name: \n");

        Scanner sc = new Scanner(System.in);
        try {

            RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeByName(sc.nextLine().trim());
            System.out.println("Room Type Details: ");
            System.out.println("Name: " + roomType.getName());
            System.out.println("Description: " + roomType.getDescription());
            System.out.println("Bed: " + roomType.getBed());
            System.out.println("Capacity: " + roomType.getCapacity());
            System.out.println("Amenities: " + roomType.getAmenities());
            System.out.println("Size: " + roomType.getSize());
            System.out.println("Room rates: ");

            if (roomType.getRoomRates().size() == 0) {
                System.out.println("No room rates");
            } else {
                for (RoomRate roomRate : roomType.getRoomRates()) {
                    System.out.println("Rate Type: " + roomRate.getRateType());
                    System.out.println("Rate Name: " + roomRate.getName());
                    System.out.println("*************************************");
                }
            }

            System.out.println("Rooms: ");

            if (roomType.getRooms().size() == 0) {
                System.out.println("No rooms of this room type");
            } else {
                for (Room room : roomType.getRooms()) {
                    System.out.println("Room Number: " + room.getRoomNumber());
                }
                System.out.println("*************************************");

            }

            while (true) {
                System.out.println("Choose one of the following options");
                System.out.println("1. Update room type details");
                System.out.println("2. Delete room type");
                System.out.println("3. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doUpdateRoomTypeDetails(roomType);
                } else if (choice == 2) {
                    doDeleteRoomType(roomType);
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid option selected. Please try again");
                }
            }
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room type " + ex.getMessage() + "!\n");
        }
    }

    private void doUpdateRoomTypeDetails(RoomType roomType) {
        System.out.println("*** HoRS ::Hotel Operations :: Editing Room Type Details ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setName(input);
        }

        System.out.print("Enter Description (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setDescription(input);
        }

        System.out.print("Enter Bed Details (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setBed(input);
        }

        System.out.print("Enter Capacity (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setCapacity(input);
        }

        System.out.print("Enter Amenities (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomType.setAmenities(input);
        }

        System.out.print("Enter Size (blank if no change)> ");
        int newSize = scanner.nextInt();
        if (input.length() > 0) {
            roomType.setSize(newSize);
        }

        System.out.println("Existing Room Rates: ");
        if (roomType.getRoomRates().size() == 0) {
            System.out.println("No room rates");
        } else {
            for (RoomRate roomRate : roomType.getRoomRates()) {
                System.out.println("Room Rate Name: " + roomRate.getName());
                System.out.println("Room Rate Type: " + roomRate.getRateType());
                System.out.println("Rate Per Night: " + roomRate.getRatePerNight());

                System.out.println("Room Rate Start Date: " + roomRate.getStartDate());
                System.out.println("Room Rate End Date: " + roomRate.getEndDate());
                System.out.println("*************************************");
            }

            while (true) {
                System.out.println("Choose one of the following options");
                System.out.println("1. Add room rate to room type");
                System.out.println("2. Delete room rate from room type");
                System.out.println("3. Done editing room rates for room type");

                int choice = scanner.nextInt();
                if (choice == 1) {
                    roomType = addRoomRateToRoomType(roomType);
                } else if (choice == 2) {
                    removeRoomRateFromRoomType(roomType);
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid option selected. Please try again");
                }
            }

            roomTypeControllerRemote.updateRoomType(roomType);
            System.out.println("Room type " + roomType.getName() + " updated successfully! \n");
        }
    }

    private RoomType addRoomRateToRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);

        //get list of enabled room rates
        List<RoomRate> roomRatesEnabled = roomRateControllerRemote.retrieveAllEnabledRoomRates();
        List<Long> roomRateIdsToAdd = new ArrayList<Long>();
        for (RoomRate roomRate : roomRatesEnabled) {
            //print out
            if (!roomType.getRoomRates().contains(roomRate)) {
                System.out.println("Room Rate Name: " + roomRate.getName());
                System.out.println("Room Rate Type: " + roomRate.getRateType());
                System.out.println("Rate Per Night: " + roomRate.getRatePerNight());

                System.out.println("Room Rate Start Date: " + roomRate.getStartDate());
                System.out.println("Room Rate End Date: " + roomRate.getEndDate());

                System.out.println("*********************************************************************");
                //request linking

                System.out.println("Add room rate?  (Enter 'Y' to add) ");
                if (scanner.nextLine().trim().equals("Y")) {
                    System.out.println("Added room rate  " + roomRate.getName() + " !");

                    roomRateIdsToAdd.add(roomRate.getRoomRateId());
                }
            }
        }
        //link room rate to room type using roomtype controller. send in roomtype id, room rate id
        //print success
        roomType = roomTypeControllerRemote.updateRoomTypeAddRoomRate(roomType, roomRateIdsToAdd);
        return roomType;
    }

    private RoomType removeRoomRateFromRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);
        List<RoomRate> roomRates = roomType.getRoomRates();
        List<Long> roomRateIdsToRemove = new ArrayList<Long>();

        if (roomRates.size() == 0) {
            System.out.println("No room rates to delete");
        } else {
            for (RoomRate roomRate : roomRates) {
                System.out.println("Room Rate Name: " + roomRate.getName());
                System.out.println("Room Rate Type: " + roomRate.getRateType());
                System.out.println("Rate Per Night: " + roomRate.getRatePerNight());

                System.out.println("Room Rate Start Date: " + roomRate.getStartDate());
                System.out.println("Room Rate End Date: " + roomRate.getEndDate());
                System.out.println("*************************************");
                System.out.println("Remove room rate?  (Enter 'Y' to remove) ");
                if (scanner.nextLine().trim().equals("Y")) {
                    System.out.println("Removed room rate  " + roomRate.getName() + " !");
                    roomRateIdsToRemove.add(roomRate.getRoomRateId());
                }
            }

        }
            roomType = roomTypeControllerRemote.updateRoomTypeRemoveRoomRate(roomType, roomRateIdsToRemove);
            return roomType;
        }

    

    private void doDeleteRoomType(RoomType roomType) {
        if (roomType.getIsUsed() == false) {
            roomType.setIsEnabled(Boolean.FALSE);
            roomTypeControllerRemote.deleteRoomType(roomType);
            System.out.println("Room type successfully deleted! ");

        } else {
            System.out.println("Room type " + roomType.getName() + "is in use. Unable to delete room type record");
        }
    }

    private void doViewAllRoomTypes() throws RoomTypeNotFoundException {
        System.out.println("*** HoRS :: Hotel Operations :: View All Room Types ***\n");

        List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllRoomtype();
        for (RoomType roomType : roomTypes) {
            System.out.println(roomType.getRoomTypeId() + ". " + roomType.getName());
        }
        System.out.println("*********************************************************************");
    }

    private void doCreateRoom() {
        Room room = new Room();
        Scanner scanner = new Scanner(System.in);
        Long roomTypeId;
        try {
            System.out.println("*** HoRS ::Hotel Operations :: Create New Room ***\n");
            System.out.print("Enter room number (4 digits only): ");
            room.setRoomNumber(scanner.nextLine().trim());
            System.out.print("Select room type: ");
            List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllEnabledRoomType();

            for (int i = 0; i < roomTypes.size(); i++) {
                System.out.println((i + 1) + ". " + roomTypes.get(i).getName());
            }
            while (true) {
                int input = scanner.nextInt();
                if (input >= 0 && input < roomTypes.size()) {
                    roomTypeId = roomTypes.get(input).getRoomTypeId();
                    break;
                } else {
                    System.out.println("Incorrect input, please try again.");
                }
            }
            room.setIsVacant(Boolean.TRUE);
            room = roomControllerRemote.createRoom(room, roomTypeId);

            System.out.println("New room:  " + room.getRoomNumber() + " created successfully!" + "\n");

        } catch (RoomExistException ex) {
            System.out.println("An error has occurred while creating the new room: " + ex.getMessage() + "!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving existing room type: " + ex.getMessage() + "!\n");
        }
    }

    private void doUpdateRoomDetails() {
        System.out.println("*** HoRS ::Hotel Operations :: Editing Room Details ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;
        Long oldroomTypeId;
        Long newroomTypeId;

        try {
            System.out.println("Which room would you like to edit details of?  (Enter room number) ");
            input = scanner.nextLine().trim();
            Room room = roomControllerRemote.retrieveRoomByRoomNum(input);
            oldroomTypeId = room.getRoomType().getRoomTypeId();
            newroomTypeId = room.getRoomType().getRoomTypeId();

            System.out.println("Current Details: ");
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType().getName());
            System.out.println("*********************************************************************");

            System.out.println("Enter New Room Number (blank if no change)> ");
            input = scanner.nextLine().trim();
            if (input.length() > 0) {
                room.setRoomNumber(input);
            }

            System.out.println("Change room status?  (Enter 'Y' to change) ");

            if (scanner.nextLine().trim().equals("Y")) {
                System.out.println("Set to vacant?  (Enter 'Y' to set) ");
                if (scanner.nextLine().trim().equals("Y")) {
                    room.setIsVacant(Boolean.TRUE);
                } else {
                    room.setIsVacant(Boolean.FALSE);
                }
            }
            System.out.println("Change room type?  (Enter 'Y' to change) ");

            if (scanner.nextLine().trim().equals("Y")) {
                System.out.print("Select room type: ");
                List<RoomType> roomTypes = roomTypeControllerRemote.retrieveAllEnabledRoomType();

                for (int i = 0; i < roomTypes.size(); i++) {
                    System.out.println((i + 1) + ". " + roomTypes.get(i).getName());
                }
                while (true) {
                    int choice = scanner.nextInt();
                    choice--;
                    if (choice >= 0 && choice < roomTypes.size()) {
                        newroomTypeId = roomTypes.get(choice).getRoomTypeId();
                        break;
                    } else {
                        System.out.println("Incorrect input, please try again.");
                    }
                }

                roomControllerRemote.updateRoom(room.getRoomId(), oldroomTypeId, newroomTypeId);
                System.out.println("Room updated successfully!: \n");
            }
        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room " + ex.getMessage() + "!\n");
        } catch (RoomTypeNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room type " + ex.getMessage() + "!\n");
        }
    }

    private void doDeleteRoom() {

        System.out.println("*** HoRS ::Hotel Operations :: Deleting Room Details ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;

        try {
            System.out.println("Which room would you like to delete?  (Enter room number) ");
            input = scanner.nextLine().trim();
            Room room = roomControllerRemote.retrieveRoomByRoomNum(input);

            System.out.println("Current Details: ");
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType().getName());
            System.out.println("*********************************************************************");

            if (room.getIsVacant()) {
                System.out.println("Delete room?  (Enter 'Y' to change) ");
                if (scanner.nextLine().trim().equals("Y")) {
                    roomControllerRemote.deleteRoom(room.getRoomId());
                    System.out.println("Successfully deleted room record.");
                }
            } else {
                System.out.println("Room is occupied, unable to delete room record.");
            }

        } catch (RoomNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room " + ex.getMessage() + "!\n");
        }
    }

    private void doViewAllRoom() throws RoomNotFoundException {
        System.out.println("*** HoRS ::Hotel Operations :: View All Rooms ***\n");
        List<Room> list = roomControllerRemote.retrieveAllRooms();

        for (Room room : list) {
            System.out.println("Room Number: " + room.getRoomNumber());
            System.out.println("Room Type: " + room.getRoomType());
            System.out.println("*********************************************************************");
        }

    }

    private void doRoomAllocationExceptionReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCreateRoomRate() {
        RoomRate roomRate = new RoomRate();
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("*** HoRS ::Hotel Operations :: Create New Room Rate ***\n");
            System.out.print("Enter name of room rate: ");
            roomRate.setName(scanner.nextLine().trim());
            System.out.print("Select rate type: ");
            System.out.println("1. Published");
            System.out.println("2. Normal");
            System.out.println("3. Peak");
            System.out.println("4. Promo");

            while (true) {
                int choice = scanner.nextInt();

                if (choice == 1) {
                    roomRate.setRateType(RateTypeEnum.PUBLISHED);
                    roomRate.setForPartner(Boolean.FALSE);
                    break;
                } else if (choice == 2) {
                    roomRate.setRateType(RateTypeEnum.NORMAL);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else if (choice == 3) {
                    roomRate.setRateType(RateTypeEnum.PEAK);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else if (choice == 2) {
                    roomRate.setRateType(RateTypeEnum.PROMO);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else {
                    System.out.println("Incorrect input, please try again.");
                }
            }

            System.out.println("Enter Rate Per Night: ");
            roomRate.setRatePerNight(scanner.nextBigDecimal());

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Enter start date (format: dd/mm/yyyy) >");
            String startDate = scanner.nextLine().trim();
            try {
                formatter.parse(startDate);
            } catch (ParseException ex) {
                System.out.println("Incorrect date format.");
            }

            System.out.println("Enter end date (format: dd/mm/yyyy) >");
            String endDate = scanner.nextLine().trim();

            try {
                formatter.parse(endDate);
            } catch (ParseException ex) {
                System.out.println("Incorrect date format.");
            }
            roomRate.setIsUsed(Boolean.FALSE);
            roomRate.setIsEnabled(Boolean.TRUE);
            roomRate = roomRateControllerRemote.createRoomRate(roomRate);

            System.out.println("New room rate:  " + roomRate.getName() + " created successfully!" + "\n");

        } catch (RoomRateExistException ex) {
            System.out.println("An error has occurred while creating the new room rate: " + ex.getMessage() + "!\n");
        }

    }

    private void doViewAllRoomRate() throws RoomRateNotFoundException {
        System.out.println("*** HoRS ::Hotel Operations :: View All Room Rates ***\n");
        List<RoomRate> list = roomRateControllerRemote.retrieveAllRoomRates();

        for (RoomRate roomRate : list) {
            System.out.println("Room Rate Name: " + roomRate.getName());
            System.out.println("Room Rate Type: " + roomRate.getRateType());
            System.out.println("Rate Per Night: " + roomRate.getRatePerNight());

            System.out.println("Room Rate Start Date: " + roomRate.getStartDate());
            System.out.println("Room Rate End Date: " + roomRate.getEndDate());

            System.out.println("*********************************************************************");
        }
    }

    private void doViewRoomRateDetails(Long option) throws RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS ::Hotel Operations :: View Room Rate Details ***\n");
        String input;
        try {
            System.out.println("Which room rate would you like to view details of?  (Enter room rate name) ");
            input = sc.nextLine().trim();
            RoomRate roomRate = roomRateControllerRemote.retrieveRoomRateByName(input);

            System.out.println("Room Rate Details: ");
            System.out.println("Room Rate Name: " + roomRate.getName());
            System.out.println("Room Rate Type: " + roomRate.getRateType());
            System.out.println("Rate Per Night: " + roomRate.getRatePerNight());

            System.out.println("Room Rate Start Date: " + roomRate.getStartDate());
            System.out.println("Room Rate End Date: " + roomRate.getEndDate());
            System.out.println("*********************************************************************");
            while (true) {
                System.out.println("Choose following options");
                System.out.println("1. Update room rate details");
                System.out.println("2. Delete room rate");
                System.out.println("3. Exit");
                int choice = sc.nextInt();
                if (choice == 1) {
                    doUpdateRoomRateDetails(roomRate);
                } else if (choice == 2) {
                    doDeleteRoomRate(roomRate);
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid option selected. Please try again");
                }
            }
        } catch (RoomRateNotFoundException ex) {
            System.out.println("An error has occurred while retrieving the room rate" + ex.getMessage() + "!\n");
        }
    }

    private void doUpdateRoomRateDetails(RoomRate roomRate) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** HoRS ::Hotel Operations :: Edit Room Rate Details ***\n");

        String input;
        System.out.println("Enter New Room Rate Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if (input.length() > 0) {
            roomRate.setName(input);
        }

        System.out.println("Change rate type?  (Enter 'Y' to change) ");
        if (scanner.nextLine().trim().equals("Y")) {
            System.out.print("Select rate type: ");
            System.out.println("1. Published");
            System.out.println("2. Normal");
            System.out.println("3. Peak");
            System.out.println("4. Promo");

            while (true) {
                int choice = scanner.nextInt();

                if (choice == 1) {
                    roomRate.setRateType(RateTypeEnum.PUBLISHED);
                    roomRate.setForPartner(Boolean.FALSE);
                    break;
                } else if (choice == 2) {
                    roomRate.setRateType(RateTypeEnum.NORMAL);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else if (choice == 3) {
                    roomRate.setRateType(RateTypeEnum.PEAK);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else if (choice == 2) {
                    roomRate.setRateType(RateTypeEnum.PROMO);
                    roomRate.setForPartner(Boolean.TRUE);

                    break;

                } else {
                    System.out.println("Incorrect input, please try again.");
                }
            }
            System.out.println("Change Rate Per Night?  (Enter 'Y' to change) ");
            if (scanner.nextLine().trim().equals("Y")) {
                System.out.println("Enter Rate Per Night: ");
                roomRate.setRatePerNight(scanner.nextBigDecimal());
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Change start date?  (Enter 'Y' to change) ");
            if (scanner.nextLine().trim().equals("Y")) {
                System.out.println("Enter start date (format: dd/mm/yyyy) >");
                String startDate = scanner.nextLine().trim();
                try {
                    formatter.parse(startDate);
                } catch (ParseException ex) {
                    System.out.println("Incorrect date format.");
                }
            }
            System.out.println("Change end date?  (Enter 'Y' to change) ");
            if (scanner.nextLine().trim().equals("Y")) {
                System.out.println("Enter end date (format: dd/mm/yyyy) >");
                String endDate = scanner.nextLine().trim();

                try {
                    formatter.parse(endDate);
                } catch (ParseException ex) {
                    System.out.println("Incorrect date format.");
                }
            }
        }
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        System.out.println("*** HoRS ::Hotel Operations :: Deleting Room Rate ***\n");
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Which room rate would you like to delete?  (Enter room rate name) ");
        input = scanner.nextLine().trim();

        if (!roomRate.getIsUsed()) {
            System.out.println("Delete room rate?  (Enter 'Y' to change) ");
            if (scanner.nextLine().trim().equals("Y")) {
                roomRateControllerRemote.deleteRoomRate(roomRate.getRoomRateId());
                System.out.println("Successfully deleted room rate record.");
            }
        } else {
            System.out.println("Room rate is used, unable to delete room rate record.");
        }
    }
}
