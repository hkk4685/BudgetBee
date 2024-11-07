package com.example.plswork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plswork.Expense;
import com.example.plswork.R;

import org.w3c.dom.Text;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private Context context;
    private List<Expense> expenseList;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvCategory.setText(expense.getCategory());
        holder.tvAmount.setText(String.format("$%.2f", expense.getAmount()));
        holder.tvDate.setText(expense.getTimestampList());

        switch (expense.getCategory()) {
            case "Car":
                holder.ivCategoryIcon.setImageResource(R.drawable.car); // Ensure you have car.png in drawable
                break;
            case "Food":
                holder.ivCategoryIcon.setImageResource(R.drawable.food); // Ensure you have food.png in drawable
                break;
            case "Shopping":
                holder.ivCategoryIcon.setImageResource(R.drawable.cart); // Ensure you have rent.png in drawable
                break;
            case "Entertainment":
                holder.ivCategoryIcon.setImageResource(R.drawable.entertainment); // A default icon
                break;
            case "Utilities":
                holder.ivCategoryIcon.setImageResource(R.drawable.utilities); // A default icon
                break;
            default:
                holder.ivCategoryIcon.setImageResource(R.drawable.others); // A default icon
                break;
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvDate, tvAmount;
        ImageView ivCategoryIcon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            // Initialize your view elements here
            // Example: tvAmount = itemView.findViewById(R.id.tv_amount);
        }
    }
}
