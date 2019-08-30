package com.example.arduinohackathon_team3.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohackathon_team3.Bean.ToiletProblemBean;
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

public class ToiletProblemActivity extends AppCompatActivity {
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

    public void uploadToiletProblem(ToiletProblemBean toiletProblemBean){
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        dbRef.child("problems").child( toiletProblemBean.toiletNum ).setValue(toiletProblemBean);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_problem);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //커스텀한 액션바로 보여주기
        getSupportActionBar().setCustomView(R.layout.custom_bar);  //커스텀한 액션바 가져오기

        checkBluetooth();
        bt = new BluetoothSPP(this);

        final TextView txtToiletProblem1_1_3=findViewById(R.id.toiletProblem1_1_3);
        String toiletProblemNum;
        toiletProblemNum="ToiletProblem1_1_3";
        mFirebaseDatabase.getReference().child("problems").child(toiletProblemNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ToiletProblemBean bean=dataSnapshot.getValue(ToiletProblemBean.class);
                if(bean.isProblem){
                    txtToiletProblem1_1_3.setText("고장");
                }else{
                    txtToiletProblem1_1_3.setText("정상");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mBluetoothHandler=new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what==MESSAGE_READ_BT){
                    String readMessage=null;
                    String readMessage1;
                    try{
                        readMessage=new String((byte[])msg.obj,"UTF-8");
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    ToiletProblemBean toiletProblemBean=new ToiletProblemBean();
                    int index=readMessage.indexOf("\r");
                    readMessage1=readMessage.substring(0,index);
                    if(readMessage1.equals("9")){
                        //Toast.makeText(ClassroomCheckActivity.this,"재실중",Toast.LENGTH_LONG).show();
                        txtToiletProblem1_1_3.setText("정상");
                        toiletProblemBean.toiletNum="ToiletProblem1_1_3";
                        toiletProblemBean.isProblem=false;
                        uploadToiletProblem(toiletProblemBean);
                    }else if(readMessage1.equals("8")){
                        //Toast.makeText(ClassroomCheckActivity.this,"재실중",Toast.LENGTH_LONG).show();
                        txtToiletProblem1_1_3.setText("고장");
                        toiletProblemBean.toiletNum="ToiletProblem1_1_3";
                        toiletProblemBean.isProblem=true;
                        uploadToiletProblem(toiletProblemBean);
                    }
                }
            }
        };

        // 추후 firebase


        Spinner dropdown = (Spinner) findViewById(R.id.sprState);
        String[] items = new String[]{"50주년 기념관", "인문사회관", "제1과학관", "제2과학관", "조형예술관", "중앙도서관", "학생누리관"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToiletProblemActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = (Spinner) findViewById(R.id.sprState2);
        String[] items2 = new String[]{"1층", "2층 왼쪽", "2층 오른쪽"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ToiletProblemActivity.this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);     // dropdown -> dropdown2
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

