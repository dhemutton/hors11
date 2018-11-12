package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import enums.ExceptionTypeEnum;
import static enums.RateTypeEnum.PEAK;
import static enums.RateTypeEnum.PROMO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class DailyController {

    @EJB
    private RoomRateControllerLocal roomRateController;

    @EJB
    private RoomControllerLocal roomController;

    @EJB
    private RoomTypeControllerLocal roomTypeController;

    @EJB
    private ReservationControllerLocal reservationController;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    @Schedule(minute = "*")
    public void dailyReservationRoomAssignment() {
        System.out.println("2am function activated");
        //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date day1 = new Date();
        Date day2 = new Date();
        day1.setDate(day2.getDate() - 1);
        day1.setHours(0);
        day1.setMinutes(0);
        day1.setSeconds(0);
        day2.setHours(0);
        day2.setMinutes(0);
        day2.setSeconds(0);
        System.out.println("Day 1: " + day1);
        System.out.println("Day 2: " + day2);

        //Obtain a list of all ending reservations(checking out)
        List<Reservation> day1ReservationList = reservationController.retrieveAllReservationFromEndDate(day1);
        System.out.println("Day 1 reservation list size: " + day1ReservationList.size());

        //Obtain a list of all starting reservations(checking in)
        List<Reservation> day2ReservationList = reservationController.retrieveAllReservationFromStartDate(day2);
        System.out.println("Day 2 reservation list size: " + day2ReservationList.size());
        //Obtain a list of all reservation that requires upgrades (deduct all assigned check in rooms from following method)
        List<Reservation> day2ReservationUpgrade = day2ReservationList;

        //Declare all rooms from ending reservations to be vacant
        for (Reservation reservation : day1ReservationList) {
            Room room = reservation.getRoom();
            room.setIsVacant(Boolean.TRUE);
            roomController.mergeRoom(room);
        }

        //Obtain all vacant rooms to assign rooms for day 2
        List<Room> vacantRoomList = roomController.retrieveAllVacantRooms();
        System.out.println("Vacant room list size: " + vacantRoomList.size());

        //Assign rooms to reservations for day 2
        List<RoomType> roomRanking = roomTypeController.retrieveAllRoomtype();

        //Room allocation(no upgrade)
        for (RoomType roomType : roomRanking) {
            //Get quantity of vacant rooms for specific room type
            List<Room> vacantRoomsByType = new ArrayList<>();
            for (Room room : vacantRoomList) {
                if (room.getRoomType().equals(roomType)) {
                    vacantRoomsByType.add(room);
                }
            }
            //Assign reservation to vacant room by room type
            for (Reservation reservation : day2ReservationList) {
                if (reservation.getInitialRoomType().equals(roomType)) {
                    Room room = vacantRoomsByType.get(0);
                    reservation.setRoom(room);
                    reservation.setExceptionType(ExceptionTypeEnum.NONE);
                    room.setIsVacant(Boolean.FALSE);
                    reservationController.updateReservation(reservation);
                    roomController.mergeRoom(room);
                    vacantRoomsByType.remove(room);
                    vacantRoomList.remove(room);
                    day2ReservationUpgrade.remove(reservation);
                    if (vacantRoomsByType.isEmpty()) {
                        break;
                    }
                }
            }
        }

        //Room allocation upgrade      
        for (RoomType roomType : roomRanking) {
            int upgradeLevel = roomType.getRanking() - 1;
            //If highest ranking level, auto-assign type 2 error
            if (roomType.getRanking() == 1) {
                for (Reservation reservation : day2ReservationUpgrade) {
                    if (reservation.getInitialRoomType().getRanking() == 1) {
                        reservation.setExceptionType(ExceptionTypeEnum.TYPE2);
                        reservationController.updateReservation(reservation);
                        day2ReservationUpgrade.remove(reservation);
                    }
                }
            } else {
                //Get quantity of reservation for specific room type
                List<Reservation> reservationsByType = new ArrayList<>();
                for (Reservation reservation : reservationsByType) {
                    if (reservation.getInitialRoomType().equals(roomType)) {
                        reservationsByType.add(reservation);
                    }
                }
                //Assign upgrades to all reservations for a specific room type
                for (Reservation reservation : reservationsByType) {
                    //Assign room rank of upgade
                    for (int i = upgradeLevel; upgradeLevel > 0; i--) {
                        //Assign room upgrade
                        for (Room room : vacantRoomList) {
                            //Check if there is a vacant room upgrade available
                            if (room.getRoomType().getRanking() == i) {
                                room.setIsVacant(Boolean.FALSE);
                                reservation.setExceptionType(ExceptionTypeEnum.TYPE1);
                                reservation.setFinalRoomType(room.getRoomType());
                                reservation.setRoom(room);
                                roomController.mergeRoom(room);
                                reservationController.updateReservation(reservation);
                                day2ReservationUpgrade.remove(reservation);
                                vacantRoomList.remove(room);
                            }
                        }
                    }
                }
            }
        }
        //After completing all upgrades, assign remainder of reservation to type 2 error
        if (day2ReservationUpgrade.size() > 0) {
            for (Reservation reservation : day2ReservationUpgrade) {
                reservation.setExceptionType(ExceptionTypeEnum.TYPE2);
                reservationController.updateReservation(reservation);
            }
        }
        //Check for early check in
        for (Reservation reservation : day1ReservationList) {
            if (!day2ReservationList.contains(reservation)) {
                reservation.setCheckInEarly(Boolean.TRUE);
                reservationController.updateReservation(reservation);
            }
        }
        //Check for late check out
        for (Reservation reservation : day2ReservationList) {
            if (!day1ReservationList.contains(reservation)) {
                reservation.setCheckOutLate(Boolean.TRUE);
                reservationController.updateReservation(reservation);
            }
        }
    }

    public void deleteAllRoomRates() {
        //Delete all room rates with no room types(assumed that room type was deleted)
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType=null");
        List<RoomRate> roomRates = query.getResultList();
        for (RoomRate roomRate : roomRates) {
            roomRateController.deleteRoomRate(roomRate.getRoomRateId());
        }

        Date date = new Date();
        //allow isUsed for all valid rate periods
        query = em.createQuery("SELECT rr FROM RoomRate rr WHERE :date BETWEEN rr.startDate AND rr.endDate");
        query.setParameter("date", date);
        roomRates = query.getResultList();
        for (RoomRate roomRate : roomRates) {
            roomRate.setIsValid(Boolean.TRUE);
            roomRateController.mergeRoomRate(roomRate);
        }

        //disallow isUsed for all invalid rate periods
        query = em.createQuery("SELECT rr FROM RoomRate rr WHERE :date NOT BETWEEN rr.startDate AND rr.endDate");
        query.setParameter("date", date);
        roomRates = query.getResultList();
        for (RoomRate roomRate : roomRates) {
            if (roomRate.getRateType() == PROMO || roomRate.getRateType() == PEAK) {
                roomRate.setIsValid(Boolean.FALSE);
                roomRateController.mergeRoomRate(roomRate);
            }
        }

        //Check for any room rates to be deleted
        query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.isUsed=false AND rr.isEnabled=false");
        roomRates = query.getResultList();
        for (RoomRate roomRate : roomRates) {
            roomRateController.deleteRoomRate(roomRate.getRoomRateId());
        }
    }

    public void deleteAllRooms() {
        //Check for any room rates to be deleted
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.isVacant=true AND rr.isEnabled=false");
        List<Room> rooms = query.getResultList();
        for (Room room : rooms) {
            roomController.deleteRoom(room.getRoomId());
        }
    }

    public void deleteAllRoomTypes() {
        Query query = em.createQuery("SELECT rt FROM RoomType rt WHERE rt.isEnabled=false");
        List<RoomType> roomTypes = query.getResultList();
        for (RoomType roomType : roomTypes) {
            if (roomType.getRooms().isEmpty()) {
                roomTypeController.deleteRoomType(roomType);
            }
        }
    }
}
