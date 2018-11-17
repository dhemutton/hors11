/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import exceptions.RoomExistException;
import exceptions.RoomNotFoundException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author sleep
 */
@Stateless
@Local(RoomControllerLocal.class)
@Remote(RoomControllerRemote.class)
public class RoomController implements RoomControllerRemote, RoomControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public Room createRoom(Room room, Long roomTypeId) throws RoomExistException, RoomTypeNotFoundException {
        try {
            RoomType roomType = roomTypeControllerLocal.retrieveRoomTypeById(roomTypeId);
            em.persist(room);
            em.flush();

            room.setRoomType(roomType);
            roomType.setIsUsed(Boolean.TRUE);
            roomType.getRooms().add(room);
            em.merge(roomType);
            em.flush();
            em.merge(room);
            em.flush();
            
            return room;
        } catch (PersistenceException ex) {
            throw new RoomExistException("Room already exists");
            
        } catch (RoomTypeNotFoundException ex) {
            throw new RoomTypeNotFoundException("Unable to create new room as the room type record does not exist");
        }
    }

    @Override
    public List<Room> retrieveAllEnabledRooms() {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.isEnabled = true ");
        return query.getResultList();
    }

      @Override
    public List<Room> retrieveAllEnabledAndVacantRooms() {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.isEnabled = true AND r.isVacant = true ");
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r ORDER BY r.roomNumber ASC ");
        return query.getResultList();
    }

    
    @Override
    public List<Room> retrieveAllEnabledRoomsFromRoomType(RoomType roomType) {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomType=:roomType AND r.isEnabled = true");
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }

    @Override
    public List<Room> retrieveAllVacantRooms() {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.isVacant=true");
        return query.getResultList();
    }

    @Override
    public List<Room> retrieveAllOccupiedRooms() {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.isVacant=false");
        return query.getResultList();
    }

    @Override
    public Room retrieveRoomById(Long RoomId) throws RoomNotFoundException {
        Room room = em.find(Room.class, RoomId);

        if (room != null) {
            room.getReservations().size();
            return room;
        } else {
            throw new RoomNotFoundException("Room ID " + RoomId + " does not exist");
        }
    }

    @Override
    public Room retrieveRoomByRoomNum(String roomNum) throws RoomNotFoundException {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNumber = :arg");
        query.setParameter("arg", roomNum);

        try {
            return (Room) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomNotFoundException("Room Number " + roomNum + " does not exist!");
        }
    }

    @Override
    public void mergeRoom(Room room) {
        
        em.merge(room);
        em.flush();
    }
    
    @Override
    public void updateRoom(Room room, Long oldroomTypeId, Long newRoomTypeId) throws RoomTypeNotFoundException {
        
        try {
            RoomType newroomType = roomTypeControllerLocal.retrieveRoomTypeById(newRoomTypeId);
            newroomType.getRooms().size();

            if (!oldroomTypeId.equals(newRoomTypeId)) { //change in roomtype
                    RoomType oldroomType = roomTypeControllerLocal.retrieveRoomTypeById(oldroomTypeId);
                    oldroomType.getRooms().size();

                    oldroomType.getRooms().remove(room);
                    room.setRoomType(newroomType); //set new room type to room
                    newroomType.getRooms().add(room); //add new room to room type
                    em.merge(oldroomType);
                    em.flush();
                    em.merge(newroomType);
                    em.flush();
            }
            
            em.merge(room);
            em.flush();
            
        } catch (RoomTypeNotFoundException ex) {
            throw new RoomTypeNotFoundException("Room Type not found! ");
        }
        
    }

    @Override
    public void deleteRoom(Long roomId) {
        Room room = em.find(Room.class, roomId);
        RoomType roomType = em.find(RoomType.class, room.getRoomType().getRoomTypeId());
        roomType.getRooms().remove(room);
        if(roomType.getRooms().isEmpty()) {
            roomType.setIsUsed(Boolean.FALSE);
        }
        em.merge(roomType);
        em.flush();
        room.setIsEnabled(Boolean.FALSE);
        room.getRoomType().getRooms().size();
        room.getRoomType().getRooms().remove(room);
        
        em.remove(room);
    }
}
