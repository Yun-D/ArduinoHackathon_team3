package com.example.arduinohackathon_team3.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arduinohackathon_team3.R;
import com.google.firebase.auth.FirebaseAuth;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //로그아웃을 처리한다.
    private void logout(){
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        try{
            if(MainActivity.mGoogleSignInClient != null) {
                MainActivity.mGoogleSignInClient.signOut();
            }
            mFirebaseAuth.signOut();
            Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
            Intent i=new Intent(this, com.example.arduinohackathon_team3.Activity.MainActivity.class);
            startActivity(i);
            //finish();
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
