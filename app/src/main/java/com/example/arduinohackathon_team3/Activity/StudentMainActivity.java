package com.example.arduinohackathon_team3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.arduinohackathon_team3.R;

public class StudentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        Button mBtnToiletCheck=findViewById(R.id.btnToiletCheck);
        Button mBtnClassroomCheck=findViewById(R.id.btnClassroomCheck);
        Button mBtnOfficeCheck=findViewById(R.id.btnOfficeCheck);


        mBtnToiletCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ToiletCheckActivity.class);
                startActivity(i);
            }
        });

        mBtnClassroomCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ClassroomCheckActivity.class);
                startActivity(i);
            }
        });

        mBtnOfficeCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),OfficeCheckActivity.class);
                startActivity(i);
            }
        });
    }
}
