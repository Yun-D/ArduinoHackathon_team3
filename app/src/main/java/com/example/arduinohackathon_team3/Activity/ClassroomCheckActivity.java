package com.example.arduinohackathon_team3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arduinohackathon_team3.R;

import java.util.ArrayList;

public class ClassroomCheckActivity extends AppCompatActivity {

    String choice = "";
    String choice2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom_check);

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
                Toast.makeText(getApplicationContext(), items.get(i) + "가 선택 되었습니다.", Toast.LENGTH_SHORT ).show();
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
                Toast.makeText(getApplicationContext(), items2.get(i) + "가 선택 되었습니다.", Toast.LENGTH_SHORT ).show();
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
}
