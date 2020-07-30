package com.example.mudskipper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.example.mudskipper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText name, mobile;
    private EditText email;
    private EditText pass;
    private EditText pass_C;
    private String nameS , emailS , passS  , cityS,countryS,stateS, mobilePh;
    private String TAG;
    private Button regButton;
    private FirebaseFirestore db;
    private MultiSpinnerSearch spinnerState, spinnerCity;
    private  List<String> nswCities;
    private  List<String> ausStates;
    private  List<String> vicCities;
    final List<KeyPairBoolData> listArrayStates = new ArrayList<>();
    final List<KeyPairBoolData> listArraySCities = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_page);
        mAuth = FirebaseAuth.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        initializeUI();


        regButton = findViewById(R.id.btn_register);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regNewUser();
            }
        });
    }

    //    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
    private void initializeUI() {
        nswCities = Arrays.asList(getResources().getStringArray(R.array.nsw_cities));
        ausStates = Arrays.asList(getResources().getStringArray(R.array.aus_states));
        vicCities = Arrays.asList(getResources().getStringArray(R.array.vic_cities));
        name = findViewById(R.id.et_name);
        email =findViewById(R.id.et_email);
        pass =findViewById(R.id.et_password_r);
        pass_C =findViewById(R.id.et_repassword);
        regButton = findViewById(R.id.btn_register);
        spinnerState =findViewById(R.id.spinnerStates);
        spinnerCity = findViewById(R.id.spinnerCities);
        spinnerState.setHintText("State");
        spinnerState.setLimit(1, new MultiSpinnerSearch.LimitExceedListener() {
            @Override
            public void onLimitListener(KeyPairBoolData data) {
                Toast.makeText(getApplicationContext(),
                        "Limit exceed ", Toast.LENGTH_LONG).show();
            }
        });
        spinnerCity.setLimit(1, new MultiSpinnerSearch.LimitExceedListener() {
            @Override
            public void onLimitListener(KeyPairBoolData data) {
                Toast.makeText(getApplicationContext(),
                        "Limit exceed ", Toast.LENGTH_LONG).show();
            }
        });
        for (int i = 0; i < ausStates.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(ausStates.get(i));
            h.setSelected(false);
            listArrayStates.add(h);
        }

        spinnerState.setItems(listArrayStates, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        switch(items.get(i).getName()){
                            case "NSW":
                                stateS = items.get(i).getName();
                                for (int nsw = 0; nsw < nswCities.size(); nsw++) {
                                    KeyPairBoolData h = new KeyPairBoolData();
                                    h.setId(nsw + 1);
                                    h.setName(nswCities.get(nsw));
                                    h.setSelected(false);
                                    listArraySCities.add(h);
                                }
                                spinnerCity.setItems(listArraySCities, -1, new SpinnerListener() {

                                    @Override
                                    public void onItemsSelected(List<KeyPairBoolData> items) {
                                        for (int i = 0; i < items.size(); i++) {
                                            if (items.get(i).isSelected()) {
                                                cityS = items.get(i).getName();
                                                Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                            }
                                        }
                                    }
                                });
                                break;
                            case "VIC":
                                for (int vic = 0; vic < vicCities.size(); vic++) {
                                    KeyPairBoolData h = new KeyPairBoolData();
                                    h.setId(vic + 1);
                                    h.setName(vicCities.get(vic));
                                    h.setSelected(false);
                                    listArraySCities.add(h);
                                    spinnerCity.setItems(listArraySCities, -1, new SpinnerListener() {
                                        @Override
                                        public void onItemsSelected(List<KeyPairBoolData> items) {

                                            for (int i = 0; i < items.size(); i++) {
                                                if (items.get(i).isSelected()) {
                                                    Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                                }
                                            }
                                        }
                                    });
                                }
                        }
                    }
                }
            }
        });


    }

    private void regNewUser(){
        nameS = name.getText().toString();
        emailS = email.getText().toString();
        passS = pass.getText().toString();


        if (TextUtils.isEmpty(emailS)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(passS)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(emailS, passS)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String,Object> newUser = new HashMap<>();
                            newUser.put("name",nameS);
                            newUser.put("email", emailS );
                            newUser.put("country","Australia");
                            newUser.put("state",stateS);
                            newUser.put("city",cityS);
                            if (mobile!= null){
                                mobilePh = mobile.getText().toString();
                                newUser.put("mobile_phone", mobilePh);
                            }
                            else{
                                newUser.put("mobile_phone", null);
                            }
                            newUser.put("description", null);
                            newUser.put("skills", null);
                            startActivity(new Intent(SignupActivity.this, SelectOccupationActivity.class));
                            db.collection("users")
                                    .document(emailS)
                                    .set(newUser)
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
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}