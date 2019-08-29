package com.example.arduinohackathon_team3.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.arduinohackathon_team3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class StudentMainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        Button mBtnToiletCheck=findViewById(R.id.btnToiletCheck);
        Button mBtnClassroomCheck=findViewById(R.id.btnClassroomCheck);
        Button mBtnOfficeCheck=findViewById(R.id.btnOfficeCheck);
        Button mBtnCalendar = findViewById(R.id.btnCalendar);


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

        mBtnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.swu.ac.kr/front/mobilehaksaschedule.do?gubun=1"));
                startActivity(intent);
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

            case R.id.action_withdraw:
                withdraw();
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

    //회원탈퇴를 처리한다.
    private void withdraw() {
        AlertDialog.Builder builer = new AlertDialog.Builder(StudentMainActivity.this);
        builer.setTitle("회원 탈퇴");
        builer.setMessage("정말 탈퇴하시겠습니까?");
        builer.setNegativeButton("아니오", null);
        builer.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String guid = JoinActivity.getUserIdFromUUID(mFirebaseAuth.getCurrentUser().getEmail());
                FirebaseDatabase.getInstance().getReference().child("members").child(guid).removeValue();
                mFirebaseAuth.signOut();
                Toast.makeText(StudentMainActivity.this, "탈퇴 되었습니다", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StudentMainActivity.this, MainActivity.class));
                ActivityCompat.finishAffinity(StudentMainActivity.this);
            }
        });
        builer.create().show(); // 다이어로그 나타남
    }
}
