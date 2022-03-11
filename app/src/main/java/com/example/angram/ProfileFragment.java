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
    private ProgressDialog pd;
    private Button signout;
    private RelativeLayout layout;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private List<Post> posts;
    private PostsAdapter adapterPosts;

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
        pd = new ProgressDialog(getActivity());
        pd.setCanceledOnTouchOutside(false);
        recyclerView = view.findViewById(R.id.myPosts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        loadPosts();
        layout = view.findViewById(R.id.relativeLayout);
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
                        String newProfileImage = "" + dataSnapshot1.child("profileImage").getValue();
                        if(!newProfileImage.equals(profileImage)) {
                            profileImage = newProfileImage;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ProfileImage", profileImage);
                            editor.commit();
                        }
                    }
                    try {
                        Glide.with(getActivity()).load(profileImage).into(avatartv);
                    } catch (Exception e) {

                    }
                    nam.setText(name);
                    email.setText(emaill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        avatartv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        return view;
    }

    private void loadPosts() {
        DatabaseReference databaseReference = FirebaseHandler.firebaseDatabase.getReference("Posts");
        Query query = databaseReference.orderByChild("uid").equalTo(FirebaseHandler.firebaseAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Post post = dataSnapshot1.getValue(Post.class);
                    posts.add(post);
                    adapterPosts = new PostsAdapter(getActivity(), posts);
                    recyclerView.setAdapter(adapterPosts);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
}