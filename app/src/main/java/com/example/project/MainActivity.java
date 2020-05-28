package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private EditText Name, Password;
    String email, pass;
    private TextView Information, newuser, forgotpass;
    private Button Login;
    //private ImageView CycleLogo;
    private int check = 5;
    private FirebaseAuth uAuth;
    private ProgressDialog dialogue;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkMapServices();
        UIsetup();
    }

    private boolean checkMapServices(){
    if(isServicesOK()){
        if(isMapsEnabled()){
            return true;
        }
    }
    return false;
}

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            UIsetup();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    UIsetup();;
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    private void UIsetup()
    {
        newuser = findViewById(R.id.Register);
        Name = findViewById(R.id.Username);
        Password = findViewById(R.id.Pass);
        Information =  findViewById(R.id.Attempts);
        Login =  findViewById(R.id.btnLogin);
        forgotpass =findViewById(R.id.forgotpass);
        //CycleLogo = (ImageView)findViewById(R.id.cyclelogo);

        Information.setText("Attempts Remaining : 5");

        forgotpass.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        uAuth = FirebaseAuth.getInstance();
        dialogue = new ProgressDialog(this);
        FirebaseUser user = uAuth.getCurrentUser();

        if(user!=null)
        {
            finish();
            startActivity(new Intent(MainActivity.this, AppMain.class));
        }

        newuser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                validate(Name.getText().toString(), Password.getText().toString());
            }
        });
    }

    private void validate(String username, String userpass)
    {
        email = Name.getText().toString();
        pass = Password.getText().toString();
        if(email.isEmpty() || pass.isEmpty())
        {
            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            check--;
            Information.setText("Attempts Remaining : " + check);
            dialogue.dismiss();
            if (check ==0)
            {
                Login.setEnabled(false);
            }
        }
        else {
            dialogue.setMessage("Logging you in");
            dialogue.show();
            uAuth.signInWithEmailAndPassword(username, userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        dialogue.dismiss();
                        //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                        checkEmailVerification();
                    } else {
                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        check--;
                        Information.setText("Attempts Remaining : " + check);
                        dialogue.dismiss();
                        if (check == 0) {
                            Login.setEnabled(false);
                        }
                    }
                }
            });
        }
    }
    //shdbmv nb nbv ngb
    private void checkEmailVerification()
    {
        FirebaseUser uVer;
        uVer = FirebaseAuth.getInstance().getCurrentUser();
        Boolean flag = uVer.isEmailVerified();
        if (flag)
        {
            finish();
            startActivity(new Intent(MainActivity.this, AppMain.class));
        }
        else
        {
            Toast.makeText(MainActivity.this, "Please complete the Email Verification", Toast.LENGTH_SHORT).show();
            uAuth.signOut();
        }

    }
}
