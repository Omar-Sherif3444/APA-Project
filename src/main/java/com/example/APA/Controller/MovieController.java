package com.example.APA.Controller;
import com.example.APA.Service.MovieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MovieController {
    private final MovieService movieService;
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }
    @GetMapping("/Movies")
    public String movies(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "Movies";
    }
    @GetMapping("/Bershama")
    public String bershama() { return "Bershama"; }

    @GetMapping("/Egybest")
    public String egybest() { return "Egybest"; }

    @GetMapping("/Hoppers")
    public String hoppers() { return "Hoppers"; }

    @GetMapping("/Safah_el_tagamoaa")
    public String safahElTagamoaa() { return "Safah_el_tagamoaa"; }

    @GetMapping("/Scream7")
    public String scream7() { return "Scream7"; }

    @GetMapping("/The_Raiders")
    public String theRaiders() { return "The_Raiders"; }
}