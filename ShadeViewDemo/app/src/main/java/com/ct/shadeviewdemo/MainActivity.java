package com.ct.shadeviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ct.shadeview.ShadeImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.iv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ShadeImageView)v).getWhere_click()== ShadeImageView.CLICK_SPACE)
                    Toast.makeText(MainActivity.this,"背景点击",Toast.LENGTH_SHORT).show();
                else if(((ShadeImageView)v).getWhere_click()== ShadeImageView.CLICK_RANGE)
                {
                    Toast.makeText(MainActivity.this,"范围点击",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
