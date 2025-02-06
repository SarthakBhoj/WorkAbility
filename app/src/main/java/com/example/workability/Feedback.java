package com.example.workability;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Feedback {
    private String username;
    private String feedbackText;

    @ServerTimestamp
    private Date timestamp;

    // **No-Arg Constructor Required for Firestore**
    public Feedback() {}

    public Feedback(String username, String feedbackText, Date timestamp) {
        this.username = username;
        this.feedbackText = feedbackText;
        this.timestamp = timestamp;
    }

    public String getUsername() { return username; }
    public String getFeedbackText() { return feedbackText; }
    public Date getTimestamp() { return timestamp; }
}
