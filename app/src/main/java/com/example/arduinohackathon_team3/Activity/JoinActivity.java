package com.example.arduinohackathon_team3.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinohackathon_team3.Bean.MemberBean;
import com.example.arduinohackathon_team3.Database.FileDB;
import com.example.arduinohackathon_team3.R;
import com.example.arduinohackathon_team3.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class JoinActivity extends AppCompatActivity {

    private static FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private TextView mTxtId;
    private EditText mEdtName, mEdtNum;
    private String email;
    private String IdToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //이 페이지만 액션바 숨김
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_join);

        Intent intent = getIntent();
        IdToken = intent.getStringExtra("tokenId");
        email = intent.getStringExtra("email");

        mTxtId = findViewById(R.id.txtId);
        mEdtName = findViewById(R.id.edtName);
        mEdtNum = findViewById(R.id.edtNum);
        Button mBtnJoin = findViewById(R.id.btnJoin);

        mTxtId.setText(email);

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinProcess();
            }
        });
    } //end onCreate()

    public static String getUserIdFromUUID(String userEmail) {
        long val = UUID.nameUUIDFromBytes(userEmail.getBytes()).getMostSignificantBits();
        return String.valueOf(val);
    }

    //회원가입 처리
    private void joinProcess() {
        MemberBean memberBean = new MemberBean();

//        //mEdtNum 과 파이어베이스 리스트 비교해 중복 체크
//        MemberBean findMemNumber = FileDB.getFindMemNum(this, mEdtNum.getText().toString());
//
//        if (findMemNumber != null) { //사용자에게 입력받은 학번이 데이터베이스에 존재한다면
//            Toast.makeText(getApplicationContext(), "이미 등록된 학번 또는 관리자 번호 입니다.", Toast.LENGTH_SHORT).show();
//        }
//        else {
            //데이터베이스에 저장한다.
            memberBean.memId = Utils.getUserIdFromUUID(email);
            memberBean.isAdmin = false;
            memberBean.memName = mEdtName.getText().toString();
            memberBean.memNum = mEdtNum.getText().toString();

            uploadMember(memberBean);
        //}

    }

    public void uploadMember(MemberBean memberBean){
        //Firebase 데이터베이스에 메모를 등록한다.
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        dbRef.child("members").child( memberBean.memId ).setValue(memberBean);
        //파이어 베이스 인증
        firebaseAuthWithGoogle(IdToken);
    }

    private void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(getBaseContext(), "Firebase 로그인 성공",Toast.LENGTH_LONG).show();

                    goStudentMainActivity();
                }else{
                    Toast.makeText(getBaseContext(), "Firebase 로그인 실패",
                            Toast.LENGTH_LONG).show();
                    Log.w("Test","인증실패: "+task.getException());
                }
            }
        });
    }  //end firebaseAuthWithGoogle()


    private void goStudentMainActivity(){
        Intent i = new Intent(this, StudentMainActivity.class);
        startActivity(i);
        finish();
    }  // end goMainActivity
}
