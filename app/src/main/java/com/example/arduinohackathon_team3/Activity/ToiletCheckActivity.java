package com.example.arduinohackathon_team3.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.arduinohackathon_team3.R;

public class ToiletCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_check);

        Spinner dropdown = (Spinner) findViewById(R.id.sprState);
        String[] items = new String[]{"50주년 기념관", "인문사회관", "제1과학관", "제2과학관", "조형예술관", "중앙도서관", "학생누리관"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToiletCheckActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        Spinner dropdown2 = (Spinner) findViewById(R.id.sprState2);
        String[] items2 = new String[]{"1층", "2층 왼쪽", "2층 오른쪽"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(ToiletCheckActivity.this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown.setAdapter(adapter2);
    }
}