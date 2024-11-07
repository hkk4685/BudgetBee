package com.example.plswork;

public class Balance {
    private String id;
    private double amount;  // Change from String to double
    private String category;
    private String timestamp;

    public Balance(String id, double amount, String category, String timestamp) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    // Getter for amount
    public double getAmount() {
        return amount;
    }

    // Getter for timestamp
    public String getTimestamp() {
        return timestamp;

    }
}

