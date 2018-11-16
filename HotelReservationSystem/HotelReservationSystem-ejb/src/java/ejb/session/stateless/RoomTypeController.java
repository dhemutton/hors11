package ejb.session.stateless;

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
        Query query  = em.createQuery("SELECT rt FROM RoomType rt ORDER BY rt.ranking");
        return query.getResultList();
    }
    
    @Override
    public List<RoomType> retrieveAllEnabledRoomType() {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.isEnabled=true");
        return query.getResultList();
    }
    
    @Override
    public List<RoomType> retrieveAllEnabledAndIsUsedRoomType() {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.isEnabled = true AND r.isUsed = true");
        return query.getResultList();
    }
    
      @Override
    public List<RoomType> retrieveAllEnabledAndIsUsedRoomTypesForWalkIn() {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.isEnabled = true AND r.isUsed = true AND r.roomRates.rateType = PUBLISHED");
        return query.getResultList();
    }
    
    @Override
    public List<RoomType> retrieveAllUsedRoomType() {
        Query query = em.createQuery("SELECT r FROM RoomType r WHERE r.isUsed = true");
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
        RoomType rt = em.find(RoomType.class, roomType.getRoomTypeId());
        int ranking = rt.getRanking();
        em.remove(rt);
        deleteRanking(ranking);
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
    
    @Override
    public void createRankings(int option) {
        List<RoomType> roomTypes = retrieveAllRoomtype();
        int temp = option-1;
        int temp1 = roomTypes.size()-1;
        for(int i=temp1; i>=temp; i--) {
            int change = roomTypes.get(i).getRanking();
            change++;
            roomTypes.get(i).setRanking(change);
            em.merge(roomTypes.get(i));
            em.flush();
        }
    }
    
    @Override
    public void updateRankings(int before, int after) {
        List<RoomType> roomTypes = retrieveAllRoomtype();
        if(before>after) {
            roomTypes.get(before-1).setRanking(0);
            em.merge(roomTypes.get(before-1));
            em.flush();
            for(int i=before-2; i>=(after-1); i--) {
                int temp = roomTypes.get(i).getRanking();
                temp++;
                roomTypes.get(i).setRanking(temp);
                em.merge(roomTypes.get(i));
                em.flush();
            }
            roomTypes.get(before-1).setRanking(after);
            em.merge(roomTypes.get(before-1));
            em.flush();
        }
        else if(after>before) {
            roomTypes.get(before-1).setRanking(0);
            em.merge(roomTypes.get(before-1));
            em.flush();
            for(int i=before; i<after; i++) {
                int temp = roomTypes.get(i).getRanking();
                temp--;
                roomTypes.get(i).setRanking(temp);
                em.merge(roomTypes.get(i));
                em.flush();
            }
            roomTypes.get(before-1).setRanking(after);
            em.merge(roomTypes.get(before-1));
            em.flush();
        }
    }
    
    @Override
    public void deleteRanking(int option) {
        List<RoomType> roomTypes = retrieveAllRoomtype();
        int temp = option-1;
        int temp1 = roomTypes.size()-1;
        for(int i=temp; i<roomTypes.size(); i++) {
            int change = roomTypes.get(i).getRanking();
            change--;
            roomTypes.get(i).setRanking(change);
            em.merge(roomTypes.get(i));
            em.flush();
        }
    }
}
