package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateProfile extends AppCompatActivity {
    private ImageView newDp;
    private EditText newAge, newName, newPhone;
    private Button editInfo;
    private TextView oldEmail;
    private FirebaseAuth uAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        uiSetup();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void uiSetup()
    {
        newDp = findViewById(R.id.updatepic);
        newAge = findViewById(R.id.updateage);
        newName = findViewById(R.id.updateName);
        newPhone = findViewById(R.id.updatephone);
        editInfo = findViewById(R.id.updateall);
        oldEmail =findViewById(R.id.oldemail);

        uAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference(uAuth.getUid());
        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userProfile userprofile = dataSnapshot.getValue(userProfile.class);
                newName.setText(userprofile.getUserName());
                newAge.setText(userprofile.getAge());
                newPhone.setText(userprofile.getPhone());
                oldEmail.setText(userprofile.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                Toast.makeText(UpdateProfile.this,"Could not get data.", Toast.LENGTH_SHORT).show();
            }
        });
        editInfo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String nname = newName.getText().toString();
                String nage = newAge.getText().toString();
                String nphone = newPhone.getText().toString();
                String oemail =oldEmail.getText().toString();

                userProfile userprofile = new userProfile(oemail,nage,nphone,nname);
                myRef.setValue(userprofile);
                Toast.makeText(UpdateProfile.this,"Update Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UpdateProfile.this, AppMain.class));
            }
        });
    }
}
