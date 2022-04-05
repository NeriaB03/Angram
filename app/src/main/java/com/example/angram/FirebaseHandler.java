package com.example.angram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirebaseHandler {
    public static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    public static void recoverAccount(String email, Context context, ProgressDialog loadingBar) {
        // send reset password email
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    Toast.makeText(context, "Send recovery mail", Toast.LENGTH_SHORT);
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(context, "Error while recover account", Toast.LENGTH_SHORT);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(context, "Error while recover account", Toast.LENGTH_SHORT);
            }
        });
    }

    public static void login(String email, String pass, Context context, ProgressDialog loadingBar) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    checkIfEmailVerfied(context, user, loadingBar, task);

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(context, "Error while logging in", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(context, "Error while logging in", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void signup(String name, String email, String pass, Context context, ProgressDialog loadingBar) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user != null) {
                        sendEmailVerification(context, user, loadingBar, name);
                    }
                } else {
                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });
    }

    public static void sendEmailVerification(Context context, FirebaseUser user, ProgressDialog loadingBar, String name) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    String email = user.getEmail();
                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", name);
                    hashMap.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/angram-42970.appspot.com/o/dog_paw.png?alt=media&token=b2561e23-1466-46a0-b99e-70a9276820cc");
                    DatabaseReference reference = firebaseDatabase.getReference("Users");
                    reference.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingBar.dismiss();
                            firebaseAuth.signOut();
                            context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
                            ((Activity) context).finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            firebaseAuth.signOut();
                            //restart the activity
                            ((Activity) context).overridePendingTransition(0, 0);
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(0, 0);
                            context.startActivity(new Intent(context.getApplicationContext(), RegisterActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    //restart the activity
                    ((Activity) context).overridePendingTransition(0, 0);
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(0, 0);
                    context.startActivity(new Intent(context.getApplicationContext(), RegisterActivity.class));
                }
            }
        });
    }

    public static void checkIfEmailVerfied(Context context, FirebaseUser user, ProgressDialog loadingBar, Task<AuthResult> task) {
        if(user.isEmailVerified()) {
            ((Activity) context).finish();
            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                String email = user.getEmail();
                String uid = user.getUid();
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("uid", uid);
                hashMap.put("name", "");
                hashMap.put("profileImage", "");
                DatabaseReference reference = firebaseDatabase.getReference("Users");
                reference.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        Toast.makeText(context, "Logged in successfully", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(context, MainActivity.class);
                        context.startActivity(mainIntent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error while logging in", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }
                });
            } else {
                loadingBar.dismiss();
                Toast.makeText(context, "Logged in successfully", Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(context, MainActivity.class);
                context.startActivity(mainIntent);
            }
        } else {
            loadingBar.dismiss();
            Toast.makeText(context, "You have to verify your email in order to login with this account", Toast.LENGTH_LONG).show();
            //sendEmailVerification(context, user, loadingBar, "");
        }
    }

    public static void signout() {
        firebaseAuth.signOut();
    }

    public static void loadPosts(String uid, List<Post> posts, Activity activity, RecyclerView recyclerView, TextView emptytv) {
        DatabaseReference databaseReference = firebaseDatabase.getReference("Posts");
        Query query = databaseReference.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Post post = dataSnapshot1.getValue(Post.class);
                    posts.add(post);
                    PostsAdapter adapterPosts = new PostsAdapter(activity, posts);
                    recyclerView.setAdapter(adapterPosts);
                }
                if(posts.size() == 0) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    emptytv.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void updateProfileImage(String storagepath, Uri uri, SharedPreferences sharedPreferences, Context context, ProgressDialog pd) {
        String filepathname = storagepath + "/" + firebaseAuth.getCurrentUser().getUid();
        StorageReference storageReference1 = firebaseStorage.getReference().child(filepathname);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;

                // We will get the url of our image using uritask
                final Uri downloadUri = uriTask.getResult();
                if (uriTask.isSuccessful()) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("profileImage", downloadUri.toString());
                    DatabaseReference usersReference = firebaseDatabase.getReference("Users");
                    usersReference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference databaseReference1 = firebaseDatabase.getReference("Posts");
                            databaseReference1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                        Post posts = dataSnapshot1.getValue(Post.class);
                                        if (posts.getUid().equals(firebaseAuth.getCurrentUser().getUid())) {
                                            databaseReference1.child(posts.getPublishDate()).child("profileImage").setValue(downloadUri.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            pd.dismiss();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ProfileImage", downloadUri.toString());
                            editor.commit();
                            Toast.makeText(context, "Updated Profile Image", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(context, "Error While Updating Profile Image", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    pd.dismiss();
                    Toast.makeText(context, "Error While Updating Profile Image", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, "Error While Updating Profile Image", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void uploadPost(EditText des, ImageView image, Uri imageuri, String uid, String name, String email, String profileImage, String description, Context context, Activity activity, ProgressDialog pd, Bitmap bitmap) {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filepathname = "Posts/" + "post" + timestamp;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        // initialising the storage reference for updating the data
        StorageReference storageReference1 = firebaseStorage.getReference().child(filepathname);
        storageReference1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // getting the url of image uploaded
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                String downloadUri = uriTask.getResult().toString();
                if (uriTask.isSuccessful()) {
                    // if task is successful the update the data into firebase
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("uid", uid);
                    hashMap.put("name", name);
                    hashMap.put("profileImage", profileImage);
                    hashMap.put("description", description);
                    hashMap.put("image", downloadUri);
                    hashMap.put("publishDate", timestamp);
                    hashMap.put("likes", "0");

                    // set the data into firebase and then empty the title ,description and image data
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Posts");
                    databaseReference.child(timestamp).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(context, "Published", Toast.LENGTH_LONG).show();
                                    des.setText("");
                                    image.setImageURI(null);
                                    context.startActivity(new Intent(context, MainActivity.class));
                                    activity.finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
