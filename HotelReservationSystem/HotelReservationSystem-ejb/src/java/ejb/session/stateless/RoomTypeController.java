/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import exceptions.RoomTypeExistException;
import entity.RoomType;
import exceptions.RoomTypeNotFoundException;
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
@Local(RoomTypeControllerLocal.class)
@Remote(RoomTypeControllerRemote.class)
public class RoomTypeController implements RoomTypeControllerRemote, RoomTypeControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public RoomType createRoomType(RoomType roomType) throws RoomTypeExistException {
        try {
            em.persist(roomType);
            em.flush();

            return roomType;
        } catch (PersistenceException ex) {
             
                throw new RoomTypeExistException("Room type already exists");
            }
    }
    
    @Override
    public List<RoomType> retrieveAllRoomtype() {
        Query query  = em.createQuery("SELECT rt FROM RoomType rt");
        return query.getResultList();
    }
    
    @Override
    public List<RoomType> retrieveAllEnabledRoomType() {
        Query query = em.createQuery("SELECT r FROM RoomType WHERE r.isEnabled=true");
        return query.getResultList();
    }
    
    @Override
    public RoomType retrieveRoomTypeById(Long RoomTypeId) throws RoomTypeNotFoundException {
        RoomType roomType = em.find(RoomType.class, RoomTypeId);
        
        if (roomType != null) {
            roomType.getRoomRates().size();
            roomType.getRooms().size();
                    return roomType;
        } else {
            throw new RoomTypeNotFoundException("Room Type ID " + RoomTypeId + " does not exist");
        }
    }
    
    @Override
    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.name = :arg");
        query.setParameter("arg", roomTypeName);

        try {
            RoomType roomType = (RoomType) query.getSingleResult();
            roomType.getRooms().size();
            roomType.getRoomRates().size();
            return roomType;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeNotFoundException("Room Type Name " + roomTypeName + " does not exist!");
        }
    }
    
    @Override
    public void updateRoomType(RoomType roomType) {
        em.merge(roomType);
    }
    
    @Override
    public void deleteRoomType(RoomType roomType) {
        em.merge(roomType);

        em.remove(roomType);
    }
    
    @Override
    public RoomType updateRoomTypeAddRoomRate(RoomType roomType, List<Long> ids) {
                em.merge(roomType);
                roomType.getRoomRates().size();
        for (Long id: ids) {
            RoomRate roomRate = em.find(RoomRate.class, id);
            roomType.getRoomRates().add(roomRate);
        }
        em.merge(roomType);
        em.flush();
        return roomType;
    }
    
    @Override
    public RoomType updateRoomTypeRemoveRoomRate(RoomType roomType, List<Long> ids) {
                em.merge(roomType);
                roomType.getRoomRates().size();
        for (Long id: ids) {
            RoomRate roomRate = em.find(RoomRate.class, id);
            roomType.getRoomRates().remove(roomRate);
        }
        em.merge(roomType);
        em.flush();
        return roomType;
    }
}
