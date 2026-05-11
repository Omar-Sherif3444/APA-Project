package com.example.APA.Model;

import jakarta.persistence.*;
import java.util.List;
@Entity
@Table(name = "Screens")
public class Screen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "screen")
    private List<Show> shows;
    @OneToMany(mappedBy = "screen")
    private List<Seat> seats;
    public Screen(){}

    public List<Show> getShows() {
        return shows;
    }
    public void setShows(List<Show> shows) {
        this.shows = shows;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<Seat> getSeats() {
        return seats;
    }
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }


}