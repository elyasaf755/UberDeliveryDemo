package com.example.uberdeliverydemo.Entities;

import androidx.annotation.NonNull;

public class Parcel {
    //region Enums
    public enum Type{

        SelectValue,
        Envelope,
        SmallPackage,
        LargePackage
    }
    public enum Status{
        Registered, CollectionOffered, OnTheWay, Delivered
    }

    public static String[] Types = {"Select Type", "Envelope", "Small Package", "Large Package"};
    public static String[] Weights = {"Select Weight", "Up to 0.5 Kg", "Up to 1 Kg", "Up to 5 Kg", "Up to 20 Kg"};

    //Fields
    private Status status = Status.Registered;
    private String type;
    private boolean isFragile;
    private String weight;
    private String fromAddress;
    private String toAddress;
    private String recipientFirstName;
    private String recipientLastName;
    private String recipientPhoneNumber;
    private String recipientEmailAddress;
    private String receivedDate;//received in shipment center
    private String shippedDate;//delivery guy took the parcel
    private String deliveryDate ;//delivered to customer
    private String deliveryGuyName;
    private String id;
    private String toAddressLatLng;
    private String senderEmailAddress;
    private String fromAddressLatLng;

    //Constructors
    public Parcel(Status status, String type, Boolean isFragile, String weight, String fromAddress,
                  String toAddress, String recipientFirstName, String recipientLastName, String recipientPhoneNumber,
                  String recipientEmailAddress,
                  String receivedDate, String deliveryDate, String shippedDate,
                  String deliveryPersonName, String parcelId, String toAddressLatLng, String senderEmailAddress, String fromAddressLatLng) {

        this.status = status;
        this.type = type;
        this.isFragile = isFragile;
        this.weight = weight;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.recipientFirstName = recipientFirstName;
        this.recipientLastName = recipientLastName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.recipientEmailAddress = recipientEmailAddress;
        this.receivedDate = receivedDate;
        this.shippedDate = shippedDate;
        this.deliveryDate = deliveryDate;
        this.deliveryGuyName = deliveryPersonName;
        this.id = parcelId;
        this.toAddressLatLng = toAddressLatLng;
        this.senderEmailAddress = senderEmailAddress;
        this.fromAddressLatLng = fromAddressLatLng;

    }

    public Parcel() {
        this(null,null,null,null,null, null,
                null,null,null,null,
                null,null,null,null, null,
                null, null, null);
    }

    //Getters & Setters
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Boolean getFragile() {
        return isFragile;
    }

    public void setFragile(Boolean fragile) {
        isFragile = fragile;
    }


    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }


    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }


    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }


    public String getRecipientFirstName() {
        return recipientFirstName;
    }

    public void setRecipientFirstName(String recipientFirstName) {
        this.recipientFirstName = recipientFirstName;
    }


    public String getRecipientLastName() {
        return recipientLastName;
    }

    public void setRecipientLastName(String recipientLastName) {
        this.recipientLastName = recipientLastName;
    }


    public String getRecipientPhoneNumber() {
        return recipientPhoneNumber;
    }

    public void setRecipientPhoneNumber(String recipientPhoneNumber) {
        this.recipientPhoneNumber = recipientPhoneNumber;
    }


    public String getRecipientEmailAddress() {
        return recipientEmailAddress;
    }

    public void setRecipientEmailAddress(String recipientEmailAddress) {
        this.recipientEmailAddress = recipientEmailAddress;
    }


    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }


    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String receivedDate) {
        this.shippedDate = shippedDate;
    }


    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliverDate) {
        deliverDate = deliverDate;
    }


    public String getDeliveryPersonName() {
        return deliveryGuyName;
    }

    public void setDeliveryPersonName(String deliveryPersonName) {
        this.deliveryGuyName= deliveryPersonName;
    }


    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getToAddressLatLng() {
        return toAddressLatLng;
    }

    public void setToAddressLatLng(String toAddressLatLng) {
        this.toAddressLatLng = toAddressLatLng;
    }

    public String getSenderEmailAddress() {
        return senderEmailAddress;
    }

    public void setSenderEmailAddress(String senderEmailAddress) {
        this.senderEmailAddress = senderEmailAddress;
    }

    public String getFromAddressLatLng() {
        return fromAddressLatLng;
    }

    public void setFromAddressLatLng(String fromAddressLatLng) {
        this.fromAddressLatLng = fromAddressLatLng;
    }

    //Overrides

    @NonNull
    @Override
    public String toString() {
        return "Status: " + getStatus() + ".\nParcelID:" + getID() + ".\n" + "To address: " + toAddress;
    }
}
