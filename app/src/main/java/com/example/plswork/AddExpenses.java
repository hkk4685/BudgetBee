package com.example.plswork;

import android.content.Context;
import android.os.Bundle;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // Make sure to import this
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AddExpenses extends AppCompatActivity {

    private EditText addAmount;
    private Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_backspace, btn_enter;
    private Spinner categorySpinner;
    private FirebaseFirestore db;
    private String[] categories = {"Food", "Travel", "Entertainment", "Shopping", "Utilities", "Others"};
    private int[] categoryIcons = {R.drawable.food, R.drawable.car, R.drawable.entertainment, R.drawable.cart, R.drawable.utilities, R.drawable.others};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expenses);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Enable the back button on the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Expense");
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
        CustomAdapter customAdapter = new CustomAdapter(this, categories, categoryIcons);
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

        // Backspace button logic
        btn_backspace.setOnClickListener(v -> {
            String text = addAmount.getText().toString();
            if (!text.isEmpty()) {
                addAmount.setText(text.substring(0, text.length() - 1));
                addAmount.setSelection(addAmount.getText().length());
            }
        });

        // Enter button logic (e.g., submit or save the amount)
        btn_enter.setOnClickListener(v -> {
            String amount = addAmount.getText().toString();
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            if (!amount.isEmpty()) {
                addExpenseToFireStore(amount, selectedCategory);
                addAmount.setText("");
                categorySpinner.setSelection(0);
            }
        });
    }

    // Save expense to Firestore
    private void addExpenseToFireStore(String amount, String category) {
        Map<String, Object> expense = new HashMap<>();
        expense.put("amount", amount);
        expense.put("category", category);
        expense.put("timestamp", FieldValue.serverTimestamp());

        db.collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddExpenses.this, "Expenses added!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Failed to add expense", e));
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
