package com.unipi.cbarbini.zantetour.Models;

import java.util.Date;

public class Comment {

    //Model Class for a message
    private  String user;
    private  String message;
    private  String stars;
    private  long time;

    public Comment (String user, String stars , String message) {

        this.user= user;
        this.message=message;
        this.stars=stars;
        time = new Date().getTime();
    }


    public Comment(){}

    public  String getUser() {
        return user;
    }

    public void setUser(String user) { this.user = user; }

    public String getStars() { return stars; }

    public void setStars(String stars) {this.stars = stars; }

    public   String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public   long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }}
