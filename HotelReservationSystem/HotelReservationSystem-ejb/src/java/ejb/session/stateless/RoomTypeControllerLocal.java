/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import exceptions.RoomTypeeExistException;
import java.util.List;

/**
 *
 * @author sleep
 */
public interface RoomTypeControllerLocal {

    public void persist(Object object);

    public RoomType createRoomType(RoomType roomType) throws RoomTypeeExistException;

    public List<RoomType> retrieveAllRoomtype();

    public void updateRoomType(RoomType roomType);

    public void deleteRoomType(RoomType roomType);
    
}
