package com.example.cointoon;


// model class for recycler view
public class TransactionItem implements HistoryItem {

    private String name;
    private String date;
    private String amount;
    private int iconResId;

    private String types;

    //firebase requires this
    public TransactionItem (){};

    // getters and setters
    public TransactionItem(String name, String date, String amount, int iconResId , String types) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.iconResId = iconResId;
        this.types = types;
    }


    public String getName() { return name; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public int getIconResId() { return iconResId; }
    public String getTypes() { return types; }




    // get type is parent class abstract method
    // tell what type of data to show
    // as we are showing transaction info and date header
    // so this is trans. info
    @Override
    public int getType() {
        return TYPE_TRANSACTION;
    }


}
