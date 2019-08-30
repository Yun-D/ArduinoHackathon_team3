package com.example.arduinohackathon_team3.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.arduinohackathon_team3.Bean.ClassroomBean;
import com.example.arduinohackathon_team3.Bean.WeatherBean;
import com.example.arduinohackathon_team3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

//import android.widget.Toolbar;


public class StudentMainActivity extends AppCompatActivity {

    static final int REQUEST_ENABLE_BT=10;
    static final int MESSAGE_READ_BT=0;
    static final int CONNECTING_STATUS_BT=3;
    int mPariedDeviceCount=0;
    Set<BluetoothDevice> mDevices;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket=null;
    OutputStream mOutputStream=null;
    InputStream mInputStream=null;
    Thread mWorkerThread=null;
    private BluetoothSPP bt;
    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;

    private static FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private static FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    public void uploadWeather(WeatherBean weatherBean){
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        dbRef.child("weathers").setValue(weatherBean);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //커스텀한 액션바로 보여주기
        getSupportActionBar().setCustomView(R.layout.custom_bar);  //커스텀한 액션바 가져오기

        checkBluetooth();
        bt = new BluetoothSPP(this);

        final TextView txtDegree=findViewById(R.id.txtDegree);


        /*mFirebaseDatabase.getReference().child("weathers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                WeatherBean bean=dataSnapshot.getValue(WeatherBean.class);
                txtDegree.setText(bean.temp);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mBluetoothHandler=new Handler(){
            public void handleMessage(Message msg){
                if(msg.what==MESSAGE_READ_BT){
                    String readMessage=null;
                    String readMessage1;
                    String readMessage2;
                    try{
                        readMessage=new String((byte[])msg.obj,"UTF-8");
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    WeatherBean weatherBean=new WeatherBean();
                    int index=readMessage.indexOf("\r\n");
                    readMessage1=readMessage.substring(0,index);
                    if(!readMessage1.equals("0")){
                        if(!readMessage1.equals("1")){
                            if(!readMessage1.equals("2")){
                                if(!readMessage1.equals("3")){
                                    if(!readMessage1.equals("4")){
                                        if(!readMessage1.equals("5")){
                                            if(!readMessage1.equals("6")){
                                                if(!readMessage1.equals("7")){
                                                    if(!readMessage1.equals("8")){
                                                        if(!readMessage1.equals("9")){
                                                            txtDegree.setText(readMessage1);
                                                            weatherBean.temp=readMessage1;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    uploadWeather(weatherBean);
                }
            }
        };

        Button mBtnToiletCheck=findViewById(R.id.btnToiletCheck);
        Button mBtnToiletProblem=findViewById(R.id.btnToiletProblem);
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

        mBtnToiletProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ToiletProblemActivity.class);
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
    public boolean onCreateOptionsMenu(Menu menu) {
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
    BluetoothDevice getDeviceFromBondedList(String name){
        BluetoothDevice selectedDevice=null;
        for(BluetoothDevice device:mDevices){
            if(name.equals(device.getName())){
                selectedDevice=device;
                break;
            }
        }
        return selectedDevice;
    }

    void connetToSelectedDevice(String selectedDeviceName){
        mRemoteDevice=getDeviceFromBondedList(selectedDeviceName);
        UUID uuid=java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try{
            mSocket=mRemoteDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(CONNECTING_STATUS_BT,1,-1).sendToTarget();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"블루투스 연결 중 오류가 발생했습니다.",Toast.LENGTH_LONG).show();
            //finish();
        }
    }

    void selectDevice(){
        mDevices=mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount=mDevices.size();

        if(mPariedDeviceCount==0){
            Toast.makeText(getApplicationContext(),"페어링된 장치가 없습니다.",Toast.LENGTH_LONG).show();
            //finish();
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        List<String> listItems=new ArrayList<String>();
        for(BluetoothDevice device:mDevices){
            listItems.add(device.getName());
        }
        listItems.add("취소");

        final CharSequence[]items=listItems.toArray(new CharSequence[listItems.size()]);
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(item==mPariedDeviceCount){
                    Toast.makeText(getApplicationContext(),"연결할 장치를 선택하지 않았습니다.",Toast.LENGTH_LONG).show();
                    //finish();
                }else{
                    connetToSelectedDevice(items[item].toString());
                }
            }
        });
        builder.setCancelable(false);
        AlertDialog alert=builder.create();
        alert.show();
    }

    void checkBluetooth(){
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            Toast.makeText(getApplicationContext(),"기기가 블루투스를 지원하지 않습니다.",Toast.LENGTH_LONG).show();
            //finish();
        }else{
            if(!mBluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"현재 블루투스가 비활성 상태입니다.",Toast.LENGTH_LONG).show();
                Intent enableBtIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
            }else
                selectDevice();
        }
    }

    public void onBackPressed() {
        try{
            mWorkerThread.interrupt();
            mInputStream.close();
            mSocket.close();
            mThreadConnectedBluetooth.cancel();
        }catch (Exception e){}
        super.onBackPressed();
        bt.stopService(); //블루투스 중지
        bt.disconnect();
        mBluetoothAdapter.isEnabled();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        }
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(requestCode==RESULT_OK){
                    selectDevice();
                }else if(resultCode==RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다.", Toast.LENGTH_LONG).show();
                    //finish();
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(MESSAGE_READ_BT, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
