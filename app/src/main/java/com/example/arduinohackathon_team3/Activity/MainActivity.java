package com.example.arduinohackathon_team3.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.arduinohackathon_team3.Bean.MemberBean;
import com.example.arduinohackathon_team3.Database.FileDB;
import com.example.arduinohackathon_team3.R;
import com.example.arduinohackathon_team3.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //구글 로그인 클라이언트 제어자
    public static GoogleSignInClient mGoogleSignInClient;
    //FireBase 인증객체
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mFirebaseDB = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //이 페이지만 액션바 숨김
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        //구글 로그인 객체선언
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }
    private void googleSignIn(){
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, 1004);
    }  //end googleSignIn()

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //구글 로그인 버튼 응답
        if (requestCode == 1004) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //구글 로그인 성공
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getBaseContext(), "구글 로그인에 성공 하였습니다.", Toast.LENGTH_LONG).show();

                final String loginedEmail = account.getEmail();

                //DB 에서 이메일 조회
                mFirebaseDB.getReference().child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int count = 0;
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            try {
                                MemberBean bean = snapshot.getValue(MemberBean.class);
                                String UUIDEmail = Utils.getUserIdFromUUID(loginedEmail);
                                if (TextUtils.equals(bean.memId, UUIDEmail)) {
                                    //FileDB memberBean 값을 저장
                                    FileDB.setLoginMember(getApplicationContext(), bean);

                                    //Firebase 인증 - true: 관리자, false: 유저
                                    firebaseAuthWithGoogle(account, bean.isAdmin);
                                    count = 1;
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if(count == 0) {
                            //회원가입 Activity 로 이동
                            Intent i = new Intent(MainActivity.this, JoinActivity.class);
                            i.putExtra("email", loginedEmail);
                            i.putExtra("tokenId", account.getIdToken());
                            startActivity(i);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }  //end onActivityResult()

    private void firebaseAuthWithGoogle(GoogleSignInAccount account, final boolean isAdmin){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(getBaseContext(), "Firebase 로그인 성공",Toast.LENGTH_LONG).show();
                    if(isAdmin == false){
                        goStudentMainActivity(); //유저
                    }else{
                        goManagerMainActivity(); //관리자
                    }
                }else{
                    Toast.makeText(getBaseContext(), "Firebase 로그인 실패",
                            Toast.LENGTH_LONG).show();
                    Log.w("Test","인증실패: "+task.getException());
                }
            }
        });
    }  //end firebaseAuthWithGoogle()

    //게시판 메인 화면으로 이동한다
    private void goStudentMainActivity(){
        Intent i = new Intent(this, StudentMainActivity.class);
        startActivity(i);
        finish();
    }  // end goStudentMainActivity

    private void goManagerMainActivity(){
        Intent i = new Intent(this, ManagerMainActivity.class);
        startActivity(i);
        finish();
    } // end goManagerMainActivity
}
