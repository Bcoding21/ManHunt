package com.brandon.manhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mSignUpButton, mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSignInButton = (Button)findViewById(R.id.sign_in);
        mSignUpButton = (Button)findViewById(R.id.sign_up);
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.sign_in:
                startActivity(new Intent(this, Sign_in_page.class));
                break;

            case R.id.sign_up:
                startActivity(new Intent(this,Sign_up_page.class));
                break;

        }
    }
}
