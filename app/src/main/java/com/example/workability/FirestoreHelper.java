package com.example.workability;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference messagesRef = db.collection("messages");

    public void sendMessage(String username, String message) {
        Map<String, Object> msg = new HashMap<>();
        msg.put("username", username);
        msg.put("message", message);
        msg.put("timestamp", com.google.firebase.Timestamp.now());

        messagesRef.add(msg);
    }
}
