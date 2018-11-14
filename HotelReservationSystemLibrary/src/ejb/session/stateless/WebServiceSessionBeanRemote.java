/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import exceptions.InvalidLoginCredentials;
import exceptions.PartnerNotFoundException;
import javax.ejb.Remote;


/**
 *
 * @author matthealoo
 */
@Remote
public interface WebServiceSessionBeanRemote {
    
       public Partner loginForPartnerWS(java.lang.String arg0, java.lang.String arg1) throws PartnerNotFoundException, InvalidLoginCredentials;

    
}
