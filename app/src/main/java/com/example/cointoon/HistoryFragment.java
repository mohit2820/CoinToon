package com.example.cointoon;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.cointoon.databinding.FragmentHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class HistoryFragment extends Fragment {

    FragmentHistoryBinding binding;
    DatabaseReference userRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadData();
        addTransaction();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater);
        return binding.getRoot();
    }

//    public  void testing (){
//        List<HistoryItem> dummyList = new ArrayList<>();
//
//        // Dummy Data — 27 May
//        dummyList.add(new DateHeaderItem("27 May 2025"));
//        dummyList.add(new TransactionItem("Burger", "27 May 2025", "₹150", R.drawable.food));
//        dummyList.add(new TransactionItem("Bus Ticket", "27 May 2025", "₹50", R.drawable.food));
//
//        // Dummy Data — 26 May
//        dummyList.add(new DateHeaderItem("26 May 2025"));
//        dummyList.add(new TransactionItem("Spotify", "26 May 2025", "₹119", R.drawable.food));
//
//        RecyclerView recyclerView = binding.rvTransactionHistory;
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        HistoryAdapter adapter = new HistoryAdapter(getContext(), dummyList);
//        recyclerView.setAdapter(adapter);
//
//    }

    public  void loadData(){


        userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("transaction");


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                Map<Date, List<TransactionItem>> groupedByDate = new TreeMap<>(Collections.reverseOrder());

                for (DataSnapshot ds : snapshot.getChildren()) {
                    TransactionItem t = ds.getValue(TransactionItem.class);
                    if (t != null) {
                        try {
                            Date parsedDate = sdf.parse(t.getDate());
                            if (!groupedByDate.containsKey(parsedDate)) {
                                groupedByDate.put(parsedDate, new ArrayList<>());
                            }
                            groupedByDate.get(parsedDate).add(t);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                List<HistoryItem> historyItems = new ArrayList<>();
                for (Map.Entry<Date, List<TransactionItem>> entry : groupedByDate.entrySet()) {
                    String formattedDate = sdf.format(entry.getKey());  // Convert back to "d/M/yyyy"
                    historyItems.add(new DateHeaderItem(formattedDate));
                    for (TransactionItem t : entry.getValue()) {
                        historyItems.add(new TransactionItem(
                                t.getName(),
                                t.getDate(),
                                t.getAmount(),
                                t.getIconResId(),
                                t.getTypes()
                        ));
                    }
                }


                HistoryAdapter adapter = new HistoryAdapter(getContext(), historyItems);
                binding.rvTransactionHistory.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rvTransactionHistory.setAdapter(adapter);

                // if there is no data to load
                if (historyItems.isEmpty()) {
                    binding.rvTransactionHistory.setVisibility(View.GONE);
                    binding.emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.rvTransactionHistory.setVisibility(View.VISIBLE);
                    binding.emptyStateLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void addTransaction(){

        binding.openBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTransactionFragment bottomSheet = new AddTransactionFragment();
                bottomSheet.show(getParentFragmentManager() , bottomSheet.getTag());
            }
        });
    }
}