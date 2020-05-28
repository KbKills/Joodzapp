package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.IOException;

public class SignupActivity extends AppCompatActivity {

    private EditText Name, Email, Pass, Age, Phone;
    private Button Signup;
    private ImageView Avatar;
    private FirebaseAuth uAuth;
    String name, pass,phone,email,age;
    private FirebaseStorage uStore;
    private final int PICK_IMAGE = 12345;
    StorageReference myStore;
    Uri imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        uiSetup();
        uAuth = FirebaseAuth.getInstance();
        uStore = FirebaseStorage.getInstance();

        myStore = uStore.getReference();

        Avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });


        Signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(validate())
                {
                    String user_email = Email.getText().toString().trim();
                    String user_password = Pass.getText().toString().trim();

                    uAuth.createUserWithEmailAndPassword(user_email , user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                sendEmailVer();
                            }
                            else
                            {
                                Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void SelectImage()
    {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            imagePath = data.getData();
            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                Avatar.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uiSetup()
    {

        Name = findViewById(R.id.newusername);
        Email = findViewById(R.id.newuseremail);
        Pass = findViewById(R.id.newuserpasword);
        Age = findViewById(R.id.age);
        Phone = findViewById(R.id.mobilenumber);
        Signup = findViewById(R.id.registerbutton);
        Avatar = findViewById(R.id.profpic);
    }

    private Boolean validate()
    {
        Boolean result = false;

        name = Name.getText().toString();
        pass = Pass.getText().toString();
        email = Email.getText().toString();
        phone = Phone.getText().toString();
        age = Age.getText().toString();

        if(imagePath == null)
        {
            Toast.makeText(this, "Click the avatar to add a profile picture", Toast.LENGTH_SHORT).show();
        }


        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || phone.isEmpty() || age.isEmpty() || imagePath == null)
            {
                Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
            }
        else
            {
                result = true;
            }
        return result;
    }

    private void sendEmailVer()
    {
        FirebaseUser uVer = uAuth.getInstance().getCurrentUser();
        if(uVer != null)
        {
            uVer.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        sendUserData();
                        Toast.makeText(SignupActivity.this, "Email sent for Verification", Toast.LENGTH_SHORT).show();
                        uAuth.signOut();
                        finish();
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this, "Email NOT sent for verification", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserData ()
    {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference myRef = data.getReference(uAuth.getUid());
        StorageReference imgRef = myStore.child(uAuth.getUid()).child("images").child("Display Picture");
        UploadTask upTask = imgRef.putFile(imagePath);
        upTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupActivity.this, "File Upload Failed", Toast.LENGTH_SHORT).show();;
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignupActivity.this, "File Upload Successful", Toast.LENGTH_SHORT).show();;
            }
        });
        userProfile profile =new userProfile(email,age,phone,name);
        myRef.setValue(profile);
    }
}