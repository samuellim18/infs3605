package com.example.login_test;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText name;
    private EditText email;
    private EditText pass;
    private EditText pass_C;
    private String nameS , emailS , passS ,pass_CS , userTypeS;
    private RadioGroup userType;
    private RadioButton radioButton;
    private String TAG;
    private Button regButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_page);
        mAuth = FirebaseAuth.getInstance();
        initializeUI();



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
    name = findViewById(R.id.et_name);
    email =findViewById(R.id.et_email);
    pass =findViewById(R.id.et_password_r);
    pass_C =findViewById(R.id.et_repassword);
    userType  =findViewById(R.id.radioG_reg);
    regButton = findViewById(R.id.btn_register);
}

    private void regNewUser(){
        int selectedId = userType.getCheckedRadioButtonId();
        nameS = name.getText().toString();
        emailS = email.getText().toString();
        passS = pass.getText().toString();
        pass_CS = pass_C.getText().toString();
        // find the radiobutton by returned id
        radioButton = (RadioButton) findViewById(selectedId);
        userTypeS = (String) radioButton.getText();

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
