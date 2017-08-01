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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        ref = FirebaseDatabase.getInstance().getReference();

        final String user_email = mAuth.getCurrentUser().getEmail();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("Hunted")){
                    ref.child("Hunted").child(user_email.replace("@", "at").replace(".", "dot"))
                            .setValue("Location");
                }
                else if (dataSnapshot.hasChild("Hunted")) {
                    ref.child("Hunters").child(user_email.replace("@", "at").replace(".", "dot"))
                            .setValue("Location");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
