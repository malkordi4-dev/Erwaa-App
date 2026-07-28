package com.example.graduationproject;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class ServiceModel {
    private String id;
    private String nameAr;
    private String nameEn;
    private String descriptionAr;
    private String tagAr;
    private String iconName;
    private double price;
    private double priceCup;
    private boolean isActive;
    private String providerId;
    private String providerName;
    private String providerType;
    private String providerPhone;
    private String providerIdNumber;
    private String municipalityCode;
    private String region;
    private Double latitude;
    private Double longitude;
    private String providerEmail;
    private String status;
    private Timestamp createdAt;
    private String rejectReason;

    public ServiceModel() {} // Required for Firestore

    @PropertyName("id")
    public String getId() { return id; }
    @PropertyName("id")
    public void setId(String id) { this.id = id; }

    @PropertyName("name_ar")
    public String getNameAr() { return nameAr; }
    @PropertyName("name_ar")
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }

    @PropertyName("name_en")
    public String getNameEn() { return nameEn; }
    @PropertyName("name_en")
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    @PropertyName("description_ar")
    public String getDescriptionAr() { return descriptionAr; }
    @PropertyName("description_ar")
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }

    @PropertyName("tag_ar")
    public String getTagAr() { return tagAr; }
    @PropertyName("tag_ar")
    public void setTagAr(String tagAr) { this.tagAr = tagAr; }

    @PropertyName("icon_name")
    public String getIconName() { return iconName; }
    @PropertyName("icon_name")
    public void setIconName(String iconName) { this.iconName = iconName; }

    @PropertyName("price")
    public double getPrice() { return price; }
    @PropertyName("price")
    public void setPrice(double price) { this.price = price; }

    @PropertyName("priceCup")
    public double getPriceCup() { return priceCup; }
    @PropertyName("priceCup")
    public void setPriceCup(double priceCup) { this.priceCup = priceCup; }

    @PropertyName("isActive")
    public boolean isActive() { return isActive; }
    @PropertyName("isActive")
    public void setActive(boolean active) { isActive = active; }

    @PropertyName("provider_id")
    public String getProviderId() { return providerId; }
    @PropertyName("provider_id")
    public void setProviderId(String providerId) { this.providerId = providerId; }

    @PropertyName("provider_name")
    public String getProviderName() { return providerName; }
    @PropertyName("provider_name")
    public void setProviderName(String providerName) { this.providerName = providerName; }

    @PropertyName("provider_type")
    public String getProviderType() { return providerType; }
    @PropertyName("provider_type")
    public void setProviderType(String providerType) { this.providerType = providerType; }

    @PropertyName("provider_phone")
    public String getProviderPhone() { return providerPhone; }
    @PropertyName("provider_phone")
    public void setProviderPhone(String providerPhone) { this.providerPhone = providerPhone; }

    @PropertyName("provider_id_number")
    public String getProviderIdNumber() { return providerIdNumber; }
    @PropertyName("provider_id_number")
    public void setProviderIdNumber(String providerIdNumber) { this.providerIdNumber = providerIdNumber; }

    @PropertyName("municipality_code")
    public String getMunicipalityCode() { return municipalityCode; }
    @PropertyName("municipality_code")
    public void setMunicipalityCode(String municipalityCode) { this.municipalityCode = municipalityCode; }

    @PropertyName("region")
    public String getRegion() { return region; }
    @PropertyName("region")
    public void setRegion(String region) { this.region = region; }

    @PropertyName("latitude")
    public Double getLatitude() { return latitude; }
    @PropertyName("latitude")
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    @PropertyName("longitude")
    public Double getLongitude() { return longitude; }
    @PropertyName("longitude")
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    @PropertyName("provider_email")
    public String getProviderEmail() { return providerEmail; }
    @PropertyName("provider_email")
    public void setProviderEmail(String providerEmail) { this.providerEmail = providerEmail; }

    @PropertyName("status")
    public String getStatus() { return status; }
    @PropertyName("status")
    public void setStatus(String status) { this.status = status; }

    @PropertyName("created_at")
    public Timestamp getCreatedAt() { return createdAt; }
    @PropertyName("created_at")
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @PropertyName("reject_reason")
    public String getRejectReason() { return rejectReason; }
    @PropertyName("reject_reason")
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
