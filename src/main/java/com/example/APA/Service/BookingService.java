package com.example.APA.Service;

import com.example.APA.DTO.BookingDTO;
import com.example.APA.DTO.BookingRequest;
import com.example.APA.DTO.SeatDTO;
import com.example.APA.DTO.ShowDTO;
import com.example.APA.Model.*;
import com.example.APA.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ShowRepository showRepository,
                          SeatRepository seatRepository,
                          MovieRepository movieRepository,
                          ScreenRepository screenRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.movieRepository = movieRepository;
        this.screenRepository = screenRepository;
    }
    @Transactional
    public BookingDTO createBooking(Long userId, BookingRequest request) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }
        User user = optionalUser.get();
        List<String> validSeatIds = request.getSeatIds().stream()
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());
        if (validSeatIds.isEmpty()) {
            throw new IllegalArgumentException("No seats selected.");
        }
        Show show = null;
        if (request.getShowId() != null) {
            show = showRepository.findById(request.getShowId()).orElse(null);
        }
        if (show == null && request.getMovieTitle() != null
                && request.getShowDate() != null && request.getShowTime() != null) {
            try {
                LocalDate date = LocalDate.parse(request.getShowDate(),
                        DateTimeFormatter.ofPattern("dd-MM-uuuu"));
                LocalTime time = LocalTime.parse(request.getShowTime(),
                        DateTimeFormatter.ofPattern("hh:mm a"));

                Optional<Show> foundShow = showRepository
                        .findByMovie_TitleAndDateAndTime(request.getMovieTitle(), date, time);
                if (foundShow.isPresent()) {
                    show = foundShow.get();
                }
            } catch (Exception e) {
            }
        }
        if (show == null && request.getMovieTitle() != null && request.getShowDate() != null) {
            try {
                LocalDate date = LocalDate.parse(request.getShowDate(),
                        DateTimeFormatter.ofPattern("dd-MM-uuuu"));
                List<Show> shows = showRepository.findByMovie_TitleAndDate(
                        request.getMovieTitle(), date);
                if (!shows.isEmpty()) {
                    show = shows.get(0);
                }
            } catch (Exception e) {
            }
        }
        if (show == null) {
            Movie movie = movieRepository.findByTitle(request.getMovieTitle()).orElse(null);
            if (movie == null) {
                movie = new Movie();
                movie.setTitle(request.getMovieTitle());
                movie.setGenre("Unknown");
                movie.setDuration(120);
                movie.setLanguage("Arabic");
                movie = movieRepository.save(movie);
            }
            Screen screen = screenRepository.save(new Screen());
            show = new Show();
            show.setMovie(movie);
            show.setScreen(screen);
            try {
                show.setDate(LocalDate.parse(request.getShowDate(),
                        DateTimeFormatter.ofPattern("dd-MM-uuuu")));
                show.setTime(LocalTime.parse(request.getShowTime(),
                        DateTimeFormatter.ofPattern("hh:mm a")));
            } catch (Exception e) {
                show.setDate(LocalDate.now());
                show.setTime(LocalTime.of(19, 0));
            }
            show = showRepository.save(show);
        }
        if (show.getScreen() == null) {
            Screen screen = screenRepository.save(new Screen());
            show.setScreen(screen);
        }
        List<Seat> seats = new ArrayList<>();
        for (String seatIdStr : validSeatIds) {
            Long seatId;
            try {
                seatId = Long.parseLong(seatIdStr);
            } catch (NumberFormatException e) {
                continue;
            }

            Optional<Seat> found = seatRepository.findById(seatId);
            if (found.isPresent() && found.get().getStatus() == SeatStatus.AVAILABLE) {
                seats.add(found.get());
            }
        }
        if (seats.isEmpty()) {
            for (String seatIdStr : validSeatIds) {
                int seatNumber;
                try {
                    seatNumber = Integer.parseInt(seatIdStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                char row = (char) ('A' + (seatNumber - 1) / 6);
                int pos = ((seatNumber - 1) % 6) + 1;

                Optional<Seat> found = seatRepository.findByRowAndSeatNumberAndScreen(
                        row, pos, show.getScreen());

                if (found.isPresent() && found.get().getStatus() == SeatStatus.AVAILABLE) {
                    seats.add(found.get());
                } else if (found.isEmpty()) {
                    Seat newSeat = new Seat();
                    newSeat.setRow(row);
                    newSeat.setSeatNumber(pos);
                    newSeat.setType(SeatType.STANDARD);
                    newSeat.setStatus(SeatStatus.OCCUPIED);
                    newSeat.setScreen(show.getScreen());
                    seats.add(seatRepository.save(newSeat));
                }
            }
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException(
                    "Selected seats are not available. Please choose different seats.");
        }
        for (Seat seat : seats) {
            seat.setStatus(SeatStatus.OCCUPIED);
        }
        seatRepository.saveAll(seats);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setSeats(seats);
        booking.setTotalPrice(seats.size() * 150.0);
        booking.setStatus(BookingStatus.CONFIRMED);
        return toDTO(bookingRepository.save(booking));
    }
    @Transactional
    public void cancelBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            booking.setStatus(BookingStatus.CANCELLED);
            for (Seat seat : booking.getSeats()) {
                seat.setStatus(SeatStatus.AVAILABLE);
            }
            seatRepository.saveAll(booking.getSeats());
            bookingRepository.save(booking);
        }
    }
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return null;
        }
        return bookingRepository.findByUser(optionalUser.get())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public BookingDTO getBookingById(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            return toDTO(optionalBooking.get());
        }
        return null;
    }
    public Long getLatestBookingIdForUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return null;
        }
        return bookingRepository.findByUser(optionalUser.get())
                .stream()
                .map(Booking::getId)
                .max(Long::compareTo)
                .orElse(null);
    }
    private BookingDTO toDTO(Booking booking) {
        Show show = booking.getShow();
        ShowDTO showDTO = new ShowDTO(
                show.getId(),
                show.getMovie() != null ? show.getMovie().getId() : null,
                show.getMovie() != null ? show.getMovie().getTitle() : null,
                show.getScreen() != null ? show.getScreen().getId() : null,
                show.getDate() != null ? show.getDate().toString() : null,
                show.getTime() != null ? show.getTime().toString() : null
        );
        List<SeatDTO> seatDTOs = booking.getSeats().stream()
                .map(seat -> new SeatDTO(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getRow(), 
                        seat.getType() != null ? seat.getType().name() : null,
                        seat.getStatus() != null ? seat.getStatus().name() : null
                ))
                .collect(Collectors.toList());
        return new BookingDTO(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getFirstName(),
                showDTO,
                seatDTOs,
                booking.getTotalPrice(),
                booking.getStatus() != null ? booking.getStatus().name() : null
        );
    }
}