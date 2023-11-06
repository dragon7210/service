package com.example.springboot.models;


import jakarta.persistence.*;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "VEG")

public class VegModel extends RepresentationModel<VegModel> implements Serializable {

    private static final long serialVersionUID= 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idpeople;
    private String firstname;
    private String lastname;
    private String address1;
    private String city;
    private String state;
    private String zip_code;
    private String country;

    public VegModel(String firstname, String lastname, String address1, String city, String state, String zip_code, String country){
        this.firstname = firstname;
        this.lastname = lastname;
        this.address1 = address1;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
        this.country = country;

    }

    public VegModel() {

    }

    public UUID getIdpeople() {
        return idpeople;
    }

    public void setIdpeople(UUID idpeople) {
        this.idpeople = idpeople;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname){ this.firstname = firstname;}

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry(){
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
}
