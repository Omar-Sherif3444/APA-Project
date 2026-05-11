package com.example.APA.Controller;
import com.example.APA.DTO.LoginRequest;
import com.example.APA.DTO.SignUpRequest;
import com.example.APA.DTO.UserDTO;
import com.example.APA.Service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/sign_up")
    public String signUpPage(Model model) {
        model.addAttribute("signUpRequest", new SignUpRequest());
        return "sign_up";
    }
    @PostMapping("/sign_up")
    public String signUp(@Valid @ModelAttribute SignUpRequest signUpRequest, BindingResult bindingResult,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "sign_up";
        }
        try {
            userService.register(signUpRequest);
            return "redirect:/Log_In";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "sign_up";
        }
    }
    @GetMapping("/Log_In")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "Log_In";
    }
    @PostMapping("/Log_In")
    public String login(@Valid @ModelAttribute LoginRequest loginRequest,BindingResult bindingResult,HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "Log_In";
        }
        try {
            UserDTO user = userService.login(loginRequest);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getFirstName());
            return "redirect:/index";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "Log_In";
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index";
    }
}