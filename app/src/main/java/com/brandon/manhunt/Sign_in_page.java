package com.brandon.manhunt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class Sign_in_page extends AppCompatActivity implements View.OnClickListener{

    private EditText mEmailField, mPasswordField;
    private FirebaseAuth mAuth;
    private TextView mStatus;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);

        //EditTexts, TextView, ProgressDialog
        mEmailField = (EditText)findViewById(R.id.username_edit);
        mPasswordField = (EditText)findViewById(R.id.password_edit);
        mEmailField.setText(R.string.example_email);
        mPasswordField.setText(R.string.example_password);
        mStatus = (TextView)findViewById(R.id.status_view);
        progress = new ProgressDialog(this);

        //Buttons
        findViewById(R.id.login_button).setOnClickListener(this);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    public void onClick(View v){
        signIn();
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


    private void signIn() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        progress.setMessage("Processing...");
        progress.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TAG", "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            progress.cancel();
                            Log.w("TAG", "signInWithEmail:failed", task.getException());
                            Toast.makeText(Sign_in_page.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progress.cancel();
                            Toast.makeText(Sign_in_page.this, "Login Successful",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
