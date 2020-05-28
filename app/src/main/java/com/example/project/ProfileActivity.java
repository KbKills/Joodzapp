package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView Showimg;
    private TextInputEditText Showname, Showage, Showphone, Showemail;
    private TextView MainName;
    private Button Editinfo;
    private FirebaseAuth uAuth;
    private FirebaseDatabase database;
    private FirebaseStorage uStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        uiSetup();
        function();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.logoutMenu:
            {
                uAuth.signOut();
                finish();
                Toast.makeText(ProfileActivity.this, "Logout Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void uiSetup()
    {
        Showimg = findViewById(R.id.displayDp);
        Showname = findViewById(R.id.displayName);
        Showage = findViewById(R.id.diaplayAge);
        Showemail = findViewById(R.id.displayEmail);
        Showphone = findViewById(R.id.displayPhone);
        Editinfo = findViewById(R.id.editUserInfo);
        MainName = findViewById(R.id.namefield);
    }

    private void function()
    {
        Editinfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(ProfileActivity.this, UpdateProfile.class));
            }
        });
        uAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        uStore = FirebaseStorage.getInstance();


        DatabaseReference myRef = database.getReference(uAuth.getUid());
        StorageReference uRef;
        uRef = uStore.getInstance().getReference();
        uRef.child(uAuth.getUid()).child("images/Display Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                Picasso.get().load(uri).into(Showimg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userProfile userprofile = dataSnapshot.getValue(userProfile.class);
                MainName.setText(userprofile.getUserName());
                Showname.setText(userprofile.getUserName());
                Showage.setText(userprofile.getAge());
                Showphone.setText(userprofile.getPhone());
                Showemail.setText(userprofile.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(ProfileActivity.this,"Could not get data.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
