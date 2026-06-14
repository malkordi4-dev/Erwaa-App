package com.example.graduationproject;

import com.google.gson.annotations.SerializedName;

public class ServiceModel {
    @SerializedName("id")
    private int id;

    @SerializedName("name_ar")
    private String nameAr;

    @SerializedName("name_en")
    private String nameEn;

    @SerializedName("description_ar")
    private String descriptionAr;

    @SerializedName("tag_ar")
    private String tagAr;

    @SerializedName("icon_name")
    private String iconName;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }
    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }
    public String getDescriptionAr() { return descriptionAr; }
    public void setDescriptionAr(String descriptionAr) { this.descriptionAr = descriptionAr; }
    public String getTagAr() { return tagAr; }
    public void setTagAr(String tagAr) { this.tagAr = tagAr; }
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
}
