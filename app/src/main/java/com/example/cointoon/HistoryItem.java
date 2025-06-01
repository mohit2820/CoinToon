package com.example.cointoon;


// interface for inserting date dynamically
// in history fragment for each transaction
public interface HistoryItem {
    int TYPE_DATE = 0;
    int TYPE_TRANSACTION = 1;

    int getType();
}

