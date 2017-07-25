package com.brandon.manhunt;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText mEmailField, mPasswordField, mDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Firebase instant
        mAuth = FirebaseAuth.getInstance();

        // Button
        findViewById(R.id.SignInButton).setOnClickListener(this);

        // EditText
        mEmailField = (EditText)findViewById(R.id.email);
        mDisplayName = (EditText)findViewById(R.id.display);
        mPasswordField = (EditText)findViewById(R.id.password);

    }

    public void onClick(View v){

        createAcc(mDisplayName.getText().toString(),
                mEmailField.getText().toString(),
                mPasswordField.getText().toString());

    }

    private void createAcc(String email, String password, String displayName){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Sign up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });




    }
}
