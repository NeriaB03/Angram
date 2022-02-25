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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        String email = user.getEmail();
                        String uid = user.getUid();
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("email", email);
                        hashMap.put("uid", uid);
                        hashMap.put("name", "");
                        hashMap.put("profile_image", "");
                        hashMap.put("followers", "0");
                        hashMap.put("following", "0");
                        DatabaseReference reference = firebaseDatabase.getReference("Users");
                        reference.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                Toast.makeText(context, "Logged in successfully", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(context, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(mainIntent);
                    }
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
                    String email = user.getEmail();
                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", name);
                    hashMap.put("profile_image", "https://firebasestorage.googleapis.com/v0/b/angram-42970.appspot.com/o/dog_paw.png?alt=media&token=b2561e23-1466-46a0-b99e-70a9276820cc");
                    hashMap.put("followers", "0");
                    hashMap.put("following", "0");
                    DatabaseReference reference = firebaseDatabase.getReference("Users");
                    reference.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loadingBar.dismiss();
                            Toast.makeText(context, "Registered User " + FirebaseHandler.firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_LONG).show();
                            Intent mainIntent = new Intent(context, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(mainIntent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error Occurred", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            firebaseAuth.signOut();
                        }
                    });

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

    public static void signout(Context context) {
        firebaseAuth.signOut();
        context.startActivity(new Intent(context, LoginActivity.class));
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
                    hashMap.put("image_profile", downloadUri.toString());
                    DatabaseReference reference = firebaseDatabase.getReference("Users");
                    reference.child(firebaseAuth.getCurrentUser().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
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

    public static void uploadPost(EditText des, ImageView image, Uri imageuri, String uid, String name, String email, String profile_image, String description, Context context, Activity activity, ProgressDialog pd, Bitmap bitmap) {
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
                    hashMap.put("profile_image", profile_image);
                    hashMap.put("description", description);
                    hashMap.put("image", downloadUri);
                    hashMap.put("publishDate", timestamp);
                    hashMap.put("likes", "0");

                    // set the data into firebase and then empty the title ,description and image data
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
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
