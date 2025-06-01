package com.example.cointoon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cointoon.databinding.FragmentChartBinding;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    FirebaseAuth auth;

    DatabaseReference userDb;

    FragmentChartBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noUnselectedChartButton(); // user can no unselect the button
        auth = FirebaseAuth.getInstance();
        setupToggleButtons();
        loadData(this::showPieChart);  // deafult chart

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChartBinding.inflate(inflater);
        return binding.getRoot();
    }



    // user can not deselect the button
    private  void noUnselectedChartButton(){

        binding.chartToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(!isChecked && group.getCheckedButtonId() == View.NO_ID){
                    group.check(checkedId);
                }
            }
        });
    }

    private  void loadData(onChartLoaded callback){
        FirebaseUser currentuser = auth.getCurrentUser();
        String uid = currentuser.getUid();
        userDb = FirebaseDatabase.getInstance().getReference("Users").child(uid) .child("transaction");

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalIncome = 0;
                int totalExpense = 0;

                for(DataSnapshot data : snapshot.getChildren()){

                    TransactionItem item = data.getValue(TransactionItem.class);

                    if(item!=null){
                        int amount = 0;
                        try {
                            amount = Integer.parseInt(item.getAmount());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        if ("income".equalsIgnoreCase(item.getTypes())) {
                            totalIncome += amount;
                        } else {
                            totalExpense += amount;
                        }

                    }
                }

                int balance = totalIncome - totalExpense;


                int[] data = {totalExpense , balance , totalIncome};

                // setting data on chart fragment
                binding.totalIncome.setText(String.valueOf("₹" + totalIncome));
                binding.totalExpenses.setText(String.valueOf("₹" + totalExpense));
                binding.netSavings.setText(String.valueOf("₹" + balance));

                callback.onDataLoaded(data);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), "Data Not Loaded" , Toast.LENGTH_SHORT).show();
            }
        });

    }

    // pie chart
    private void showPieChart(int[] data) {

        int expense = data[0];
        int balance = data[1];
        int income = data[2];

        binding.pieChart.setVisibility(View.VISIBLE);
        binding.barChart.setVisibility(View.GONE);
        binding.lineChart.setVisibility(View.GONE);

        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(expense, "Expenses"));
        entries.add(new PieEntry(balance, "Savings"));
        entries.add(new PieEntry(income , "Income"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);

        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.invalidate(); // refresh chart
    }

    private void showBarChart(int[] data) {
        binding.pieChart.setVisibility(View.GONE);
        binding.barChart.setVisibility(View.VISIBLE);
        binding.lineChart.setVisibility(View.GONE);
        binding.pieChart.getDescription().setEnabled(false);


        int expense = data[0];
        int balance = data[1];
        int income = data[2];

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, expense)); // Expenses
        entries.add(new BarEntry(1, balance)); // Savings
        entries.add(new BarEntry(2, income)); // Income

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(ColorTemplate.getHoloBlue());
        BarData data1 = new BarData(dataSet);

        binding.barChart.setData(data1);
        binding.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"Expenses", "Savings" , "Income"}));
        binding.barChart.getXAxis().setGranularity(1f);
        binding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        binding.barChart.invalidate();
    }

    private void showLineChart(int[] data) {
        binding.pieChart.setVisibility(View.GONE);
        binding.barChart.setVisibility(View.GONE);
        binding.lineChart.setVisibility(View.VISIBLE);
        binding.pieChart.getDescription().setEnabled(false);



        int expense = data[0];
        int balance = data[1];
        int income = data[2];

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, expense)); // Expenses
        entries.add(new Entry(1, balance)); // saving
        entries.add(new Entry(2, income)); // income

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        LineData data1 = new LineData(dataSet);

        binding.lineChart.setData(data1);
        binding.lineChart.invalidate();
    }

    private void setupToggleButtons() {
        binding.chartToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;

            loadData(data -> {
                if (checkedId == R.id.pieChartButton) {
                    showPieChart(data);
                } else if (checkedId == R.id.barChartButton) {
                    showBarChart(data);
                } else if (checkedId == R.id.lineChartButton) {
                    showLineChart(data);
                }
        });
    });

}

}

