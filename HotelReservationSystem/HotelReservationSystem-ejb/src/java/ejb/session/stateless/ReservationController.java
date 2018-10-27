/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import javax.ejb.Stateless;

/**
 *
 * @author matthealoo
 */
@Stateless
public class ReservationController implements ReservationControllerRemote, ReservationControllerLocal {

    @Override
    public Reservation createNewReservation(Reservation reservation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
