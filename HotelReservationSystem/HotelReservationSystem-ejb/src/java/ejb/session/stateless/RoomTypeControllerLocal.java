/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import exceptions.RoomTypeNotFoundException;
import java.util.List;

/**
 *
 * @author sleep
 */
public interface RoomTypeControllerLocal {

    public void persist(Object object);

    public Long createRoomType(RoomType roomType);

    public List<RoomType> retrieveAllRoomTypes();

    public RoomType retrieveRoomTypeById(Long id) throws RoomTypeNotFoundException;

    public void updateRoomType(RoomType roomType);

    public void deleteRoomType(RoomType roomType);
    
}
