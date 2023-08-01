package com.example.chat0.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;


import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.chat0.LoginActivity;
import com.example.chat0.Model.User;
import com.example.chat0.R;
import com.example.chat0.RegistrActivity;
import com.example.chat0.ResetPasswordActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    CircleImageView profile_image;
    EditText username;
    EditText email;
    TextView edit_txt;
    Button btn_save;
    TextView edit_pass;
    DatabaseReference reference;
    FirebaseUser fuser;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container,false);

        profile_image = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email);
        edit_txt = view.findViewById(R.id.edit_txt);
        btn_save = view.findViewById(R.id.btn_save);
        edit_pass = view.findViewById(R.id.edit_pass);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                }
                email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });

        edit_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setEnabled(true);
                email.setEnabled(true);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editData ();
            }
        });

        edit_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResetPasswordActivity.class));
            }
        });

        return view ;
    }

    private void  editData () {
        reference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", username.getText().toString());
        hashMap.put("email", email.getText().toString());
        hashMap.put("search", username.getText().toString().toLowerCase());

        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    username.setEnabled(false);
                    email.setEnabled(false);
                    Toast.makeText(getContext(), "Данные сохранены!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void openImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);

    }

    private String getFileExtension (Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false); // if you want user to wait for some process to finish
        builder.setView(R.layout.layout_loading_dialog);
        AlertDialog dialog = builder.create();
        dialog.show();

        /*final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Загрузка");
        pd.show();*/

        if(imageUri != null){
            final StorageReference finalReference = storageReference.
                    child(System.currentTimeMillis()+"."+getFileExtension(imageUri));

            uploadTask = finalReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return finalReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), "Изображение не выбрано", Toast.LENGTH_SHORT).show();
        }
    }

   ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            if (imageUri != null) {
                                try {
                                    uploadImage();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Выполняется загрузка", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
            });

}
