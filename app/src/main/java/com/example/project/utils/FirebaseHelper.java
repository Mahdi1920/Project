package com.example.project.utils;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;

public class FirebaseHelper {
    private static FirebaseFirestore db;

    public static FirebaseFirestore getInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
        }
        return db;
    }

    public static CollectionReference getUsersCollection() {
        return getInstance().collection("users");
    }

    public static CollectionReference getCommandesCollection() {
        return getInstance().collection("commandes");
    }

    public static CollectionReference getDeliveryTrackingCollection() {
        return getInstance().collection("delivery_tracking");
    }

    // Status constants - NEW FLOW
    public static final String STATUS_PENDING = "pending";              // Order created, waiting for manager
    public static final String STATUS_MANAGER_ACCEPTED = "manager_accepted";  // Manager accepted, waiting for livreur
    public static final String STATUS_LIVREUR_ACCEPTED = "livreur_accepted";  // Livreur accepted delivery
    public static final String STATUS_PICKED_UP = "picked_up";          // Livreur picked up order
    public static final String STATUS_DELIVERED = "delivered";          // Order delivered
    public static final String STATUS_CANCELLED = "cancelled";          // Order cancelled/rejected

    // User types - ONLY TWO NOW
    public static final String USER_TYPE_LIVREUR = "livreur";
    public static final String USER_TYPE_MANAGER = "manager";
    public static final String STATUS_ACCEPTED = "accepted";

}