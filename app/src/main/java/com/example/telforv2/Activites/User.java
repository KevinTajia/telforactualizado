package com.example.telforv2.Activites;

public class User {

    private String task;
    private String description;
    private String id;
    private String date;
    private String new_time;

    public User() {
    }

    public User(String task, String description, String id, String date, String new_time) {
        this.task = task;
        this.description = description;
        this.id = id;
        this.date = date;
        this.new_time = new_time;
    }


    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNew_time() {
        return new_time;
    }

    public void setNew_time(String new_time) {
        this.new_time = new_time;
    }
}
