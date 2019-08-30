package com.example.arduinohackathon_team3.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arduinohackathon_team3.R;

public class ToiletProblemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_problem);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //커스텀한 액션바로 보여주기
        getSupportActionBar().setCustomView(R.layout.custom_bar);  //커스텀한 액션바 가져오기

        // 추후 firebase
        final TextView txtToiletProblem1_1_1=findViewById(R.id.toiletProblem1_1_1);

        Spinner dropdown = (Spinner) findViewById(R.id.sprState);
        String[] items = new String[]{"50주년 기념관", "인문사회관", "제1과학관", "제2과학관", "조형예술관", "중앙도서관", "학생누리관"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToiletProblemActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = (Spinner) findViewById(R.id.sprState2);
        String[] items2 = new String[]{"1층", "2층 왼쪽", "2층 오른쪽"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ToiletProblemActivity.this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);     // dropdown -> dropdown2
    }
}

