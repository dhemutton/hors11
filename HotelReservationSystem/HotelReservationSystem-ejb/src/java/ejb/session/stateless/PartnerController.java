/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Partner;
import exceptions.PartnerExistException;
import exceptions.PartnerNotFoundException;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author matthealoo
 */
@Stateless
@Local(PartnerControllerLocal.class)
@Remote(PartnerControllerRemote.class)
public class PartnerController implements PartnerControllerRemote, PartnerControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;


    
    @Override
    public Partner createNewPartner(Partner partner) throws PartnerExistException {
        try {
            em.persist(partner);
            em.flush();

            return partner;
        } catch (PersistenceException ex) {
             
                throw new PartnerExistException("Partner with same username already exists");
            }
        }  


    @Override
    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException {
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try
        {
            return (Partner)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new PartnerNotFoundException("Partner " + username + " does not exist!");
        }       
    }
    

    @Override
    public Partner retrievePartnerById(Long partnerId) throws PartnerNotFoundException {
        Partner partner = em.find(Partner.class, partnerId);
        
        if (partner != null) {
            return partner;
        } else {
            throw new PartnerNotFoundException("Partner ID " + partnerId + " does not exist");
        }    
    }
    

    @Override
    public List<Partner> retrieveAllPartner() {
    Query query = em.createQuery("SELECT p FROM Partner p ORDER BY p.partnerId ASC");
        
        return query.getResultList();      
    }

   
}
