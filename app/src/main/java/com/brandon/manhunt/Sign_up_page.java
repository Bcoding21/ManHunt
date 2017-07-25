package com.brandon.manhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Sign_up_page extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmailInput, mPasswordInput, mDisplayName;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        // EditTexts/TextView initialization
        mEmailInput = (EditText) findViewById(R.id.email_edit);
        mPasswordInput = (EditText) findViewById(R.id.password_edit);
        mDisplayName = (EditText) findViewById(R.id.display_name);
        mDisplayName.setText("Brandon Cole");
        mEmailInput.setText(getText(R.string.example_email));
        mPasswordInput.setText(getText(R.string.example_password));
        progress = new ProgressDialog(this);

        //Button
        findViewById(R.id.login_button).setOnClickListener(this);

        //Authentication
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("Tag", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onClick(View v) {
        createAccount();
        saveToDatabase();
    }

    private void saveToDatabase(){
        String id = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(id);
        ref.setValue(new UserProfile());
    }

    private void createAccount() {
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();
        final String name = mDisplayName.getText().toString();

        progress.setMessage("Working...");
        progress.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("IncreateAccount", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            setDisplayName(name);
                            Toast.makeText(Sign_up_page.this, "Email already registered",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Sign_up_page.this, "Sign up complete!",
                                    Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Sign_up_page.this, Sign_in_page.class);
                            i.putExtra("name", name);
                            startActivity(i);
                            finish();
                        }
                        progress.cancel();
                    }
                });
    }

    private void setDisplayName(String name){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User profile updated.");
                        }
                    }
                });
    }

}
