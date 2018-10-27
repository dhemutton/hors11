/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import exceptions.RoomExistException;
import java.util.List;

/**
 *
 * @author sleep
 */
public interface RoomControllerRemote {
    
    public List<Room> retrieveAllRooms();

    public List<Room> retrieveAllRoomsFromRoomType(RoomType roomType);

    public List<Room> retrieveAllVacantRooms();

    public List<Room> retrieveAllOccupiedRooms();

    public void updateRoom(Room room);

    public void deleteRoom(Room room);

    public void persist(Object object);

    public Room createRoom(Room room) throws RoomExistException;
}
