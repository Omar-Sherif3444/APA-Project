package com.example.APA.Service;

import com.example.APA.DTO.BookingDTO;
import com.example.APA.DTO.SeatDTO;
import com.example.APA.DTO.ShowDTO;
import com.example.APA.DTO.UserDTO;
import com.example.APA.Model.*;
import com.example.APA.Repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,
                        UserRepository userRepository,
                        BookingRepository bookingRepository,
                        MovieRepository movieRepository,
                        PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.movieRepository = movieRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========== ADMIN AUTH ==========

    public boolean login(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        return admin.isPresent() && passwordEncoder.matches(password, admin.get().getPassword());
    }

    // ========== USER MANAGEMENT ==========

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // ========== BOOKING MANAGEMENT ==========

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toBookingDTO)
                .collect(Collectors.toList());
    }

    // ========== MOVIE MANAGEMENT ==========

    @Transactional
    public void deleteMovie(Long movieId) {
        movieRepository.deleteById(movieId);
    }

    // ========== DTO CONVERTERS ==========

    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate()
        );
    }

    private BookingDTO toBookingDTO(Booking booking) {
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