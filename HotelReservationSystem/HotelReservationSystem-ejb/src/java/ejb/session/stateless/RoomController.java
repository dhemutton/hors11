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
import java.util.List;
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

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public Room createRoom(Room room) throws RoomExistException {
        try {
            em.persist(room);
            em.flush();

            return room;
        } catch (PersistenceException ex) {
             
                throw new RoomExistException("Room already exists");
            }
    }
    
    @Override
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r FROM Room r");
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAllRoomsFromRoomType(RoomType roomType) {
        Query query = em.createQuery("SELECT r FROM Room WHERE r.roomType=:roomType");
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAllVacantRooms() {
        Query query = em.createQuery("SELECT r FROM Room WHERE r.vacancy=true");
        return query.getResultList();
    }
    
    @Override
    public List<Room> retrieveAllOccupiedRooms() {
        Query query = em.createQuery("SELECT r FROM Room WHERE r.vacancy=false");
        return query.getResultList();
    }
    
    @Override
    public Room retrieveRoomById(Long RoomId) throws RoomNotFoundException {
        Room room = em.find(Room.class, RoomId);
        
        if (room != null) {
            return room;
        } else {
            throw new RoomNotFoundException("Employee ID " + RoomId + " does not exist");
        }
    }
    
    @Override
    public Room retrieveRoomByRoomNum(String roomNum) throws RoomNotFoundException {
        Query query = em.createQuery("SELECT r FROM Room r WHERE r.roomNum = :arg");
        query.setParameter("arg", roomNum);
        
        try
        { 
            return (Room)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {    throw new RoomNotFoundException("Room Number " + roomNum + " does not exist!");
        }   
    }
    
    @Override
    public void updateRoom(Room room) {
        em.merge(room);
    }
    
    @Override
    public void deleteRoom(Room room) {
        em.remove(room);
    }
}
