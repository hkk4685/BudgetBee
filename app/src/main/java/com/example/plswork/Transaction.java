package com.example.plswork;

import com.google.firebase.Timestamp;

public class Transaction {
    private String id;
    private double amount;
    private String category;
    private String timestampList; // For display
    private Timestamp timestamp;   // For sorting and data handling
    private boolean isBalance;

    public Transaction(String id, double amount, String category, String timestampList, Timestamp timestamp, boolean isBalance) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.timestampList = timestampList; // Initialize the string representation
        this.timestamp = timestamp;          // Initialize the Timestamp object
        this.isBalance = isBalance;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getTimestampList() {
        return timestampList;
    }

    public boolean isBalance() {
        return isBalance;
    }
}
