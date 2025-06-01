package com.example.cointoon;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cointoon.databinding.FragmentDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    FirebaseAuth auth;
    DatabaseReference userDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        floatingButton();

        // firebase
        auth = FirebaseAuth.getInstance();

        setName();
        loadDashboardData();
        viewCharts();

    }

    public void setName() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDb = FirebaseDatabase.getInstance().getReference("Users").child(uid);

            userDb.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    binding.userNameText.setText("Hi, " + name + "!");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to load name", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void floatingButton() {
        // using view binding & lambda function
        binding.floatingButton.setOnClickListener(click -> {

            new AddTransactionFragment().show(getChildFragmentManager(), null);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater);
        return binding.getRoot();
    }

    private void loadDashboardData() {

        FirebaseUser currentUser = auth.getCurrentUser();

        String uid = currentUser.getUid();
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

                binding.totalIncome.setText(String.valueOf(totalIncome));
                binding.totalExpense.setText(String.valueOf(totalExpense));
                binding.balance.setText(String.valueOf(balance));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void viewCharts(){

        Fragment chartFragment = new ChartFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        binding.viewFullCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) requireActivity();
                mainActivity.selectTab(2);
            }
        });
    }

}