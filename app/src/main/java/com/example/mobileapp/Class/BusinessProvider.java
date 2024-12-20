package com.example.mobileapp.Class;

public class BusinessProvider {
    private String providerId;
    private String providerName;
    private String providerProductId;
    private String providerPhone;
    private String providerHead;
    private String providerEmail;
    private String providerDate;
    private String providerStatus;
    private String providerNote;

    // Constructor to initialize all fields
    public BusinessProvider(String providerId, String providerName, String providerProductId, String providerPhone,
                             String providerHead, String providerEmail, String providerDate, String providerStatus,
                             String providerNote) {
        this.providerId = providerId;
        this.providerName = providerName;
        this.providerProductId = providerProductId;
        this.providerPhone = providerPhone;
        this.providerHead = providerHead;
        this.providerEmail = providerEmail;
        this.providerDate = providerDate;
        this.providerStatus = providerStatus;
        this.providerNote = providerNote;
    }

    // Getter methods to access the private fields
    public String getProviderId() {
        return providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderProductId() {
        return providerProductId;
    }

    public String getProviderPhone() {
        return providerPhone;
    }

    public String getProviderHead() {
        return providerHead;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public String getProviderDate() {
        return providerDate;
    }

    public String getProviderStatus() {
        return providerStatus;
    }

    public String getProviderNote() {
        return providerNote;
    }
}
