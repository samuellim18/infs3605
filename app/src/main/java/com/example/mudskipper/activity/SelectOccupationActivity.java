package com.example.mudskipper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.example.mudskipper.R;
import com.example.mudskipper.adapter.ExpandableListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectOccupationActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {
    private FirebaseAuth mAuth =  FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MultiSpinnerSearch directingSpinner, actingSpinner, freelanceSpinner, designSpinner, photographerSpinner;
    private String TAG = "Select Occupation";
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    List<String> occAL = new ArrayList<>();
    HashMap<String, List<String>> listDataChild;
    Button button ;
    List<String> occupationsArrayList = new ArrayList<>();
    List<String> directingArrayList = new ArrayList<>();
    List<String> actingArrayList = new ArrayList<>();
    List<String> photoArrayList = new ArrayList<>();
    List<String> designArrayList = new ArrayList<>();
    List<String> freelanceArrayList = new ArrayList<>();
    ArrayList<String> directingSkillArray = new ArrayList<>();
    ArrayList<String> actingSkillArray = new ArrayList<>();
    ArrayList<String> photoSkillArray = new ArrayList<>();
    ArrayList<String> designSkillArray = new ArrayList<>();
    ArrayList<String> freelanceSkillArray = new ArrayList<>();
    List<KeyPairBoolData> directingBooleanList = new ArrayList<>();
    List<KeyPairBoolData> actingBooleanList = new ArrayList<>();
    List<KeyPairBoolData> photographyBooleanList = new ArrayList<>();
    List<KeyPairBoolData> designBooleanList = new ArrayList<>();
    List<KeyPairBoolData> freelanceBooleanList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_occupation_page);
        setLayout();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regOccupation();
                startActivity(new Intent(SelectOccupationActivity.this, SelectInterestActivity.class));
            }
        });

        // get the listview
        //expListView = (ExpandableListView) findViewById(R.id.occExp);

        // preparing list data
        //prepareListData();

       // listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapte
        /*expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Doing nothing
                return true;
            }
        });
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);
        expListView.expandGroup(1);
        expListView.setOnChildClickListener(this::onChildClick);*/
    }

    /*
     * Preparing the list data
     */
    private void setLayout(){
        occupationsArrayList = Arrays.asList(getResources().getStringArray(R.array.occupations));
        directingArrayList = Arrays.asList(getResources().getStringArray(R.array.director_skills));
        actingArrayList = Arrays.asList(getResources().getStringArray(R.array.acting_skills));
        photoArrayList = Arrays.asList(getResources().getStringArray(R.array.photography_skills));
        designArrayList = Arrays.asList(getResources().getStringArray(R.array.design_skills));
        freelanceArrayList = Arrays.asList(getResources().getStringArray(R.array.freelance_skills));
        button = findViewById(R.id.buttonOcc);
        directingSpinner = findViewById(R.id.directorSpinnerMulti);
        actingSpinner = findViewById(R.id.actorSkillSpinnerMulti);
        freelanceSpinner = findViewById(R.id.freelanceSpinnerMulti);
        designSpinner = findViewById(R.id.designerSpinnerMulti);
        photographerSpinner = findViewById(R.id.photographerSpinnerMulti);
        setSpinner(directingSpinner,directingArrayList, directingBooleanList);
        setSpinner(actingSpinner, actingArrayList, actingBooleanList);
        setSpinner(freelanceSpinner, freelanceArrayList, freelanceBooleanList);
        setSpinner(designSpinner,designArrayList, designBooleanList);
        setSpinner(photographerSpinner,photoArrayList, photographyBooleanList);


    }
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        for (int a =0;a<occupationsArrayList.size();a++){
            listDataHeader.add(occupationsArrayList.get(a));
        }


        listDataChild.put(listDataHeader.get(0), directingArrayList); // Header, Child data
        listDataChild.put(listDataHeader.get(1), actingArrayList);
        listDataChild.put(listDataHeader.get(2), photoArrayList);
        listDataChild.put(listDataHeader.get(3), designArrayList);
        listDataChild.put(listDataHeader.get(4), freelanceArrayList);
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        System.out.println("child " + childPosition+ "clicked");
        return false;
    }

    public void regOccupation(){
        String email = mAuth.getCurrentUser().getEmail();
        System.out.println(email);
        Map<String,Object> newOccupation = new HashMap<>();
        getBooleanData(directingBooleanList,directingSkillArray);
        getBooleanData(actingBooleanList,actingSkillArray);
        getBooleanData(freelanceBooleanList,freelanceSkillArray);
        getBooleanData(designBooleanList,designSkillArray);
        getBooleanData(photographyBooleanList,photoSkillArray);
        newOccupation.put("directing_skills", directingSkillArray);
        newOccupation.put("acting_skills", actingSkillArray);
        newOccupation.put("freelance_skills",freelanceSkillArray);
        newOccupation.put("design_skills",designSkillArray);
        newOccupation.put("photography_skills",photoSkillArray);
        db.collection("users").document(email)
                .update(newOccupation)
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

    private void setSpinner(MultiSpinnerSearch spinner, List<String> skillList, List<KeyPairBoolData> booleanSkillList){
        for (int i = 0; i < skillList.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(skillList.get(i));
            h.setSelected(false);
            booleanSkillList.add(h);
        }
        spinner.setItems(booleanSkillList, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {
                for (int i = 0; i < items.size(); i++) {
                    Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                }
            }
        });
    }

    private void getBooleanData(List<KeyPairBoolData> booleanSkillArray, ArrayList<String> skillArray){
        for (int i=0;i<booleanSkillArray.size();i++){
            if (booleanSkillArray.get(i).isSelected()==true){
                skillArray.add(booleanSkillArray.get(i).getName());
            }
        }
    }
}