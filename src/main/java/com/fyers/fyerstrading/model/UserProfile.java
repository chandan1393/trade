package com.fyers.fyerstrading.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown fields
public class UserProfile {
    
    @JsonProperty("email_id")
    private String emailId;
    
    private String image;
    private boolean totp;
    
    @JsonProperty("mtf_enabled")
    private boolean mtfEnabled;
    
    @JsonProperty("display_name")
    private String displayName;
    
    @JsonProperty("fy_id")
    private String fyId;
    
    @JsonProperty("pwd_change_date")
    private String pwdChangeDate;
    
    @JsonProperty("ddpi_enabled")
    private boolean ddpiEnabled;
    
    private String name;
    
    @JsonProperty("pin_change_date")
    private String pinChangeDate;
    
    @JsonProperty("pwd_to_expire")
    private int pwdToExpire;
    
    @JsonProperty("PAN") // Mapping PAN with uppercase key in JSON
    private String pan;
    
    @JsonProperty("mobile_number")
    private String mobileNumber;
    
    // Getters and Setters
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isTotp() {
        return totp;
    }

    public void setTotp(boolean totp) {
        this.totp = totp;
    }

    public boolean isMtfEnabled() {
        return mtfEnabled;
    }

    public void setMtfEnabled(boolean mtfEnabled) {
        this.mtfEnabled = mtfEnabled;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFyId() {
        return fyId;
    }

    public void setFyId(String fyId) {
        this.fyId = fyId;
    }

    public String getPwdChangeDate() {
        return pwdChangeDate;
    }

    public void setPwdChangeDate(String pwdChangeDate) {
        this.pwdChangeDate = pwdChangeDate;
    }

    public boolean isDdpiEnabled() {
        return ddpiEnabled;
    }

    public void setDdpiEnabled(boolean ddpiEnabled) {
        this.ddpiEnabled = ddpiEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinChangeDate() {
        return pinChangeDate;
    }

    public void setPinChangeDate(String pinChangeDate) {
        this.pinChangeDate = pinChangeDate;
    }

    public int getPwdToExpire() {
        return pwdToExpire;
    }

    public void setPwdToExpire(int pwdToExpire) {
        this.pwdToExpire = pwdToExpire;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "emailId='" + emailId + '\'' +
                ", image='" + image + '\'' +
                ", totp=" + totp +
                ", mtfEnabled=" + mtfEnabled +
                ", displayName='" + displayName + '\'' +
                ", fyId='" + fyId + '\'' +
                ", pwdChangeDate='" + pwdChangeDate + '\'' +
                ", ddpiEnabled=" + ddpiEnabled +
                ", name='" + name + '\'' +
                ", pinChangeDate='" + pinChangeDate + '\'' +
                ", pwdToExpire=" + pwdToExpire +
                ", pan='" + pan + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                '}';
    }
}
