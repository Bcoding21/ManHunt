package com.brandon.manhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.SignUp).setOnClickListener(this);
        findViewById(R.id.SignIn).setOnClickListener(this);

    }

    public void onClick(View v){

        switch(v.getId()){

            case R.id.SignIn:
                startActivity(new Intent(this, SignIn.class));
                break;

            case R.id.SignUp:
                startActivity(new Intent(this, SignUp.class));
                break;
        }
    }
}
