package com.tyb.xd.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.tyb.xd.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.ac_grade)
public class GradeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        x.view().inject(this);
    }
}
