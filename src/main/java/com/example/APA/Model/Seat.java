package com.example.APA.Model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int seatNumber;
    @Column(name = "seat_row")
    private Character row;  
    @Enumerated(EnumType.STRING)
    private SeatType type;
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;
    @ManyToMany(mappedBy = "seats")
    private List<Booking> bookings;
    public Seat() {}
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }
    public Screen getScreen() { return screen; }
    public void setScreen(Screen screen) { this.screen = screen; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public Character getRow() { return row; } 
    public void setRow(Character row) { this.row = row; }  
    public SeatType getType() { return type; }
    public void setType(SeatType type) { this.type = type; }
    public SeatStatus getStatus() { return status; }
    public void setStatus(SeatStatus status) { this.status = status; }
}