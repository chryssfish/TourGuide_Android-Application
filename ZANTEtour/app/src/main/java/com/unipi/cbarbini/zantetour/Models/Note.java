package com.unipi.cbarbini.zantetour.Models;
import java.util.Date;
public class Note {
    //Model Class for a message
    private  String user;
    private String title;
    private  String note;
    private  long time;

    public Note(String user,String title, String note) {

        this.user= user;
        this.title=title;
        this.note=note;
        time = new Date().getTime();
    }
    public Note(){}

    public  String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public   String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public   String getNote() { return note; }

    public void setNote(String note) {
        this.note = note;
    }

    public   long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
