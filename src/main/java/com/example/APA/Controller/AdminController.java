package com.example.APA.Controller;
import com.example.APA.Service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @GetMapping("/login")
    public String loginPage() {
        return "Admin_Login";
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (adminService.login(username, password)) {
            session.setAttribute("adminUsername", username);
            return "redirect:/admin/dashboard";
        }
        redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
        return "redirect:/admin/login";
    }
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("adminUsername") == null) {
            return "redirect:/admin/login";
        }
        return "Admin_Dashboard";
    }
    @GetMapping("/users")
    public String viewUsers(HttpSession session, Model model) {
        if (session.getAttribute("adminUsername") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("users", adminService.getAllUsers());
        return "Admin_Users";
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminUsername") == null) {
            return "redirect:/admin/login";
        }
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }
    @GetMapping("/bookings")
    public String viewBookings(HttpSession session, Model model) {
        if (session.getAttribute("adminUsername") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("bookings", adminService.getAllBookings());
        return "Admin_Bookings";
    }
    @GetMapping("/movies/delete/{id}")
    public String deleteMovie(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("adminUsername") == null) {
            return "redirect:/admin/login";
        }
        adminService.deleteMovie(id);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("adminUsername");
        return "redirect:/admin/login";
    }
}