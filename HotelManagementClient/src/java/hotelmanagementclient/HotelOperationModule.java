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
import exceptions.RoomNotFoundException;
import exceptions.RoomRateNotFoundException;
import exceptions.RoomTypeExistException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import java.util.Scanner;

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
            roomType.setBed(scanner.nextLine().trim());
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
        System.out.println("Enter the room type id: \n");

        Scanner sc = new Scanner(System.in);
        try {
            Long option = sc.nextLong();
            RoomType roomType = roomTypeControllerRemote.retrieveRoomTypeById(option);
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
                System.out.println("Rate Type: " + roomRate.getRateType());
                System.out.println("Rate Name: " + roomRate.getName());
                System.out.println("*************************************");
            }

            while (true) {
                System.out.println("Choose one of the following options");
                System.out.println("1. Add room rate to room type");
                System.out.println("2. Delete room rate from room type");
                System.out.println("3. Exit");

                int choice = scanner.nextInt();
                if (choice == 1) {
                    addRoomRateToRoomType(roomType);
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

    private void addRoomRateToRoomType(RoomType roomType) {
        //get list of enabled room rates
        //print out
        //request linking
        //link room rate to room type using roomtype controller. send in roomtype id, room rate id
        //print success

    }

    private void removeRoomRateFromRoomType(RoomType roomType) {

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllRoom() throws RoomNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter ID of room for more details(Enter 0 to exit)");
            List<Room> roomList = roomControllerRemote.retrieveAllRooms();
            String title1 = "Room ID";
            String title2 = "Room Number";
            System.out.printf("%-15s %-15s %n", title1, title2);
            for (int i = 0; i < roomList.size(); i++) {
                System.out.printf("%-15d %-15s %n", roomList.get(i).getRoomId(), roomList.get(i).getRoomNumber());
            }
        }
    }

    private void doRoomAllocationExceptionReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCreateRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllRoomRate() throws RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter ID of room type for more details(Enter 0 to exit)");
            List<RoomRate> roomRateList = roomRateControllerRemote.retrieveAllRoomRates();
            String title1 = "Room rate ID";
            String title2 = "Room rate";
            System.out.printf("%-15s %-15s %n", title1, title2);
            for (int i = 0; i < roomRateList.size(); i++) {
                System.out.printf("%-15d %n", roomRateList.get(i).getRoomRateId());
            }
            Long option = sc.nextLong();
            if (option == 0) {
                break;
            } else {
                doViewRoomRateDetails(option);
            }
        }
    }

    

    private void doUpdateRoomDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewRoomRateDetails(Long option) throws RoomRateNotFoundException {
        Scanner sc = new Scanner(System.in);
        RoomRate roomRate = roomRateControllerRemote.retrieveRoomRateById(option);
        System.out.println("Print out room rate details here");
        while (true) {
            System.out.println("Choose following options");
            System.out.println("1. Update room details");
            System.out.println("2. Delete room");
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
    }

    private void doUpdateRoomRateDetails(RoomRate roomRate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
