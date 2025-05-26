package com.quizzyonline.app.services;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.Calendar;

public class CleanupService {

    public static void deleteOldPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        db.collection("discussions")
                .whereLessThan("timestamp", new com.google.firebase.Timestamp(calendar.getTime()))
                .whereEqualTo("isPermanent", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots) {
                        db.collection("discussions").document(doc.getId()).delete();
                    }
                });
    }
}
