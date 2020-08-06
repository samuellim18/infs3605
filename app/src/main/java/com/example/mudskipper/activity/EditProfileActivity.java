package com.example.mudskipper.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mudskipper.R;
import com.example.mudskipper.fragment.AbtAndProjProfileFragments;
import com.example.mudskipper.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Sets;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    int cityPosition , statePosition;
    String oldName, oldPhone, oldDescription,oldSkills,oldState,oldCity;
    private Uri filePath;
    String newName, newPhone, newDescription,stateS,cityS;
    CircleImageView edit_profile_pic;
    String newSkills = "";
    TextView emailTV;
    Button saveData;
    EditText epName, epPhone, epDescription;
    String TAG = "edit prof";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String email = currentUser.getEmail();
    TextView mItemSelected;
    String[] skillItem;
    boolean[] selectedSkills;
    ArrayList<String> mSkills = new ArrayList<>();
    private MultiSpinnerSearch spinnerState, spinnerCity;
    private  List<String> nswCities;
    private  List<String> ausStates;
    private  List<String> vicCities;
     String mCurrentPhotoPath ="" ;
     List<KeyPairBoolData> listArrayStates = new ArrayList<>();
     List<KeyPairBoolData> listArraySCities = new ArrayList<>();
     List<String> skillList;
     List<KeyPairBoolData> listArraySkills = new ArrayList<>();

    MultiSpinnerSearch skillMultiSpinner;
    private final int PICK_IMAGE_REQUEST = 71;
    Bitmap bitmapPic;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        initializeUI();
        getData();
    }

    //Method to put values in string.xml into a list and link the xml elements with this java class
    public void initializeUI(){
        nswCities = Arrays.asList(getResources().getStringArray(R.array.nsw_cities));
        ausStates = Arrays.asList(getResources().getStringArray(R.array.aus_states));
        vicCities = Arrays.asList(getResources().getStringArray(R.array.vic_cities));
        epName = findViewById(R.id.editP_name);
        epDescription = findViewById(R.id.editP_description);
        epPhone = findViewById(R.id.editP_mobile);
        emailTV  =findViewById(R.id.editP_tv_email);
        spinnerCity = findViewById(R.id.spinnerCities_ep);
        spinnerState = findViewById(R.id.spinnerStates_ep);
        saveData = findViewById(R.id.save_edit_profile);
        edit_profile_pic = findViewById(R.id.edit_profile_photo);
        edit_profile_pic.setImageResource(R.drawable.ic_baseline_person_24);

        //Allows the user to select a profile photo
        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //Button to save user data
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(newSkills);
                updateUserDetails();
                finish();
            }
        });


    }

    //method launched when user clicks on profile image, launches implicit intent to open a file picker
    //for user to select an image
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //After chooseImage() is completed, check if there is any image selected and set into the CircleImageView
    //profile photo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Glide
                        .with(this)
                        .load(filePath)
                        .apply(new RequestOptions().override(600, 200))
                        .into(edit_profile_pic);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //Uploads the image to the cloud storage in firebase if a picture is selected by the user
    //Referenced from: https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference imgRef = storageRef.child("images/"+ mAuth.getUid());
            imgRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    //ON initialization, call this method to get data from database and set into the editText and spinners
    public void getData(){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Gets data from the document with the relevant field names
                        oldState = document.getString("state");
                        oldCity = document.getString("city");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        oldName=document.getString("name");
                        epName.setText(oldName);
                        emailTV.setText(email);
                        oldPhone=document.getString("mobile_phone");
                        oldDescription = document.getString("description");
                        epPhone.setText(oldPhone);
                        epDescription.setText(oldDescription);

                        //Sets the limit of the spinner to 1, only one state and city can be selected each
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

                        //Sets the states as from the array into the booleanPair, as a default all is false
                        //So none is selected
                        for (int i = 0; i < ausStates.size(); i++) {
                            KeyPairBoolData h = new KeyPairBoolData();
                            h.setId(i + 1);
                            h.setName(ausStates.get(i));
                            h.setSelected(false);
                            listArrayStates.add(h);
                        }
                        //Gets the position old city and state from the database values
                        statePosition = ausStates.indexOf(oldState);
                        cityPosition = nswCities.indexOf(oldCity);

                        //Sets the BooleanPair list into the spinner and statePosition as the default selected item
                        spinnerState.setItems(listArrayStates, statePosition,new SpinnerListener() {

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


                                                spinnerCity.setItems(listArraySCities, cityPosition, new SpinnerListener() {

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
                                                //Sets the cities list based on the selection of the state in the previous spinner
                                            case "VIC":
                                                for (int vic = 0; vic < vicCities.size(); vic++) {
                                                    KeyPairBoolData h = new KeyPairBoolData();
                                                    h.setId(vic + 1);
                                                    h.setName(vicCities.get(vic));
                                                    h.setSelected(false);
                                                    listArraySCities.add(h);
                                                    spinnerCity.setItems(listArraySCities, cityPosition, new SpinnerListener() {
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

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
    }


    //This method is called when the saveButton is clicked, updates the user details in the database
    private void updateUserDetails() {
        DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        newName = epName.getText().toString();
        newDescription  =epDescription.getText().toString();
        newPhone = epPhone.getText().toString();
        uploadImage();
        for(int i=0;i<listArraySkills.size();i++){
            if(listArraySkills.get(i).isSelected()) {
                newSkills += listArraySkills.get(i).getName();
                if (i < listArraySkills.size() - 2) {
                newSkills += ",";
            }else {
                    System.out.println(newSkills);
                }
        }
        }
        System.out.println(email+ " "+newSkills + newName);
        db.collection("users").document(email)
                .update("name",newName,
                        "description", newDescription,
                        "mobile_phone", newPhone,
                        "skills",newSkills
                );

        //Updates the user details in realtime database to update chat details
        Map<String, Object> updateUser = new HashMap<>();
        updateUser.put("username", newName);
        updateUser.put("uid", currentUser.getUid());
        updateUser.put("search", newName.toLowerCase());
        databaseReference.setValue(updateUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }




}
