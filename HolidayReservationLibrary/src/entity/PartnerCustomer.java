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
    private String email;
    @Column(length = 32, nullable = false)
    private String firstName;
    @Column(length = 32, nullable = false)
    private String lastName;
    @Column(length = 32, nullable = false)
    private String contactNumber;
    @Column(length = 32, nullable = false)
    private String password;
    private Boolean isLogin = false;
    private List<Long> bookingIds;

    public PartnerCustomer() {
        bookingIds = new ArrayList<>();
    }

    public PartnerCustomer(String email, String firstName, String lastName, String contactNumber, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNumber = contactNumber;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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

    public List<Long> getBookingIds() {
        return bookingIds;
    }

    public void setBookingIds(List<Long> bookingIds) {
        this.bookingIds = bookingIds;
    }
    

    public Long getPartnerCustomerId() {
        return partnerCustomerId;
    }

    public void setPartnerCustomerId(Long partnerCustomerId) {
        this.partnerCustomerId = partnerCustomerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (partnerCustomerId != null ? partnerCustomerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the partnerCustomerId fields are not set
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
