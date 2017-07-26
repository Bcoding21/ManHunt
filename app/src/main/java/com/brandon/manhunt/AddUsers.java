package com.brandon.manhunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddUsers extends AppCompatActivity implements View.OnClickListener{

    EditText mPhoneNumberField;
    String
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        // Button
        findViewById(R.id.add_button).setOnClickListener(this);
        findViewById(R.id.start_game_button).setOnClickListener(this);

        // EditText
        mPhoneNumberField = (EditText)findViewById(R.id.phone_number_field);

        // Game session number
        game_session_id = "12345";

    }

    public void onClick(View v){

        switch(v.getId()){

            case R.id.add_button:
                String phone_number = mPhoneNumberField.getText().toString();

                break;

            case R.id.start_game_button:
                break;

        }
    }
}
