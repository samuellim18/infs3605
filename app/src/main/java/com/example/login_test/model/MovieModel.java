package com.example.login_test.model;

public class MovieModel {
    private String MovieName;

    public MovieModel(String MovieName)
    {
        this.MovieName = MovieName;
    }

    public String getMovieName() {
        return this.MovieName;
    }
}