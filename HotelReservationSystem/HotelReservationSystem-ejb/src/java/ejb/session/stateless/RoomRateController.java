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
public class RoomRateController implements RoomRateControllerRemote, RoomRateControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void persist(Object object) {
        em.persist(object);
    }

    public RoomRate createRoom(RoomRate roomRate) throws RoomRateExistException {
        try {
            em.persist(roomRate);
            em.flush();

            return roomRate;
        } catch (PersistenceException ex) {
             
                throw new RoomRateExistException("Room already exists");
            }
    }
    
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr");
        return query.getResultList();
    }
    
    public List<RoomRate> retrieveAllRoomRatesByRoomType(RoomType roomType) {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.roomType=:roomType");
        query.setParameter("roomType", roomType);
        return query.getResultList();
    }
    
    public List<RoomRate> retrieveAllEnabledRoomRates() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.isEnabled=:true");
        return query.getResultList();
    }
    
    public List<RoomRate> retrieveAllRoomRatesForPartners() {
        Query query = em.createQuery("SELECT rr FROM RoomRate rr WHERE rr.forPartner=:true");
        return query.getResultList();
    }
}
