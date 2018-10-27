/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import javax.ejb.Remote;

/**
 *
 * @author matthealoo
 */
@Remote
public interface ReservationControllerRemote {
        public Reservation createNewReservation(Reservation reservation) ;

}
