package com.example.login_test.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login_test.R;
import com.example.login_test.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
    HomeRecyclerViewAdapter adapter;
    CircleImageView profile_photo;
    String TAG = "Profile ";
    String name;
    TextView profileName;
    String userType;
    private static int RESULT_LOAD_IMAGE = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        myRecyclerView = view.findViewById(R.id.recyclerview_project);
        profile_photo = view.findViewById(R.id.profile_photo);
        profileName = view.findViewById(R.id.profile_name);
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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String emailS = currentUser.getEmail();
        System.out.println(emailS);
        DocumentReference docRef = db.collection("users").document(emailS);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name=document.getString("name");
                        profileName.setText(name);
                        userType = document.getString("userType");
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