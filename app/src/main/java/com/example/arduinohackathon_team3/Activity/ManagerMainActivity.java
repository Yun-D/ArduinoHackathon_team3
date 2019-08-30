package com.example.arduinohackathon_team3.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.arduinohackathon_team3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ManagerMainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //커스텀한 액션바로 보여주기
        getSupportActionBar().setCustomView(R.layout.custom_bar);  //커스텀한 액션바 가져오기

        Button mBtnToiletCheck=findViewById(R.id.btnToiletCheck);
        Button mBtnToiletProblem=findViewById(R.id.btnToiletProblem);
        Button mBtnClassroomCheck=findViewById(R.id.btnClassroomCheck);

        mBtnToiletCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ToiletCheckActivity.class);
                startActivity(i);
            }
        });

        mBtnToiletProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ToiletProblemActivity.class);
                startActivity(i);
            }
        });

        mBtnClassroomCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ClassroomCheckActivity.class);
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

            case R.id.action_withdraw:
                withdraw();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //로그아웃을 처리한다.
    private void logout(){
        try{
            if(MainActivity.mGoogleSignInClient != null) {
                MainActivity.mGoogleSignInClient.signOut();
            }
            mFirebaseAuth.signOut();
            Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, com.example.arduinohackathon_team3.Activity.MainActivity.class);
            startActivity(i);
            //finish();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //회원탈퇴를 처리한다.
    private void withdraw() {
        AlertDialog.Builder builer = new AlertDialog.Builder(ManagerMainActivity.this);
        builer.setTitle("회원 탈퇴");
        builer.setMessage("정말 탈퇴하시겠습니까?");
        builer.setNegativeButton("아니오", null);
        builer.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //MemberBean loginMember = FileDB.getLoginMember(ManagerMainActivity.this);

                String guid = JoinActivity.getUserIdFromUUID(mFirebaseAuth.getCurrentUser().getEmail());
                FirebaseDatabase.getInstance().getReference().child("members").child(guid).removeValue();
                mFirebaseAuth.signOut();
                Toast.makeText(ManagerMainActivity.this, "탈퇴 되었습니다", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ManagerMainActivity.this, MainActivity.class));
                ActivityCompat.finishAffinity(ManagerMainActivity.this);
            }
        });
        builer.create().show(); // 다이어로그 나타남
    }
}
