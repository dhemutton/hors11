/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import enums.ExceptionTypeEnum;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author matthealoo
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    private Boolean checkInEarly;
    private Boolean checkOutLate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExceptionTypeEnum exceptionType;

    
    @ManyToOne
    private Room room;
    
    @ManyToOne(optional = false)
    private RoomType roomType;
    
    @ManyToOne(optional = false)
    private Booking booking;

    public Reservation() {
        
    }

    public Reservation(Long reservationId, ExceptionTypeEnum exceptionType, RoomType roomType, Booking booking) {
        this.reservationId = reservationId;
        this.exceptionType = exceptionType;
        this.roomType = roomType;
        this.booking = booking;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Boolean getCheckInEarly() {
        return checkInEarly;
    }

    public void setCheckInEarly(Boolean checkInEarly) {
        this.checkInEarly = checkInEarly;
    }

    public Boolean getCheckOutLate() {
        return checkOutLate;
    }

    public void setCheckOutLate(Boolean checkOutLate) {
        this.checkOutLate = checkOutLate;
    }

    public ExceptionTypeEnum getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ExceptionTypeEnum exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    

    
    
    
    
    public Long getId() {
        return reservationId;
    }

    public void setId(Long id) {
        this.reservationId = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
    
}
