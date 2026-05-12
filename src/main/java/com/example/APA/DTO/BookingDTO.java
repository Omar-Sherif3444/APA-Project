package com.example.APA.DTO;

import java.util.List;

public class BookingDTO {

    private Long id;
    private Long userId;
    private String userFirstName;
    private ShowDTO show;
    private List<SeatDTO> seats;
    private double totalPrice;
    private String status;

    public BookingDTO() {}

    public BookingDTO(Long id, Long userId, String userFirstName,
                      ShowDTO show, List<SeatDTO> seats,
                      double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.show = show;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public ShowDTO getShow() { return show; }
    public void setShow(ShowDTO show) { this.show = show; }

    public List<SeatDTO> getSeats() { return seats; }
    public void setSeats(List<SeatDTO> seats) { this.seats = seats; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}