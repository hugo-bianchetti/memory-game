package com.example.jogomemorizacao;

public class Score {
    private Integer id;
    private String name;
    private Integer errors;
    private Integer time;

    public Score(Integer id, String name, Integer errors, Integer time) {
        this.id = id;
        this.name = name;
        this.errors = errors;
        this.time = time;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setErrors(Integer errors) {
        this.errors = errors;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public Integer getErrors() {
        return errors;
    }

    public Integer getTime() {
        return time;
    }

    public String getName() {
        return name;
    }
}
