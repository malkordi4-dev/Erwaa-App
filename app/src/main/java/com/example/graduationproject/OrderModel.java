package com.example.graduationproject;

import com.google.firebase.firestore.Exclude;
import com.google.gson.annotations.SerializedName;

public class OrderModel {
    @Exclude
    private String id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("provider_id")
    private String providerId;

    @SerializedName("provider_name")
    private String providerName; // تم إضافة هذا الحقل

    @SerializedName("service_id")
    private String serviceId;

    @SerializedName("status")
    private String status; // 'pending', 'accepted', 'on_way', 'delivered', 'cancelled'

    @SerializedName("order_type")
    private String orderType;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unit")
    private String unit;

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
    private String scheduledTime;

    @SerializedName("created_at")
    private Object createdAt;

    public OrderModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getProviderName() { return providerName; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
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
    public Object getCreatedAt() { return createdAt; }
    public void setCreatedAt(Object createdAt) { this.createdAt = createdAt; }
}
