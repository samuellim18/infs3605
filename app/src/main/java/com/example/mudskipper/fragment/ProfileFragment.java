package com.example.mudskipper.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.R;
import com.example.mudskipper.UniversalImageLoader;
import com.example.mudskipper.activity.EditProfileActivity;
import com.example.mudskipper.activity.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.model.DocumentCollections;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment
{

    ArrayList<Integer> img_arraylist =new ArrayList<>();
    private Context mContext_f;
    RecyclerView myRecyclerView;
    Button follow;
    HomeRecyclerViewAdapter adapter;
    CircleImageView profile_photo;
    ImageView profile_menu;
    String TAG = "Profile ";
    String name,mobile_phone, skills,description;
    TextView profileName, profileDescription, profileEmail,profilePhone, profileSkills ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://test-a2467.appspot.com/");
    StorageReference storageRef = storage.getReference();
    String email = currentUser.getEmail();

    private static int RESULT_LOAD_IMAGE = 1;
    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        if(bundle!=null){
            byte[] byteArray = getArguments().getByteArray("image");
            // bitmap = intent.getParcelableExtra("bitmap");
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            profile_photo.setImageBitmap(bitmap);
        }
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        myRecyclerView = view.findViewById(R.id.recyclerview_project);
        profile_photo = view.findViewById(R.id.profile_photo);
        profilePhone = view.findViewById(R.id.profilePhone);
        profileName = view.findViewById(R.id.profile_name);
        profileDescription= view.findViewById(R.id.profileDescription);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileSkills = view.findViewById(R.id.profileSkills);
        profile_menu = view.findViewById(R.id.profile_menu1);
        follow= view.findViewById(R.id.follow_btn);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        profile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });

        getData();
        img_arraylist.clear();
        for (int i = 0; i < 35; i++)
        {
            img_arraylist.add(R.drawable.dummy_trending);
            img_arraylist.add(R.drawable.dummy_trending);
        }

        adapter = new HomeRecyclerViewAdapter(mContext_f);
        int numberOfColumns = 2;
        myRecyclerView.setLayoutManager(new GridLayoutManager(mContext_f, numberOfColumns));
        myRecyclerView.setAdapter(adapter);
        InitImgLoader();
        setProfilePic();
        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        System.out.println(email);
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name=document.getString("name");
                        mobile_phone=document.getString("mobile_phone");
                        skills = document.getString("skills");
                        description = document.getString("description");
                        profileName.setText(name);
                        profilePhone.setText("Phone no: " + mobile_phone);
                        profileSkills.setText("Skills: " + skills);
                        profileDescription.setText("Description: " + description);


                        System.out.println(document);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile_menu, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getContext(), "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.edit_profile:
                // do your code
                return true;
            case R.id.logout:
                // do your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getData(){
//        StorageReference islandRef = storageRef.child("images/s@yahoo.com");
//        final long FIVE_MEGABYTE = 5120 * 5120;
//        islandRef.getBytes(FIVE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Data for "images/island.jpg" is returns, use this as needed
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                profile_photo.setImageBitmap(bitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });
        // Create a reference to the cities collection
        CollectionReference usersRef = db.collection("users");



        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name=document.getString("name");
                        profileName.setText(name);
                        profileEmail.setText(email);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
    }

    /* @Override
     public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
     {
         super.onActivityResult(requestCode, resultCode, data);
         if (data != null) {
             Uri selectedImage = data.getData();
             String[] filePathColumn = { MediaStore.Images.Media.DATA };
             Cursor cursor = mContext_f.getContentResolver().query(selectedImage,
                     filePathColumn, null, null, null);

             cursor.moveToFirst();
             int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
             String picturePath = cursor.getString(columnIndex);
             profile_photo.setImageBitmap(BitmapFactory.decodeFile(picturePath));
             cursor.close();
         } else {
             Toast.makeText(getActivity(), "Try Again!!", Toast.LENGTH_SHORT)
                     .show();
         }
     }
 */

    private void InitImgLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    private void setProfilePic(){
        Log.d("prof","setr");
        String imgUrl = "www.skillvalue.com/wp-content/uploads/sites/7/2019/06/mobile-developer-android-hybrid-freelance-project.jpg";
        UniversalImageLoader.setImage(imgUrl, profile_photo,"https://",null);
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext_f= context;
        super.onAttach(context);
    }
    public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>
    {

        Context context;


        public HomeRecyclerViewAdapter(Context context)
        {
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_row,parent,false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
        {
            //   Glide.with(context).load(userInformation.getImage()).into(holder.menuImage);

            holder.menuImage.setImageResource(img_arraylist.get(position));
            holder.menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                }
            });
        }

        @Override
        public int getItemCount()
        {
            return img_arraylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {

            public ImageView menuImage;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);
                context = itemView.getContext();

                menuImage = itemView.findViewById(R.id.menu_image);
            }
        }
    }
}