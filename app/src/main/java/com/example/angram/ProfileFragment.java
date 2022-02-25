package com.example.angram;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {
    private de.hdodenhof.circleimageview.CircleImageView avatartv;
    private TextView nam, email;
    private FloatingActionButton fab;
    private ProgressDialog pd;
    private Button signout;
    private GridView gridView;
    private RelativeLayout layout;
    private SharedPreferences sharedPreferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        sharedPreferences = getContext().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
        avatartv = view.findViewById(R.id.avatartv);
        nam = view.findViewById(R.id.nametv);
        email = view.findViewById(R.id.emailtv);
        fab = view.findViewById(R.id.fab);
        pd = new ProgressDialog(getActivity());
        pd.setCanceledOnTouchOutside(false);
        gridView = view.findViewById(R.id.imagesGridView);
        loadMyPosts();
        layout = view.findViewById(R.id.relativeLayout);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Log.d("Hello", "Workkk");
            }
        });
        Query query = FirebaseHandler.firebaseDatabase.getReference("Users").orderByChild("email").equalTo(FirebaseHandler.firebaseAuth.getCurrentUser().getEmail());
        signout = view.findViewById(R.id.signoutBtn);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String name = "" + dataSnapshot1.child("name").getValue();
                    String emaill = "" + dataSnapshot1.child("email").getValue();
                    String profileImage = sharedPreferences.getString("ProfileImage", "");
                    if(profileImage.equals("")) {
                        String newProfileImage = "" + dataSnapshot1.child("profile_image").getValue();
                        if(!newProfileImage.equals(profileImage)) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ProfileImage", profileImage);
                            editor.commit();
                        }
                    } else {
                        try {
                            Glide.with(getActivity()).load(profileImage).into(avatartv);
                        } catch (Exception e) {

                        }
                    }
                    nam.setText(name);
                    email.setText(emaill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("ProfileImage");
                editor.commit();
                FirebaseHandler.signout(getActivity());
            }
        });
        return view;
    }

    private void loadMyPosts() {
        List<String> images = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("Posts/" + FirebaseHandler.firebaseAuth.getCurrentUser().getUid() + "/");
        storageReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        final int numOfFiles = listResult.getItems().size();
                        for (StorageReference item : listResult.getItems()) {
                            Task<Uri> uriTask = item.getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            final Uri downloadUri = uriTask.getResult();
                            images.add(downloadUri.toString());
                        }
                        GridAdapter gridAdapter = new GridAdapter(getActivity(), images, images.size());
                        gridView.setAdapter(gridAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}