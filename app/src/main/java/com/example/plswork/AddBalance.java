package com.example.plswork;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddBalance extends AppCompatActivity {

    private EditText addAmount;
    private Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_backspace, btn_enter;
    private Spinner categorySpinner;
    private FirebaseFirestore db;
    private String[] categories = {"Salary", "Savings", "Bonus", "Others"};
    private int[] categoryIcons = {R.drawable.salary, R.drawable.savings, R.drawable.bonus1, R.drawable.others};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_balance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Enable the back button on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Income");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        addAmount = findViewById(R.id.addAmount);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_backspace = findViewById(R.id.btn_backspace);
        btn_enter = findViewById(R.id.btn_enter);
        categorySpinner = findViewById(R.id.categorySpinner);

        // Set up the custom adapter for the Spinner
        AddBalance.CustomAdapter customAdapter = new AddBalance.CustomAdapter(this, categories, categoryIcons);
        categorySpinner.setAdapter(customAdapter);

        View.OnClickListener numberClickListener = v -> {
            Button b = (Button) v;
            addAmount.append(b.getText().toString());
        };

        // Attach the number click listener to all number buttons
        btn_0.setOnClickListener(numberClickListener);
        btn_1.setOnClickListener(numberClickListener);
        btn_2.setOnClickListener(numberClickListener);
        btn_3.setOnClickListener(numberClickListener);
        btn_4.setOnClickListener(numberClickListener);
        btn_5.setOnClickListener(numberClickListener);
        btn_6.setOnClickListener(numberClickListener);
        btn_7.setOnClickListener(numberClickListener);
        btn_8.setOnClickListener(numberClickListener);
        btn_9.setOnClickListener(numberClickListener);

        btn_enter.setOnClickListener(v -> addBalanceToFirestore());

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle category selection if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where no category is selected (optional)
            }
        });
        btn_backspace.setOnClickListener(v -> {
            String text = addAmount.getText().toString();
            if (!text.isEmpty()) {
                addAmount.setText(text.substring(0, text.length() - 1));
                addAmount.setSelection(addAmount.getText().length());
            }
        });
        btn_enter.setOnClickListener(v -> {
            String amount = addAmount.getText().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            if (!amount.isEmpty()) {
                addBalanceToFirestore(); // Use the correct method name here
                addAmount.setText("");
                categorySpinner.setSelection(0);
            }
        });

    }

    private void addBalanceToFirestore() {
        String amountString = addAmount.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();

        if (amountString.isEmpty()) {
            Toast.makeText(AddBalance.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountString);
        } catch (NumberFormatException e) {
            Toast.makeText(AddBalance.this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> balance = new HashMap<>();
        balance.put("amount", amount);
        balance.put("category", category);
        balance.put("timestamp", new com.google.firebase.Timestamp(new java.util.Date()));

        db.collection("balances")
                .add(balance)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddBalance.this, "Balance added!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddBalance.this, "Error adding balance: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // CustomAdapter class for the Spinner
    private static class CustomAdapter extends ArrayAdapter<String> {
        private Context context;
        private String[] categoryNames;
        private int[] icons;

        public CustomAdapter(Context context, String[] categoryNames, int[] icons) {
            super(context, R.layout.spinner_item, categoryNames);
            this.context = context;
            this.categoryNames = categoryNames;
            this.icons = icons;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return createCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return createCustomView(position, convertView, parent);
        }

        private View createCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.spinner_item, parent, false);

            // Set category icon
            ImageView icon = itemView.findViewById(R.id.icon);
            icon.setImageResource(icons[position]);

            // Set category name
            TextView categoryName = itemView.findViewById(R.id.text);
            categoryName.setText(categoryNames[position]);

            return itemView;
        }
    }
}
