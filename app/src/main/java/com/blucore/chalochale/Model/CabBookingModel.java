package com.blucore.chalochale.Model;

public class CabBookingModel {
    private  String cab_id;
    private  String driver_id;
    private  String cab_name;
    private  String cab_image;
    private  String cab_time;
    private  String cab_price;
    private String distance;
    private  String cab_number;
    private  String driver_name;
    private  String driver_image;
    private  String cab_company;
    private  String driver_number;

    public CabBookingModel(String cab_id,String driver_id, String cab_name, String cab_image, String cab_time, String cab_price,String distance,String cab_number,String driver_name,String driver_image,String cab_company,String driver_number) {
        this.cab_id = cab_id;
        this.driver_id=driver_id;
        this.cab_name = cab_name;
        this.cab_image = cab_image;
        this.cab_time = cab_time;
        this.cab_price = cab_price;
        this.distance=distance;
        this.cab_number=cab_number;
        this.driver_name=driver_name;
        this.cab_company=cab_company;
        this.driver_image=driver_image;
        this.driver_number=driver_number;


    }

    public CabBookingModel() {

    }


    public String getCab_id() {
        return cab_id;
    }

    public void setCab_id(String cab_id) {
        this.cab_id = cab_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
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

    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }

    public String getCab_company() {
        return cab_company;
    }

    public void setCab_company(String cab_company) {
        this.cab_company = cab_company;
    }

    public String getDriver_number() {
        return driver_number;
    }

    public void setDriver_number(String driver_number) {
        this.driver_number = driver_number;
    }
}
