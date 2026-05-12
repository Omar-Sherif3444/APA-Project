package com.example.APA.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.APA.Service.BookingService;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {
    private final BookingService bookingService;
    public PageController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
    @GetMapping("/About_Us")
    public String aboutUs() {
        return "About_Us";
    }
    @GetMapping("/FAQ")
    public String faq() {
        return "FAQ";
    }
    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session,
                             Model model) {

        if (session.getAttribute("userId") == null) {
            return "redirect:/Log_In";
        }
        Long userId = (Long) session.getAttribute("userId");

        model.addAttribute(
                "bookings",
                bookingService.getBookingsByUserId(userId)
        );

        return "my_bookings";
    }
    @GetMapping("/Terms&Conditions")
    public String terms() {
        return "Terms&Conditions";
    }
}
