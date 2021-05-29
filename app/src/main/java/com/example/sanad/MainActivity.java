package com.example.sanad;
import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register;
    private TextView login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        register = (TextView) findViewById(R.id.button4);
        register.setOnClickListener(this);
        login = (TextView) findViewById(R.id.button3);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button4:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }

    }
}