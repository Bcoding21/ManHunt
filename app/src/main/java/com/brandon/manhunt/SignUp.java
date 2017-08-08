package com.brandon.manhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.id.message;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText mEmailField, mPasswordField, mDisplayName;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Progress Dialog
        progress = new ProgressDialog(this);

        // Button
        findViewById(R.id.SignUpButton).setOnClickListener(this);

        // EditText
        mEmailField = (EditText)findViewById(R.id.email);
        mDisplayName = (EditText)findViewById(R.id.display);
        mPasswordField = (EditText)findViewById(R.id.password);
        mEmailField.setText("brandoncole673@gmail.com");
        mPasswordField.setText("Unknown21");
        mDisplayName.setText("Brandon");


        // Firebase instant
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {


                }
            }
        };
    }

    public void onClick(View v){

        createAcc(mEmailField.getText().toString().trim(),
                mPasswordField.getText().toString().trim());

    }

    private void createAcc(String email, String password){

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill each field", Toast.LENGTH_SHORT).show();
        }
        else {
            User.getInstance().setName(mDisplayName.getText().toString());
            User.getInstance().setEmail(mEmailField.getText().toString());
            storeUserName();


            progress.setMessage("Working..");
            progress.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                progress.cancel();
                                Exception e = task.getException();
                                Toast.makeText(SignUp.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else{
                                progress.cancel();
                                Toast.makeText(SignUp.this, "Sign up complete", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUp.this, MainPage.class);
                                i.putExtra("name", mDisplayName.getText().toString());
                                startActivity(i);
                                finish();
                            }
                        }
                    });
        }
    }

    private void storeUserName(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.child("Username").child(User.getInstance().getEmail()).setValue(User.getInstance().getDisplayName());
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
}
