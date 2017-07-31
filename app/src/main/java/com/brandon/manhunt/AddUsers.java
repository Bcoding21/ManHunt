package com.brandon.manhunt;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUsers extends AppCompatActivity{

    private final String GAME_SESSION_ID = "game1";
    private TextView mHuntedField;
    DatabaseReference ref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        // Authentication
        mAuth = FirebaseAuth.getInstance();

        // Database
        ref = FirebaseDatabase.getInstance().getReference(GAME_SESSION_ID);
        ref.child(mAuth.getCurrentUser().getEmail().replace("@", "at").replace(".", "dot")).setValue("11:00:45");

        // EditText
        mHuntedField = (TextView) findViewById(R.id.hunted);
        mHuntedField.append(mAuth.getCurrentUser().getEmail());
    }
}

