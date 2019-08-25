package com.example.arduinohackathon_team3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.arduinohackathon_team3.R;

import java.util.ArrayList;

public class OfficeCheckActivity extends AppCompatActivity {

    String choice = "";
    String choice2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_office_check);

        // 강의실 재실 목룍 표
        final TableLayout tableOffice1_1 = (TableLayout) findViewById(R.id.tableOffice1_1);
        final TableLayout tableOffice1_2 = (TableLayout) findViewById(R.id.tableOffice1_2);
        final TableLayout tableOffice1_3 = (TableLayout) findViewById(R.id.tableOffice1_3);


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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(OfficeCheckActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
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
        //String[] items2 = new String[]{"1층", "2층", "3층"};
        final ArrayList<String> items2 = new ArrayList<>();
        items2.add("1층");
        items2.add("2층");
        items2.add("3층");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(OfficeCheckActivity.this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);

        // 층 수 spinner 선택 시
        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), items2.get(i) + "가 선택 되었습니다.", Toast.LENGTH_SHORT ).show();
                choice2 = items2.get(i);

                //
                System.out.println("choice1" + choice);
                System.out.println("choice2" + choice2);

                //
                if(choice == "50주년 기념관")
                {
                    if(choice2 == "1층")
                    {
                        tableOffice1_1.setVisibility(View.VISIBLE);
                        tableOffice1_2.setVisibility(View.INVISIBLE);
                        tableOffice1_3.setVisibility(View.INVISIBLE);
                    }
                    else if(choice2 == "2층")
                    {
                        tableOffice1_1.setVisibility(View.INVISIBLE);
                        tableOffice1_2.setVisibility(View.VISIBLE);
                        tableOffice1_3.setVisibility(View.INVISIBLE);
                    }
                    else if(choice2 == "3층")
                    {
                        tableOffice1_1.setVisibility(View.INVISIBLE);
                        tableOffice1_2.setVisibility(View.INVISIBLE);
                        tableOffice1_3.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
