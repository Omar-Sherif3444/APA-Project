package com.example.APA.Service;

import com.example.APA.DTO.MovieDTO;
import com.example.APA.Model.Movie;
import com.example.APA.Repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDTO> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<MovieDTO> getMovieById(Long id) {
        return movieRepository.findById(id).map(this::toDTO);
    }

    public Optional<MovieDTO> getMovieByTitle(String title) {
        return movieRepository.findByTitle(title).map(this::toDTO);
    }

    public MovieDTO saveMovie(MovieDTO dto) {
        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setGenre(dto.getGenre());
        movie.setDuration(dto.getDuration());
        movie.setLanguage(dto.getLanguage());
        return toDTO(movieRepository.save(movie));
    }

    private MovieDTO toDTO(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getLanguage()
        );
    }
}