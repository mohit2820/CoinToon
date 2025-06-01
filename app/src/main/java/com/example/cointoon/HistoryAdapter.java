package com.example.cointoon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cointoon.databinding.ItemDateHeaderBinding;
import com.example.cointoon.databinding.ItemTransactionBinding;

import java.util.List;
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<HistoryItem> itemList;

    public HistoryAdapter(Context context, List<HistoryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == HistoryItem.TYPE_DATE) {
            ItemDateHeaderBinding binding = ItemDateHeaderBinding.inflate(inflater, parent, false);
            return new DateViewHolder(binding);
        } else {
            ItemTransactionBinding binding = ItemTransactionBinding.inflate(inflater, parent, false);
            return new TransactionViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HistoryItem item = itemList.get(position);

        if (holder instanceof DateViewHolder) {
            ((DateViewHolder) holder).bind((DateHeaderItem) item);
        } else {
            ((TransactionViewHolder) holder).bind((TransactionItem) item);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // --- ViewHolders ---

    static class DateViewHolder extends RecyclerView.ViewHolder {
        private final ItemDateHeaderBinding binding;

        DateViewHolder(ItemDateHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DateHeaderItem item) {
            binding.tvDateHeader.setText(item.getDate());
        }
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionBinding binding;

        TransactionViewHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TransactionItem item) {
            binding.transactionIcon.setImageResource(item.getIconResId());
            binding.transactionName.setText(item.getName());
            binding.transactionDateTime.setText(item.getDate());
            binding.transactionAmount.setText("₹" + item.getAmount());
        }
    }
}
