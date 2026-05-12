package com.example.APA.Repository;

import com.example.APA.Model.Screen;
import com.example.APA.Model.Seat;
import com.example.APA.Model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByScreen(Screen screen);
    List<Seat> findByIdInAndStatus(List<Long> ids, SeatStatus status);

    Optional<Seat> findByRowAndSeatNumberAndScreen(Character row, int seatNumber, Screen screen);

    List<Seat> findByScreenAndStatus(Screen screen, SeatStatus status);
}