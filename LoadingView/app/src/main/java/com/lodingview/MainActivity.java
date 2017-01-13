package com.lodingview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingView = (LoadingView) findViewById(R.id.loading_view);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Hello World",Toast.LENGTH_SHORT).show();
            }
        });
        startLoading();
    }

    Handler handler = new Handler();
    private void startLoading(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingView.startNextAnimation();
            }
        },3000);
    }
}
