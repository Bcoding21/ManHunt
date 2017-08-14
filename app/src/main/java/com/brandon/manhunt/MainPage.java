package com.brandon.manhunt;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainPage extends AppCompatActivity implements View.OnClickListener{

    TextView mWelcome;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Firebase
        mReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // TextView
        mWelcome = (TextView) findViewById(R.id.display);

        // Buttons
        findViewById(R.id.log_out).setOnClickListener(this);
        findViewById(R.id.delete_acc).setOnClickListener(this);
        findViewById(R.id.play_game).setOnClickListener(this);

        // Listens for sign in/sign out
        signOutCheck();
    }



    public void onClick(View v){
        switch (v.getId()){

            case R.id.play_game:
                Intent i = new Intent(MainPage.this, gamePage.class);
                startActivity(i);
                break;

            case R.id.delete_acc:
                deleteAccount();
                break;

            case R.id.log_out:
                mAuth.getInstance().signOut();
                Intent myIntent = new Intent(MainPage.this, MainActivity.class);
                startActivity(myIntent);
                break;
        }
    }

    private void signOutCheck(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    startActivity(new Intent(MainPage.this, MainActivity.class));
                }
                // ...
            }
        };
    }

    private void deleteAccount(){

        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainPage.this, R.string.delete_account_success,
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainPage.this, MainActivity.class));

                } else if (!task.isSuccessful()){
                    Toast.makeText(MainPage.this, R.string.delete_account_fail,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDisplay(){

        String email = User.getInstance().getEmail();

        mReference.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = (String)dataSnapshot.getValue();
                mWelcome.setText("Welcome " + username);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
