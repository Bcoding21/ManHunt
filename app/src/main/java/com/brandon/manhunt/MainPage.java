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
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        //Firebase
        mAuth = FirebaseAuth.getInstance();

        // TextView
        mWelcome = (TextView) findViewById(R.id.display);
        findViewById(R.id.log_out).setOnClickListener(this);
        findViewById(R.id.delete_acc).setOnClickListener(this);
        findViewById(R.id.play_game).setOnClickListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = User.getInstance().getEmail();
                String username = (String)dataSnapshot.child(email).getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mWelcome.append("\n" + mUserName);

        //Buttons
        findViewById(R.id.log_out).setOnClickListener(this);
        findViewById(R.id.delete_acc).setOnClickListener(this);
        findViewById(R.id.play_game).setOnClickListener(this);

        // Listens for sign in/sign out
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

    public void onClick(View v){
        switch (v.getId()){

            case R.id.play_game:
                Intent i = new Intent(MainPage.this, gamePage.class);
                i.putExtra("name", mUserName);
                startActivity(i);
                break;

            case R.id.delete_acc:
                FirebaseUser user = mAuth.getCurrentUser();
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainPage.this, R.string.delete_account_success,
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainPage.this, MainActivity.class));

                        }
                    }
                });
                break;

            case R.id.log_out:
                mAuth.getInstance().signOut();
                startActivity(new Intent(MainPage.this, MainActivity.class));
                break;
        }
    }

    private void getUsername(){
        final FirebaseDatabase data = FirebaseDatabase.getInstance();
        DatabaseReference ref = data.getReference();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String email = User.getInstance().getEmail();
                String username = (String)dataSnapshot.child(email).getValue();
                mUserName = username;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Not working", Toast.LENGTH_LONG).show();
            }
        });

    }
}
