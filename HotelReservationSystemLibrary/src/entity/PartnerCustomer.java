/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author matthealoo
 */
@Entity
public class PartnerCustomer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerCustomerId;
    @Column(length = 32, nullable = false, unique = true)
    private String username;
    @Column(length = 32, nullable = false)
    private String password;
    private Boolean isLogin = false;
    
    @ManyToOne 
    private Partner partner;

    public PartnerCustomer() {

    }

    public PartnerCustomer(Long partnerCustomerId, String username, String password, Partner partner) {
        this.partnerCustomerId = partnerCustomerId;
        this.username = username;
        this.password = password;
        this.partner = partner;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public Long getPartnerCustomerId() {
        return partnerCustomerId;
    }

    public void setPartnerCustomerId(Long partnerCustomerId) {
        this.partnerCustomerId = partnerCustomerId;
    }

    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(Boolean isLogin) {
        this.isLogin = isLogin;
    }

   

   
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerCustomerId != null ? partnerCustomerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PartnerCustomer)) {
            return false;
        }
        PartnerCustomer other = (PartnerCustomer) object;
        if ((this.partnerCustomerId == null && other.partnerCustomerId != null) || (this.partnerCustomerId != null && !this.partnerCustomerId.equals(other.partnerCustomerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PartnerCustomer[ id=" + partnerCustomerId + " ]";
    }
    
}
