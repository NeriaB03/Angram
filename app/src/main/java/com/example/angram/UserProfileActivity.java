package com.example.angram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private de.hdodenhof.circleimageview.CircleImageView avatar;
    private TextView username;
    private TextView emptytv;
    private RecyclerView recyclerView;
    private RelativeLayout layout;
    private List<Post> posts;
    private PostsAdapter adapterPosts;
    private String name;
    private String uid;
    private String profImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            name = extras.getString("username");
        } else {
            //exit
        }

        avatar = findViewById(R.id.userAvatar);
        username = findViewById(R.id.usernametv);
        emptytv = findViewById(R.id.emptytv);
        username.setText(name);
        recyclerView = findViewById(R.id.posts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(UserProfileActivity.this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        posts = new ArrayList<>();
        Query query = FirebaseHandler.firebaseDatabase.getReference("Users").orderByChild("name").equalTo(name);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    uid = "" + dataSnapshot1.child("uid").getValue();
                    profImg = "" + dataSnapshot1.child("profileImage").getValue();
                    try {
                        Glide.with(UserProfileActivity.this).load(profImg).into(avatar);
                    } catch (Exception e) {

                    }
                    FirebaseHandler.loadPosts(uid, posts,UserProfileActivity.this, recyclerView, emptytv);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //exit
            }
        });
        layout = findViewById(R.id.relativeLayout);
    }
}