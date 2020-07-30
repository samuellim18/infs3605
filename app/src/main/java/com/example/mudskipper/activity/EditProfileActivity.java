package com.example.mudskipper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;


import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.example.mudskipper.R;
import com.example.mudskipper.fragment.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void initializeUI(){
        nswCities = Arrays.asList(getResources().getStringArray(R.array.nsw_cities));
        ausStates = Arrays.asList(getResources().getStringArray(R.array.aus_states));
        vicCities = Arrays.asList(getResources().getStringArray(R.array.vic_cities));
        skillList= Arrays.asList(getResources().getStringArray(R.array.skillSpinnerItems));
        epName = findViewById(R.id.editP_name);
        epDescription = findViewById(R.id.editP_description);
        epPhone = findViewById(R.id.editP_mobile);
        emailTV  =findViewById(R.id.editP_tv_email);
        skillMultiSpinner = findViewById(R.id.skillSpinnerMulti);
        spinnerCity = findViewById(R.id.spinnerCities_ep);
        spinnerState = findViewById(R.id.spinnerStates_ep);
        saveData = findViewById(R.id.save_edit_profile);
        edit_profile_pic = findViewById(R.id.edit_profile_photo);
        edit_profile_pic.setImageResource(R.drawable.ic_baseline_person_24);
        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(newSkills);
                updateUserDetails();
                finish();
            }
        });


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                edit_profile_pic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference imgRef = storageRef.child("images/"+ email);
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
            try {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Intent intent = new Intent(EditProfileActivity.this, ProfileFragment.class);
                bitmapPic = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapPic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image",byteArray);
                //intent.putExtra("bitmap", bitmapPic);
                Bundle bundle  =new Bundle();
                bundle.putByteArray("image",byteArray);
                profileFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container,profileFragment).commit();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }



    }

    public void getData(){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        oldState = document.getString("state");
                        oldCity = document.getString("city");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        oldName=document.getString("name");
                        epName.setText(oldName);
                        emailTV.setText(email);
                        oldPhone=document.getString("mobile_phone");
                        oldSkills = document.getString("skills");
                        oldDescription = document.getString("description");

                        System.out.println("SKILLS" + oldSkills);
                        System.out.println("OLD " + oldCity + " " + oldState);
//                        spinnerCity.setHintText(cityS);
//                        spinnerState.setHintText(stateS);
                        epPhone.setText(oldPhone);
                        String[] oldSkillArray = oldSkills.split(",");
                        for (int i = 0; i < skillList.size(); i++) {
                            KeyPairBoolData h = new KeyPairBoolData();
                            h.setId(i + 1);
                            h.setName(skillList.get(i));
                            h.setSelected(false);
                            listArraySkills.add(h);
                        }

                        for(String skill :oldSkillArray){
                            for(int i = 0;  i<listArraySkills.size();i++){
                                if (listArraySkills.get(i).getName().equals(skill)){
                                    listArraySkills.get(i).setSelected(true);
                                }
                            }
                        }

                        skillMultiSpinner.setItems(listArraySkills,-1 , new SpinnerListener() {

                            @Override
                            public void onItemsSelected(List<KeyPairBoolData> items) {

                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).isSelected()) {
                                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                                        mSkills.add(items.get(i).getName());
                                    }
                                }
                            }
                        });

                        skillMultiSpinner.setHintText(oldSkills);
                        epDescription.setText(oldDescription);
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
                        statePosition = ausStates.indexOf(oldState);
                        cityPosition = nswCities.indexOf(oldCity);
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
                                                break;
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
    private View.OnClickListener awesomeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateUserDetails();
        }
    };

    private void updateUserDetails() {
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




    }

}
