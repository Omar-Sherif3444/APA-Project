package com.example.APA.DTO;

public class SeatDTO {
    private Long id;
    private int seatNumber;
    private Character row;  // ✅ Changed from char to Character
    private String type;
    private String status;

    public SeatDTO() {}

    public SeatDTO(Long id, int seatNumber, Character row, String type, String status) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.row = row;
        this.type = type;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }

    public Character getRow() { return row; }
    public void setRow(Character row) { this.row = row; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
