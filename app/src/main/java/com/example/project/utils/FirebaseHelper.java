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

    // Status constants
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_PICKED_UP = "picked_up";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    // User types
    public static final String USER_TYPE_LIVREUR = "livreur";
    public static final String USER_TYPE_MANAGER = "manager";
    public static final String USER_TYPE_CLIENT = "client";
}