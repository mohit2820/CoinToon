package com.example.cointoon;

public class DateHeaderItem implements HistoryItem {
    private String date;

    public DateHeaderItem(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
