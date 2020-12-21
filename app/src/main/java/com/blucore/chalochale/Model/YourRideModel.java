package com.blucore.chalochale.Model;

public class YourRideModel {
    private  String source;
    private  String destination;
    private  String cab_price;
    private  String jouney_date;
    private  String cab_number;
    private  String driver_name;
    private  String driver_number;
    private  String total_distance;
    private  String cab_image;
    private  String driver_image;


    public YourRideModel(String source, String destination, String cab_price, String jouney_date,String cab_number,String driver_name,String driver_number,String total_distance,String cab_image,String driver_image) {
        this.source = source;
        this.destination = destination;
        this.cab_price = cab_price;
        this.jouney_date = jouney_date;
        this.cab_number=cab_number;
        this.driver_name=driver_name;
        this.driver_number=driver_number;
        this.total_distance=total_distance;
        this.cab_image=cab_image;
        this.driver_image=driver_image;

    }

    public YourRideModel() {
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCab_price() {
        return cab_price;
    }

    public void setCab_price(String cab_price) {
        this.cab_price = cab_price;
    }

    public String getJouney_date() {
        return jouney_date;
    }

    public void setJouney_date(String jouney_date) {
        this.jouney_date = jouney_date;
    }

    public String getCab_number() {
        return cab_number;
    }

    public void setCab_number(String cab_number) {
        this.cab_number = cab_number;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_number() {
        return driver_number;
    }

    public void setDriver_number(String driver_number) {
        this.driver_number = driver_number;
    }

    public String getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(String total_distance) {
        this.total_distance = total_distance;
    }

    public String getCab_image() {
        return cab_image;
    }

    public void setCab_image(String cab_image) {
        this.cab_image = cab_image;
    }

    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }
}
