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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohackathon_team3.Bean.ClassroomBean;
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

public class ClassroomCheckActivity extends AppCompatActivity {

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

    String choice = "";
    String choice2 = "";

    public void uploadClassroom(ClassroomBean classroomBean){
        DatabaseReference dbRef = mFirebaseDatabase.getReference();
        dbRef.child("classrooms").child( classroomBean.classNum ).setValue(classroomBean);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_check);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //커스텀한 액션바로 보여주기
        getSupportActionBar().setCustomView(R.layout.custom_bar);  //커스텀한 액션바 가져오기

        checkBluetooth();
        bt = new BluetoothSPP(this);

        final TextView txtClassroom1_2_201=findViewById(R.id.classroom1_2_201);
        String classroomNum;
        classroomNum="Classroom1_2_201";
        mFirebaseDatabase.getReference().child("classrooms").child(classroomNum).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassroomBean bean=dataSnapshot.getValue(ClassroomBean.class);
                if(bean.isFull){
                    txtClassroom1_2_201.setText("재실중");
                }else{
                    txtClassroom1_2_201.setText("0");
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
                  ClassroomBean classroomBean=new ClassroomBean();
                  int index=readMessage.indexOf("\r");
                  readMessage1=readMessage.substring(0,index);
                  if(readMessage1.equals("1")){
                      //Toast.makeText(ClassroomCheckActivity.this,"재실중",Toast.LENGTH_LONG).show();
                      txtClassroom1_2_201.setText("재실중");
                      classroomBean.classNum="Classroom1_2_201";
                      classroomBean.isFull=true;
                      uploadClassroom(classroomBean);
                  } else if(readMessage1.equals("0")){
                      //Toast.makeText(ClassroomCheckActivity.this,"0",Toast.LENGTH_LONG).show();
                      txtClassroom1_2_201.setText("0");
                      classroomBean.classNum="Classroom1_2_201";
                      classroomBean.isFull=false;
                      uploadClassroom(classroomBean);
                  }
              }
          }
        };

        // 강의실 재실 목룍 표
        final TableLayout tableLayout1_2 = (TableLayout) findViewById(R.id.table1_2);
        final TableLayout tableLayout1_3 = (TableLayout) findViewById(R.id.table1_3);
        final TableLayout tableLayout1_4 = (TableLayout) findViewById(R.id.table1_4);
        final TableLayout tableLayout1_5 = (TableLayout) findViewById(R.id.table1_5);

        // '재실중' 제어
        final TextView text1_4_408 = (TextView) findViewById(R.id.classroom1_4_408);


        // 건물명 spinner
        Spinner dropdown = (Spinner) findViewById(R.id.sprState);
        //String[] items = new String[]{"50주년 기념관", "인문사회관", "제1과학관", "제2과학관", "조형예술관", "중앙도서관", "학생누리관"};
        final ArrayList<String> items = new ArrayList<>();
        //Arrays.asList("50주년 기념관", "인문사회관", "제1과학관", "제2과학관", "조형예술관", "중앙도서관", "학생누리관");
        items.add("50주년 기념관");
        items.add("인문사회관");
        items.add("제1과학관");
        items.add("제2과학관");
        items.add("조형예술관");
        items.add("중앙도서관");
        items.add("학생누리관");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ClassroomCheckActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        // 건물명 spinner 선택 시
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), items.get(i) + "이 선택 되었습니다.", Toast.LENGTH_SHORT ).show();
                choice = items.get(i);

                // INVISIBLE 상태에서도 변경 가능 확인 완료.
                // text1_4_408.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        // 층 수 spinner
        Spinner dropdown2 = (Spinner) findViewById(R.id.sprState2);
        //String[] items2 = new String[]{"2층", "3층", "4층", "5층"};
        final ArrayList<String> items2 = new ArrayList<>();
        items2.add("2층");
        items2.add("3층");
        items2.add("4층");
        items2.add("5층");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ClassroomCheckActivity.this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);     // dropdown -> dropdown2

        // 층 수 spinner 선택 시
        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), items2.get(i) + "이 선택 되었습니다.", Toast.LENGTH_SHORT ).show();
                choice2 = items2.get(i);

                //
                System.out.println("choice1" + choice);
                System.out.println("choice2" + choice2);

                // 추후, 첫 번째 spinner에도 적용 고려
                // 50주년 기념관에 한해서만 작업 진행 (나머지 건물도 동일 시)
                if(choice == "50주년 기념관")
                {
                    if(choice2 == "2층")
                    {
                        tableLayout1_2.setVisibility(View.VISIBLE);
                        tableLayout1_3.setVisibility(View.INVISIBLE);
                        tableLayout1_4.setVisibility(View.INVISIBLE);
                        tableLayout1_5.setVisibility(View.INVISIBLE);
                    }
                    else if(choice2 == "3층")
                    {
                        tableLayout1_2.setVisibility(View.INVISIBLE);
                        tableLayout1_3.setVisibility(View.VISIBLE);
                        tableLayout1_4.setVisibility(View.INVISIBLE);
                        tableLayout1_5.setVisibility(View.INVISIBLE);
                    }
                    else if(choice2 == "4층")
                    {
                        tableLayout1_2.setVisibility(View.INVISIBLE);
                        tableLayout1_3.setVisibility(View.INVISIBLE);
                        tableLayout1_4.setVisibility(View.VISIBLE);
                        tableLayout1_5.setVisibility(View.INVISIBLE);
                    }
                    else if(choice2 == "5층")
                    {
                        tableLayout1_2.setVisibility(View.INVISIBLE);
                        tableLayout1_3.setVisibility(View.INVISIBLE);
                        tableLayout1_4.setVisibility(View.INVISIBLE);
                        tableLayout1_5.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        List<String>listItems=new ArrayList<String>();
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
        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
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
