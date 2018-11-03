/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enums.BookingStatusEnum;
import enums.BookingTypeEnum;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author matthealoo
 */
@Entity
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal cost;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingTypeEnum bookingType;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatusEnum bookingStatus;
    @Column(nullable = false)
    private Date startDate;
    @Column(nullable = false)
    private Date endDate;

    @ManyToOne(optional = true)
    private Guest guest;
    
    @ManyToOne(optional = true)
    private Partner partner;
    
    @OneToMany(mappedBy = "booking")
    private List<Reservation> reservation;
    
    

    public Booking() {
         this.cost = new BigDecimal("0.0000");
        reservation = new ArrayList<>();
    
    }

    public Booking(BookingTypeEnum bookingType, BookingStatusEnum bookingStatus, Date startDate, Date endDate) {
        this.cost = new BigDecimal("0.0000");
        reservation = new ArrayList<>();
        this.bookingType = bookingType;
        this.bookingStatus = bookingStatus;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BookingTypeEnum getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingTypeEnum bookingType) {
        this.bookingType = bookingType;
    }

    public BookingStatusEnum getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatusEnum bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public List<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(List<Reservation> reservation) {
        this.reservation = reservation;
    }
    
  
    
    public Long getId() {
        return bookingId;
    }

    public void setId(Long id) {
        this.bookingId = bookingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bookingId != null ? bookingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.bookingId == null && other.bookingId != null) || (this.bookingId != null && !this.bookingId.equals(other.bookingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Booking[ id=" + bookingId + " ]";
    }
    
}
