/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;


/**
 *
 * @author matthealoo
 */
public interface SelfInvokeDailyControllerLocal {
    public void dailyReservationRoomAssignment();

    
        public void deleteAllRoomRates();

    
        public void deleteAllRooms();

    
        public void deleteAllRoomTypes();
    
}
