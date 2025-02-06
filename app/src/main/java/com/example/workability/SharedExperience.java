package com.example.workability;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class SharedExperience {
    private String userEmail;
    private String disabilityType;
    private String experienceText;

    @ServerTimestamp
    private Date timestamp;

    // **No-Arg Constructor Required for Firestore Deserialization**
    public SharedExperience() {}

    public SharedExperience(String userEmail, String disabilityType, String experienceText,Date date) {
        this.userEmail = userEmail;
        this.disabilityType = disabilityType;
        this.experienceText = experienceText;
        this.timestamp = date;
    }

    // Getters (Required for Firestore deserialization)
    public String getUserEmail() { return userEmail; }
    public String getDisabilityType() { return disabilityType; }
    public String getExperienceText() { return experienceText; }
    public Date getTimestamp() { return timestamp; }
}
