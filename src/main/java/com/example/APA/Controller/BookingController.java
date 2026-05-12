package com.example.APA.Controller;

import com.example.APA.DTO.BookingDTO;
import com.example.APA.DTO.BookingRequest;
import com.example.APA.DTO.ShowDTO;
import com.example.APA.Model.SeatStatus;
import com.example.APA.Model.Show;
import com.example.APA.Repository.SeatRepository;
import com.example.APA.Repository.ShowRepository;
import com.example.APA.Service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class BookingController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-uuuu");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    private final BookingService bookingService;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    public BookingController(BookingService bookingService,
                             ShowRepository showRepository,
                             SeatRepository seatRepository) {
        this.bookingService = bookingService;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
    }

    @GetMapping("/Seats")
    public String seatsPage(@RequestParam(required = false) Long showId,
                            @RequestParam(required = false) String movieTitle,
                            @RequestParam(required = false) String showDate,
                            @RequestParam(required = false) String showTime,
                            HttpSession session, Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/Log_In";
        }

        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("bookings", bookingService.getBookingsByUserId(userId));
        model.addAttribute("bookingRequest", new BookingRequest());

        Show show = resolveShow(showId, movieTitle, showDate, showTime);

        if (show != null) {
            model.addAttribute("show", toShowDTO(show));
            model.addAttribute("occupiedSeatIds", getOccupiedSeatNumbers(show));
        } else {

            if (movieTitle != null && !movieTitle.isBlank()) {
                model.addAttribute("show", new ShowDTO(null, null, movieTitle, null, showDate, showTime));
            }
            model.addAttribute("occupiedSeatIds", List.of());
        }

        return "Seats";
    }

    @PostMapping("/Seats/checkout")
    public String checkout(@ModelAttribute BookingRequest bookingRequest,
                           HttpSession session,
                           Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/Log_In";
        }

        bookingRequest.setSeatIds(filterBlankIds(bookingRequest.getSeatIds()));
        model.addAttribute("bookingRequest", bookingRequest);
        return "Checkout";
    }

    @PostMapping("/confirm-payment")
    public String confirmPayment(@ModelAttribute BookingRequest bookingRequest,
                                 HttpSession session,
                                 Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/Log_In";
        }

        try {
            bookingRequest.setSeatIds(filterBlankIds(bookingRequest.getSeatIds()));
            Long userId = (Long) session.getAttribute("userId");
            BookingDTO booking = bookingService.createBooking(userId, bookingRequest);
            return "redirect:/Ticket/" + booking.getId();
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("bookingRequest", bookingRequest);
            return "Checkout";
        }
    }

    @GetMapping("/Ticket/{bookingId}")
    public String ticketPage(@PathVariable Long bookingId,
                             HttpSession session,
                             Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/Log_In";
        }

        BookingDTO booking = bookingService.getBookingById(bookingId);
        if (booking != null) {
            model.addAttribute("booking", booking);
        }
        return "Ticket";
    }

    private Show resolveShow(Long showId, String movieTitle, String showDate, String showTime) {
        if (showId != null) {
            return showRepository.findById(showId).orElse(null);
        }
        if (movieTitle != null && !movieTitle.isBlank()
                && showDate != null && !showDate.isBlank()
                && showTime != null && !showTime.isBlank()) {
            return findShowByDetails(movieTitle, showDate, showTime).orElse(null);
        }
        return null;
    }

    private List<Integer> getOccupiedSeatNumbers(Show show) {
        if (show.getScreen() == null) {
            return List.of();
        }
        return seatRepository
                .findByScreenAndStatus(show.getScreen(), SeatStatus.OCCUPIED)
                .stream()
                .map(seat -> (seat.getRow() - 'A') * 6 + seat.getSeatNumber())
                .collect(Collectors.toList());
    }

    private Optional<Show> findShowByDetails(String movieTitle, String showDate, String showTime) {
        try {
            LocalDate date = LocalDate.parse(showDate, DATE_FMT);
            LocalTime time = LocalTime.parse(showTime, TIME_FMT);
            return showRepository.findByMovie_TitleAndDateAndTime(movieTitle, date, time);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    private List<String> filterBlankIds(List<String> ids) {
        if (ids == null) return List.of();
        return ids.stream()
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toList());
    }

    private ShowDTO toShowDTO(Show show) {
        return new ShowDTO(
                show.getId(),
                show.getMovie() != null ? show.getMovie().getId() : null,
                show.getMovie() != null ? show.getMovie().getTitle() : null,
                show.getScreen() != null ? show.getScreen().getId() : null,
                show.getDate() != null ? show.getDate().toString() : null,
                show.getTime() != null ? show.getTime().toString() : null
        );
    }
}