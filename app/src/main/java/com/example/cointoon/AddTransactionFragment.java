package com.example.cointoon;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.cointoon.databinding.FragmentAddTransactionBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddTransactionFragment extends BottomSheetDialogFragment {

    FragmentAddTransactionBinding binding;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ek button to selected rahe koi bi button unselected nhi ho sakta (user ko data dena padega)
        noUnselectExpenseIncome();
        noUnselectCategory();
        noUnselectPayment();
        selectDate();


        //hiding keyboard & clear focus
        clearFocusAndHideKeyboard();


        // done button
        doneButton();



    }


    private  void doneButton(){

        // working of done button when clicked by the user on dashboard screen
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = binding.etAmount.getText().toString().trim();
                String date = binding.etDate.getText().toString().trim();


                if(amount.isEmpty() || date.isEmpty()){
                    Toast.makeText(getContext() , "Enter the necessary info" , Toast.LENGTH_SHORT).show();
                }

                else {
                    saveData();
                }
            }
        });

    }
    // saving data
    private  void saveData(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("transaction");

        String key = ref.push().getKey();

        // data entered by the user
        String name = checkChipName();
        String date = binding.etDate.getText().toString().trim();
        String amount = binding.etAmount.getText().toString().trim();
        int iconResId = R.drawable.food;   // default icon to be stored
        String type = checkChipType();   // income or expense

        TransactionItem transactionItem = new TransactionItem(name, date , amount , iconResId , type);
        ref.child(key).setValue(transactionItem);

        dismiss(); // hides the bottom sheet

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddTransactionBinding.inflate(inflater);
        return binding.getRoot();
    }

    public  String  checkChipName(){
        int selectedChipId = binding.categoryToggleGroup.getCheckedButtonId();

        String selectedChipText = "";
        if(selectedChipId!=-1){
            MaterialButton button = binding.categoryToggleGroup.findViewById(selectedChipId);
            selectedChipText = button.getText().toString().trim();
        }

        return  selectedChipText;
    }


    public  String checkChipType(){
        // finds whether the transaction added is income or expense
        String type = "";

        int selectedChipId = binding.toggleTransactionType.getCheckedButtonId();

        if(selectedChipId!=-1){
            MaterialButton button = binding.toggleTransactionType.findViewById(selectedChipId);
            type = button.getText().toString().trim();
        }

        return type;
    }
    public void noUnselectExpenseIncome(){

        binding.toggleTransactionType.addOnButtonCheckedListener(
                new MaterialButtonToggleGroup.OnButtonCheckedListener() {
                    @Override
                    public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                        if (!isChecked && group.getCheckedButtonId() == View.NO_ID) {
                            group.check(checkedId); // Recheck to prevent all unselected
                        }
                    }
                });
    }

    public void noUnselectCategory(){

        binding.categoryToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(!isChecked && group.getCheckedButtonId() == View.NO_ID){
                    group.check(checkedId);
                }
            }
        });

    }


    public  void noUnselectPayment(){

        binding.paymentToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(!isChecked && group.getCheckedButtonId() == View.NO_ID){
                    group.check(checkedId);
                }
            }
        });

    }


    public  void selectDate(){
        binding.etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        binding.etDate.setText(date);

                    }
                },year , month ,day);

                datePickerDialog.show();

            }
        });
    }

    public  void clearFocusAndHideKeyboard(){

        View view = getActivity().getCurrentFocus();

        if(view!=null){
            view.clearFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken() , 0);
        }

    }
}


