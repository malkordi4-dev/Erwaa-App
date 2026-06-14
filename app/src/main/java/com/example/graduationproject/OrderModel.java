package com.example.graduationproject;

import com.google.gson.annotations.SerializedName;

public class OrderModel {
    @SerializedName("id")
    private String id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("provider_id")
    private Integer providerId;

    @SerializedName("service_id")
    private Integer serviceId;

    @SerializedName("status")
    private String status; // 'pending', 'accepted', 'on_way', 'delivered', 'cancelled'

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unit")
    private String unit; // 'لتر' or 'خزان'

    @SerializedName("total_price")
    private Double totalPrice;

    @SerializedName("delivery_lat")
    private double deliveryLat;

    @SerializedName("delivery_lng")
    private double deliveryLng;

    @SerializedName("address_details")
    private String addressDetails;

    @SerializedName("notes")
    private String notes;

    @SerializedName("scheduled_time")
    private String scheduledTime; // 'الآن', 'اليوم', 'غداً'

    @SerializedName("created_at")
    private String createdAt;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public Integer getProviderId() { return providerId; }
    public void setProviderId(Integer providerId) { this.providerId = providerId; }
    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    public double getDeliveryLat() { return deliveryLat; }
    public void setDeliveryLat(double deliveryLat) { this.deliveryLat = deliveryLat; }
    public double getDeliveryLng() { return deliveryLng; }
    public void setDeliveryLng(double deliveryLng) { this.deliveryLng = deliveryLng; }
    public String getAddressDetails() { return addressDetails; }
    public void setAddressDetails(String addressDetails) { this.addressDetails = addressDetails; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
