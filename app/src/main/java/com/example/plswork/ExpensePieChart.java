package com.example.plswork;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpensePieChart extends Fragment {
    private Map<String, Integer> categoryColorMap;
    private FirebaseFirestore db;

    public ExpensePieChart() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expense_pie_chart, container, false);

        PieChart expensePieChart = view.findViewById(R.id.expense_pie_chart);
        db = FirebaseFirestore.getInstance();  // Initialize FirebaseFirestore

        categoryColorMap = new HashMap<>();
        categoryColorMap.put("Food", ContextCompat.getColor(getContext(), R.color.colorFood));
        categoryColorMap.put("Others", ContextCompat.getColor(getContext(), R.color.colorOthers));
        categoryColorMap.put("Travel", ContextCompat.getColor(getContext(), R.color.colorCar));
        categoryColorMap.put("Utilities", ContextCompat.getColor(getContext(), R.color.colorUtilities));
        categoryColorMap.put("Entertainment", ContextCompat.getColor(getContext(), R.color.colorEntertainment));
        categoryColorMap.put("Shopping", ContextCompat.getColor(getContext(), R.color.colorShopping));

        setupExpensePieChart(expensePieChart);


        return view;
    }

    private void setupExpensePieChart(PieChart pieChart) {
        db.collection("expenses").get().addOnCompleteListener(expenseTask -> {
            if (expenseTask.isSuccessful()) {
                List<Transaction> transactions = new ArrayList<>();
                final double[] totalExpenses = {0.0};

                for (QueryDocumentSnapshot document : expenseTask.getResult()) {
                    double amount = 0.0;

                    try {
                        Object amountObj = document.get("amount");
                        if (amountObj instanceof Number) {
                            amount = ((Number) amountObj).doubleValue();
                        } else if (amountObj instanceof String) {
                            amount = Double.parseDouble((String) amountObj);
                        }
                    } catch (Exception ex) {
                        Log.w("Firestore", "Invalid expense amount format: " + document.getId(), ex);
                    }

                    String category = document.getString("category");
                    String timestampList = formatTimestamp(document.get("timestamp"));
                    Timestamp timestamp = document.getTimestamp("timestamp");

                    transactions.add(new Transaction(document.getId(), amount, category, timestampList, timestamp, false)); // for expense
                    totalExpenses[0] += amount; // Accumulate expenses
                }

                // Update PieChart with expenses data
                updatePieChart(pieChart, transactions);
                pieChart.getLegend().setEnabled(false);
            }
        });
    }

    private void updatePieChart(PieChart pieChart, List<Transaction> transactions) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        Map<String, Float> categoryTotals = new HashMap<>();

        // Prepare the PieEntry list based on the transactions
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                String category = transaction.getCategory();
                float amount = (float) transaction.getAmount();
                categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
            }
        }

        // Create PieEntry objects from the accumulated totals
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));

            // Fetch color for each category and add to colors list
            int color = categoryColorMap.getOrDefault(entry.getKey(), Color.GRAY);
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        dataSet.setValueTextSize(16f);
        pieChart.setEntryLabelTextSize(16f);
        pieChart.setEntryLabelColor(Color.BLACK);
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // Refresh the chart
    }

    private String formatTimestamp(Object timestampObj) {
        // Convert the timestamp to a human-readable format
        return timestampObj != null ? timestampObj.toString() : "N/A";
    }
}
