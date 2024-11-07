package com.example.plswork;

import android.content.Intent;
import com.example.plswork.R;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Collections;
import java.util.Comparator;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView remainingBalanceTextView;
    private List<Transaction> transactionList;
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private boolean isEditMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        remainingBalanceTextView = findViewById(R.id.tv_balance);
        transactionList = new ArrayList<>();

        recyclerView = findViewById(R.id.rv_latest_transaction);
        transactionAdapter = new TransactionAdapter(this, transactionList);
        recyclerView.setAdapter(transactionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View customToolbar = getLayoutInflater().inflate(R.layout.toolbar, null);
        toolbar.addView(customToolbar);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );


        Button addBalanceButton = findViewById(R.id.btn_add_balance);
        addBalanceButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Add Balance Button Clicked");
            Intent intent = new Intent(MainActivity.this, AddBalance.class);
            startActivity(intent);
        });

        Button addExpenseButton = findViewById(R.id.btn_add_expense);
        addExpenseButton.setOnClickListener(v -> {
            Log.d("MainActivity", "Add Expense Button Clicked");
            Intent intent = new Intent(MainActivity.this, AddExpenses.class);
            startActivity(intent);
        });
        fetchAndDisplayTransactions();
    }


    public void refreshData() {
        fetchAndDisplayTransactions(); // Call your existing method to refresh the data
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAndDisplayTransactions();
    }

    private void fetchAndDisplayTransactions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Transaction> transactions = new ArrayList<>();
        final double[] totalBalance = {0.0};

        // Fetch income first
        db.collection("balances").get().addOnCompleteListener(balanceTask -> {
            if (balanceTask.isSuccessful()) {
                for (QueryDocumentSnapshot document : balanceTask.getResult()) {
                    double amount = 0.0;

                    try {
                        Object amountObj = document.get("amount");
                        if (amountObj instanceof Number) {
                            amount = ((Number) amountObj).doubleValue();
                        } else if (amountObj instanceof String) {
                            amount = Double.parseDouble((String) amountObj);
                        }
                    } catch (Exception ex) {
                        Log.w("Firestore", "Invalid balance amount format: " + document.getId(), ex);
                    }

                    String category = document.getString("category");
                    String timestampList = formatTimestamp(document.get("timestamp"));
                    Timestamp timestamp = document.getTimestamp("timestamp");

                    transactions.add(new Transaction(document.getId(), amount, category, timestampList, timestamp, true)); // for balance
                    totalBalance[0] += amount; // true for balance
                }

                // Fetch expenses next after balances have been fetched
                db.collection("expenses").get().addOnCompleteListener(expenseTask -> {
                    if (expenseTask.isSuccessful()) {
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
                            totalBalance[0] -= amount; // false for expense
                        }

                        // Sort the transactions by timestamp (most recent first)
                        Collections.sort(transactions, new Comparator<Transaction>() {
                            @Override
                            public int compare(Transaction t1, Transaction t2) {
                                return t2.getTimestamp().compareTo(t1.getTimestamp());
                            }
                        });

                        // Update the list after sorting
                        Collections.sort(transactions, (t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));
                        updateTransactionList(transactions);
                        remainingBalanceTextView.setText(String.format("Balance: $%.2f", totalBalance[0]));

                    } else {
                        Log.w("Firestore", "Failed to fetch expenses", expenseTask.getException());
                    }
                });
            } else {
                Log.w("Firestore", "Failed to fetch balances", balanceTask.getException());
            }
        });
    }


    // Utility method to format the timestamp
    private String formatTimestamp(Object timestampObj) {
        if (timestampObj instanceof com.google.firebase.Timestamp) {
            com.google.firebase.Timestamp timestampValue = (com.google.firebase.Timestamp) timestampObj;
            Date date = timestampValue.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
        return "Unknown";
    }

    private void updateTransactionList(List<Transaction> transactions) {
        transactionList.clear();
        transactionList.addAll(transactions);
        transactionAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                isEditMode = !isEditMode;
                Log.d("MainActivity", "Edit mode toggled: " + isEditMode);
                transactionAdapter.setEditMode(isEditMode);
                item.setTitle(isEditMode ? "Done" : "Edit");
                return true;

            case R.id.action_statistics:
                Intent intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
