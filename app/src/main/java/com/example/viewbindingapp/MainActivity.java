package com.example.viewbindingapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.binder_annotations.BindView;
import com.example.binder_annotations.OnClick;
import com.example.viewbinder.Binding;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_view)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Binding.bind(this);
        textView.setText("happy very hapy");
    }

    @OnClick(R.id.text_view)
    public  void onClicktext() {
        textView.setText("bind view working");
    }
}
