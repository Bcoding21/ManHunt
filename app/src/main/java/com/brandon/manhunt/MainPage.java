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

public class MainPage extends AppCompatActivity implements View.OnClickListener{

    TextView mWelcome;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mAuth = FirebaseAuth.getInstance();

        mWelcome = (TextView) findViewById(R.id.display);
        findViewById(R.id.log_out).setOnClickListener(this);
        findViewById(R.id.delete_acc).setOnClickListener(this);
        findViewById(R.id.play_game).setOnClickListener(this);

        String name = mAuth.getCurrentUser().getDisplayName();

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
                startActivity(new Intent(MainPage.this, gamePage.class));
                break;

            case R.id.delete_acc:
                FirebaseUser user = mAuth.getCurrentUser();

                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainPage.this, "Logged out", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.log_out:
                mAuth.getInstance().signOut();
                startActivity(new Intent(MainPage.this, MainActivity.class));
                break;
        }
    }
}
