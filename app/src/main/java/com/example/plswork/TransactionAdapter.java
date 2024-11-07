package com.example.plswork;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactionList;
    private boolean isEditMode = false;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        Log.d("TransactionAdapter", "Edit mode set to: " + isEditMode);
        notifyDataSetChanged();
    }

    public interface OnTransactionDeletedListener {
        void onTransactionDeleted();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvCategory.setText(transaction.getCategory());
        String formattedAmount = transaction.isBalance() ? "+" : "-";
        formattedAmount += String.format("$%.2f", transaction.getAmount());
        holder.tvAmount.setText(formattedAmount);
        holder.tvDate.setText(transaction.getTimestampList());

        Log.d("TransactionAdapter", "Binding transaction at position: " + position + ", Edit Mode: " + isEditMode);

        if (isEditMode) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.tvAmount.setPadding(0, 0, 0, 0); // Adjust padding as needed
        } else {
            holder.deleteButton.setVisibility(View.GONE);
            holder.tvAmount.setPadding(0, 0, 0, 0);
        }


        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // If 'Yes', remove the transaction
                        deleteTransaction(transaction);
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        // If 'No', just dismiss the dialog
                        dialog.dismiss();
                    })
                    .create()
                    .show();  // Delete the transaction
        });

        if (transaction.isBalance()) {
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green)); // Replace with your green color resource
        } else {
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red)); // Replace with your red color resource
        }

        // Set the category icon based on the category
        switch (transaction.getCategory()) {
            case "Salary":
                holder.ivCategoryIcon.setImageResource(R.drawable.salary);
                break;
            case "Savings":
                holder.ivCategoryIcon.setImageResource(R.drawable.savings);
                break;
            case "Bonus":
                holder.ivCategoryIcon.setImageResource(R.drawable.bonus);
                break;
            case "Shopping":
                holder.ivCategoryIcon.setImageResource(R.drawable.cart);
                break;
            case "Travel":
                holder.ivCategoryIcon.setImageResource(R.drawable.car);
                break;
            case "Entertainment":
                holder.ivCategoryIcon.setImageResource(R.drawable.entertainment);
                break;
            case "Utilities":
                holder.ivCategoryIcon.setImageResource(R.drawable.utilities);
                break;
            case "Food":
                holder.ivCategoryIcon.setImageResource(R.drawable.food);
                break;
            default:
                holder.ivCategoryIcon.setImageResource(R.drawable.others);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


    private void deleteTransaction(Transaction transaction) {
        transactionList.remove(transaction);
        notifyDataSetChanged();

        // Delete from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collection = transaction.isBalance() ? "balances" : "expenses";
        db.collection(collection).document(transaction.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("TransactionAdapter", "Transaction deleted successfully");
                    // Notify the main activity to refresh the entire view
                    if (context instanceof MainActivity) {
                        ((MainActivity) context).refreshData(); // Call refresh method
                    }
                })
                .addOnFailureListener(e -> Log.w("TransactionAdapter", "Error deleting transaction", e));
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount;
        ImageView ivCategoryIcon;
        ImageButton deleteButton;


        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
