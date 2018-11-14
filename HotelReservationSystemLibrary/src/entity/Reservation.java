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
import javax.persistence.ManyToOne;

@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    private Boolean checkInEarly = false;
    private Boolean checkOutLate = false;
    private Boolean isCheckedIn = false;
    private Boolean isCheckedOut = false;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExceptionTypeEnum exceptionType;
    
    @ManyToOne(optional = true)
    private RoomType finalRoomType;
    
    @ManyToOne(optional = true)
    private Room room;
    
    @ManyToOne(optional = false)
    private RoomType initialRoomType;
    
    @ManyToOne(optional = false)
    private Booking booking;

    public Reservation() {
        
    }

    public Reservation(RoomType initialRoomType, Booking booking, ExceptionTypeEnum exceptionType) {
        this.initialRoomType = initialRoomType;
        this.booking = booking;
        this.exceptionType = exceptionType;
    }
    
    public Reservation(RoomType initialRoomType, RoomType finalRoomType, Room room, Booking booking, ExceptionTypeEnum exceptionType) {
        this.initialRoomType = initialRoomType;
        this.finalRoomType = finalRoomType;
        this.room = room;
        this.booking = booking;
        this.exceptionType = exceptionType;
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

    public RoomType getInitialRoomType() {
        return initialRoomType;
    }

    public void setInitialRoomType(RoomType roomType) {
        this.initialRoomType = roomType;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public RoomType getFinalRoomType() {
        return finalRoomType;
    }

    public void setFinalRoomType(RoomType finalRoomType) {
        this.finalRoomType = finalRoomType;
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

    /**
     * @return the isCheckedIn
     */
    public Boolean getIsCheckedIn() {
        return isCheckedIn;
    }

    /**
     * @param isCheckedIn the isCheckedIn to set
     */
    public void setIsCheckedIn(Boolean isCheckedIn) {
        this.isCheckedIn = isCheckedIn;
    }

    /**
     * @return the isCheckedOut
     */
    public Boolean getIsCheckedOut() {
        return isCheckedOut;
    }

    /**
     * @param isCheckedOut the isCheckedOut to set
     */
    public void setIsCheckedOut(Boolean isCheckedOut) {
        this.isCheckedOut = isCheckedOut;
    }
    
}
