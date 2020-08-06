package com.example.mudskipper.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.example.mudskipper.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectInterestActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Button full_time, part_time, final_reg;
    String TAG = "Select Interest";
    String newInterest = "";
    ArrayList<String> mInterest = new ArrayList<>();
    boolean ft = false, pt = false, mov = false, photo_b = false;
    MultiSpinnerSearch interestSpinner;
    List<String> interestList;
    List<KeyPairBoolData> interestBolList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_interest);
        full_time = findViewById(R.id.full_time);
        part_time = findViewById(R.id.part_time);
        interestSpinner= findViewById(R.id.interestSpinnerMulti);
        final_reg = findViewById(R.id.btn_final_reg);
        interestList  = Arrays.asList(getResources().getStringArray(R.array.interests));
        for (int i = 0; i < interestList.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(interestList.get(i));
            h.setSelected(false);
            interestBolList.add(h);
        }

        interestSpinner.setEmptyTitle("Empty Title");
        interestSpinner.setColorSeparation(true);
        interestSpinner.setItems(interestBolList, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    if (items.get(i).isSelected()==true){
                    mInterest.add(items.get(i).getName());}
                }
            }
        });
        setOnClick(full_time);
        setOnClick(part_time);
        final_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                for(int i=0;i<mInterest.size();i++){
//                        newInterest += mInterest.get(i);
//                        if (i < mInterest.size() - 1) {
//                            newInterest += ",";
//                        }
//                    }
                addRegDetails();
                System.out.println(newInterest);
                startActivity(new Intent(SelectInterestActivity.this, MainActivity.class));
                }
        });
    }

    private void setOnClick(final Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButton(btn);
            }
        });
    }

    private void handleButton(Button btn) {
        String buttonName = btn.getText().toString();
        switch (buttonName) {
            case "Full Time":
                if (ft == false) {
                    if (pt==false){
                    btn.setBackgroundResource(R.drawable.button_clicked);
                    ft = true;
                    break;}
                    else{
                    //TODO SET TOAST MESSAGE SAYING ONLY ONE TYPE CAN BE SELECTED
                        Toast.makeText(SelectInterestActivity.this, "Please select one only.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                 else {
                    ft = false;
                    btn.setBackgroundResource(R.drawable.orange_btn);
                    break;
                }
            case "Part Time":
                if (pt == false) {
                    if (ft==false){
                        btn.setBackgroundResource(R.drawable.button_clicked);
                        ft = true;
                        break;}
                    else{
                        //TODO SET TOAST MESSAGE SAYING ONLY ONE TYPE CAN BE SELECTED
                        Toast.makeText(SelectInterestActivity.this, "Please select one only.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                } else {
                    pt = false;
                    btn.setBackgroundResource(R.drawable.orange_btn);
                    break;
                }
        }
    }

    public void addRegDetails(){
        String work = "";
        String email = mAuth.getCurrentUser().getEmail();
        if(pt ==true){
            work = "Part Time";
        }
        else {
            work = "Full Time";
        }
        Map<String,Object> newInterest = new HashMap<>();
        newInterest.put("work_commitments", work );
        newInterest.put("interests", mInterest);
        db.collection("users").document(email)
                .update(newInterest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
