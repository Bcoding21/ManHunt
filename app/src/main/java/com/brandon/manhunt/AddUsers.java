package com.brandon.manhunt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUsers extends AppCompatActivity implements View.OnClickListener{

    private final String GAME_SESSION_ID = "12345";
    private EditText mPhoneNumberField;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);

        // Button
        findViewById(R.id.add_button).setOnClickListener(this);
        findViewById(R.id.start_game_button).setOnClickListener(this);

        // EditText
        mPhoneNumberField = (EditText)findViewById(R.id.phone_number_field);

        // Database
        ref = FirebaseDatabase.getInstance().getReference(GAME_SESSION_ID);

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
