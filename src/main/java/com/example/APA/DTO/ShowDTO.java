package com.example.APA.DTO;

public class ShowDTO {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long screenId;
    private String date;
    private String time;

    public ShowDTO() {}

    public ShowDTO(Long id, Long movieId, String movieTitle,
                   Long screenId, String date, String time) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.screenId = screenId;
        this.date = date;
        this.time = time;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public Long getScreenId() { return screenId; }
    public void setScreenId(Long screenId) { this.screenId = screenId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}