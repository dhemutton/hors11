/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import exceptions.RoomRateExistException;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author sleep
 */
@Stateless
@Local(RoomRateControllerLocal.class)
@Remote(RoomRateControllerRemote.class)
public class RoomRateController implements RoomRateControllerRemote, RoomRateControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public RoomRate createRoom(RoomRate roomRate) throws RoomRateExistException {
        try {
            em.persist(roomRate);
            em.flush();

            return roomRate;
        } catch (PersistenceException ex) {
             
                throw new RoomRateExistException("Room already exists");
            }
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        return query.getResultList();
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRatesByRoomType(RoomType roomType) {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType=:roomType");
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }
    
    @Override
    public List<RoomRate> retrieveAllEnabledRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.isEnabled=:true");
        return query.getResultList();
    }
    
    @Override
    public List<RoomRate> retrieveAllDisabledRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.isEnabled=false");
        return query.getResultList();
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRatesForPartners() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.forPartner=true");
        return query.getResultList();
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRatesForNonPartners() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.forPartner=false");
        return query.getResultList();
    }
    
    @Override
    public void updateRoomRate(RoomRate roomRate) {
        em.merge(roomRate);
    }
    
    @Override
    public void deleteRoomRate(RoomRate roomRate) {
        em.remove(roomRate);
    }
}
