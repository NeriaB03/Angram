package com.example.angram;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<com.example.angram.PostsAdapter.MyHolder> {
    Context context;
    String myuid;
    private DatabaseReference liekeref, postref;
    boolean mprocesslike = false;
    List<Post> posts;
    String profileImage;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        liekeref = FirebaseDatabase.getInstance().getReference().child("Likes");
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final String uid = posts.get(position).getUid();
        String nameh = posts.get(position).getName();
        final String descri = posts.get(position).getDescription();
        final String date = posts.get(position).getPublishDate();
        String profileImage = posts.get(position).getProfileImage();
        String likes = posts.get(position).getLikes();
        final String image = posts.get(position).getImage();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(date));
        String timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.name.setText(nameh);
        holder.description.setText(descri);
        holder.publishDate.setText(timedate);
        holder.like.setText(likes + " Likes");
        try {
            Glide.with(context).load(profileImage).into(holder.picture);
        } catch (Exception e) {

        }
        holder.image.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(image).into(holder.image);
        } catch (Exception e) {

        }
        holder.image.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onDoubleClick() {
                final int likes = Integer.parseInt(posts.get(position).getLikes());
                mprocesslike = true;
                final String postid = posts.get(position).getPublishDate();
                liekeref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mprocesslike) {
                            if (dataSnapshot.child(postid).hasChild(myuid)) {
                                postref.child(postid).child("likes").setValue("" + (likes - 1));
                                liekeref.child(postid).child(myuid).removeValue();
                                mprocesslike = false;
                            } else {
                                postref.child(postid).child("likes").setValue("" + (likes + 1));
                                liekeref.child(postid).child(myuid).setValue("Liked");
                                mprocesslike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(300);
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.image.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/Download");
                dir.mkdirs();

                String filename = String.format("%d.png", System.currentTimeMillis());

                File outFile = new File(dir, filename);
                try {
                    FileOutputStream outputStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    private void showMoreOptions(ImageButton more, String uid, String myuid, final String pid, final String image) {
        PopupMenu popupMenu = new PopupMenu(context, more, Gravity.END);
        if (uid.equals(myuid)) {
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "DELETE");
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 0) {
                    deltewithImage(pid, image);
                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void deltewithImage(final String pid, String image) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting");
        StorageReference picref = FirebaseStorage.getInstance().getReferenceFromUrl(image);
        picref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("publishDate").equalTo(pid);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                        pd.dismiss();
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        ImageView picture, image;
        TextView name, publishDate, description, like;
        Button likebtn;
        LinearLayout profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            picture = itemView.findViewById(R.id.profileImage);
            image = itemView.findViewById(R.id.postImage);
            name = itemView.findViewById(R.id.username);
            publishDate = itemView.findViewById(R.id.publishDate);
            description = itemView.findViewById(R.id.description);
            like = itemView.findViewById(R.id.likes);
            profile = itemView.findViewById(R.id.profilelayout);
        }
    }
}