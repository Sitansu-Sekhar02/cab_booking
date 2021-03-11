package com.blucore.cabchalochale.Model;

public class DriverRideModel {
    private  String source;
    private  String destination;
    private  String cab_price;
    private  String jouney_date;
    private  String user_number;
    private  String payment_mode;

    public DriverRideModel() {
    }

    public DriverRideModel(String source, String destination, String cab_price, String jouney_date, String user_number, String payment_mode) {
        this.source = source;
        this.destination = destination;
        this.cab_price = cab_price;
        this.jouney_date = jouney_date;
        this.user_number = user_number;
        this.payment_mode = payment_mode;
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

    public String getUser_number() {
        return user_number;
    }

    public void setUser_number(String user_number) {
        this.user_number = user_number;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }
}
