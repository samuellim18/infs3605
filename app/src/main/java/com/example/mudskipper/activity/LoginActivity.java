package com.example.mudskipper.activity;

import android.app.ProgressDialog;
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

import com.example.mudskipper.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private Button signup;
    private String TAG = "Login Activity";
    private FirebaseAuth mAuth;
    private EditText email,pass;
    private String emailS , passS;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    GoogleSignInClient googleSignInClient;
    private ArrayList<String> userList;
    private static final int RC_SIGN_IN = 1001;
    String googleEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        initUI();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Checking User...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void initUI() {
        login = findViewById(R.id.btn_login);
        signup = findViewById(R.id.btn_signup);
        email = findViewById(R.id.et_email);
        pass = findViewById(R.id.et_password_l);
        userList = new ArrayList<>();

        SignInButton googleSignIn = findViewById(R.id.google_sign_in_button);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIntoGoogle();
            }
        });

        //Configure Google client
        configureGoogleClient();
    }


    //Intent to start google User sign in
    //Process referenced from : https://stackoverflow.com/questions/38868873/google-sign-in-with-firebase-android
    /*
    This allowed me to implement google sign in, which aligns with Mudskipper's wants to integrate existing technologies into the app
    This could be expanded in the future to allow facebook or other social media supported by Firebase
     */
    public void signIntoGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void configureGoogleClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    //Log in process, which first checks if the fields are null and notifies the user
    private void loginUser() {
        emailS = email.getText().toString();
        passS = pass.getText().toString();
        progressDialog.show();

        if (TextUtils.isEmpty(emailS)) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(passS)) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(emailS, passS)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.show();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login failed! Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    //Received the outcome of the google sign in, if successful, run the following code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                 googleEmail = account.getEmail();
                Log.e(TAG,googleEmail);

                showToastMessage("Google Sign in Succeeded");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                showToastMessage("Google Sign in Failed " + e);
            }
        }
    }

    //Authenticates user with firebase after google sign in is successful
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "Signin Success");
                            //Gets lists of current users in the database
                            getUsersList();
                            FirebaseUser user = mAuth.getCurrentUser();
                            boolean isRegistered = false;

                            //Checks if the current google user has registered their details from the database
                            for (String email:userList){
                                Log.e(TAG, "in email loop" + email + "  " + user.getEmail());
                                if (email.equals(user.getEmail())){
                                    Log.e(TAG, "equal");
                                    isRegistered =true;
                                }
                            }
                            if (isRegistered==true){
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }else{
                                //Brings user to reg page if their data does not exist in the database user list
                                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                                intent.putExtra("google_userID",mAuth.getUid());
                                System.out.println(mAuth.getUid());
                                startActivity(intent);
                            }
                            Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());
                            showToastMessage("Firebase Authentication Succeeded ");

                        } else {
                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showToastMessage("Firebase Authentication failed:" + task.getException());
                        }
                    }
                });
    }

    private void showToastMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void getUsersList() {
        Log.e(TAG, "launch getUsersList");
        progressDialog.show();
        // Get a reference to the pedestal collection
        final CollectionReference firestoreUsers = firestore.collection("users");
        //
        //        // get all the names of the users in firestore
        firestoreUsers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e(TAG, "launch getUsersList - inCOmp");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    //  names.add(document.getString("name"));
                    Log.e(TAG, "launch getUsersList - loop");
                    userList.add(document.get("email").toString());
                }
                for (String a : userList){
                    System.out.println(a);
                }
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "success!", Toast.LENGTH_LONG).show();
            }//end of onSuccess
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "error!", Toast.LENGTH_LONG).show();
                Log.e(TAG, "could not get the query");
            }
        });
    };

}



