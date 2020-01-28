package com.example.uberdeliverydemo.Entities;

import androidx.annotation.Nullable;

public class Member {
    //Fields
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String emailAddress;


    //Constructors
    public Member(String id, String firstName, String lastName, String phoneNumber, String address, String emailAdress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.emailAddress = emailAdress;
    }

    public Member(String id, String firstName, String lastName, String emailAddress, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = "NULL";
        this.emailAddress = emailAddress;
    }

    private String getPhoneNumberFromOS() {

        //TODO: Imp
        return "";
    }


    //Getters and Setters

    public String getId(){ return id; }

    public void setId(String id){ this.id = id; }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    //Overrides

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        Member member = (Member) obj;

        return member.getId() == this.getId();
    }

}