package com.blucore.chalochale.Model;

public class YourRideModel {
    private  String source;
    private  String destination;
    private  String cab_price;
    private  String cab_date;

    public YourRideModel(String source, String destination, String cab_price, String cab_date) {
        this.source = source;
        this.destination = destination;
        this.cab_price = cab_price;
        this.cab_date = cab_date;
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

    public String getCab_date() {
        return cab_date;
    }

    public void setCab_date(String cab_date) {
        this.cab_date = cab_date;
    }
}
