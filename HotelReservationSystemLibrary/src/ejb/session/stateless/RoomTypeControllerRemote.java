package ejb.session.stateless;

import entity.RoomType;
import exceptions.RoomTypeExistException;
import exceptions.RoomTypeNotFoundException;
import java.util.List;

public interface RoomTypeControllerRemote {
    
    public void persist(Object object);

    public RoomType createRoomType(RoomType roomType) throws RoomTypeExistException;

    public List<RoomType> retrieveAllRoomtype();
    
    public List<RoomType> retrieveAllEnabledRoomType();

    public RoomType retrieveRoomTypeByName(String roomTypeName) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType);

    public void deleteRoomType(RoomType roomType);
    
    public RoomType retrieveRoomTypeById(Long RoomTypeId) throws RoomTypeNotFoundException;
    
   public RoomType updateRoomTypeAddRoomRate(RoomType roomType, List<Long> ids);
   
    public RoomType updateRoomTypeRemoveRoomRate(RoomType roomType, List<Long> ids);

    public void updateRankings(int option);
    
    public void deleteRanking(int option);
    
}
