package com.example.APA.Repository;

import com.example.APA.Model.Movie;
import com.example.APA.Model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovie(Movie movie);
    Optional<Show> findByMovie_TitleAndDateAndTime(String title, LocalDate date, LocalTime time);
    List<Show> findByMovie_TitleAndDate(String title, LocalDate date);
}