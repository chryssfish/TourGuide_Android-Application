package com.unipi.cbarbini.zantetour.Models;


public class Attraction {
    //Model Class for an attraction
    private String title;
    private  String telephone;
    private  String image;
    private String location;
    private String generalinfo;
    private String address;
    private String stars;
    private String website;

    public Attraction(String title,String generalinfo, String telephone, String image, String location,String address) {

        this.title= title;
        this.generalinfo=generalinfo;
        this.telephone=telephone;
        this.image=image;
        this.location=location;
        this.address=address;

    }
    public Attraction(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String photos) {
        this.image = photos;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGeneralinfo() {
        return generalinfo;
    }

    public void setGeneralinfo(String generalinfo) {
        this.generalinfo = generalinfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStars() { return stars; }

    public void setStars(String stars) { this.stars = stars; }

    public String getWebsite() { return website; }

    public void setWebsite(String website) { this.website = website; }

}
