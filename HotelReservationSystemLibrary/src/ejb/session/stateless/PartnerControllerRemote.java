/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import exceptions.PartnerExistException;
import exceptions.PartnerNotFoundException;
import java.util.List;

/**
 *
 * @author matthealoo
 */
public interface PartnerControllerRemote {
    public Partner createNewPartner(Partner partner) throws PartnerExistException;

    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException;

    public Partner retrievePartnerById(Long partnerId) throws PartnerNotFoundException;

    public List<Partner> retrieveAllPartner();
}
