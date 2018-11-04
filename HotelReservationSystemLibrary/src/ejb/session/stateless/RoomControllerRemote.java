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

/**
 *
 * @author sleep
 */
public interface RoomControllerRemote {
    
        public void mergeRoom(Room room);

    
    public List<Room> retrieveAllRooms();

    public List<Room> retrieveAllRoomsFromRoomType(RoomType roomType);

    public List<Room> retrieveAllVacantRooms();

    public List<Room> retrieveAllOccupiedRooms();
    
   public Room retrieveRoomByRoomNum(String roomNum) throws RoomNotFoundException;

    public void updateRoom(Long roomId, Long oldroomTypeId, Long newroomTypeId ) throws RoomTypeNotFoundException;

    public void deleteRoom(Long roomId);

    public void persist(Object object);

    public Room createRoom(Room room, Long roomTypeId) throws RoomExistException, RoomTypeNotFoundException;
    
    public Room retrieveRoomById(Long RoomId) throws RoomNotFoundException;
    
}
