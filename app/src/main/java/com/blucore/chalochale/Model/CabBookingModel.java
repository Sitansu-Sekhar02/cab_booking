package com.blucore.chalochale.Model;

public class CabBookingModel {
    private  String cab_id;
    private  String cab_name;
    private  String cab_image;
    private  String cab_time;
    private  String cab_price;

    public CabBookingModel(String cab_id, String cab_name, String cab_image, String cab_time, String cab_price) {
        this.cab_id = cab_id;
        this.cab_name = cab_name;
        this.cab_image = cab_image;
        this.cab_time = cab_time;
        this.cab_price = cab_price;
    }

    public String getCab_id() {
        return cab_id;
    }

    public void setCab_id(String cab_id) {
        this.cab_id = cab_id;
    }

    public String getCab_name() {
        return cab_name;
    }

    public void setCab_name(String cab_name) {
        this.cab_name = cab_name;
    }

    public String getCab_image() {
        return cab_image;
    }

    public void setCab_image(String cab_image) {
        this.cab_image = cab_image;
    }

    public String getCab_time() {
        return cab_time;
    }

    public void setCab_time(String cab_time) {
        this.cab_time = cab_time;
    }

    public String getCab_price() {
        return cab_price;
    }

    public void setCab_price(String cab_price) {
        this.cab_price = cab_price;
    }
}
