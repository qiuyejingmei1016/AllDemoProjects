package com.yyh.testdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joanzapata.pdfview.PDFView;

public class MainActivity extends AppCompatActivity {

    private PDFView mPDFView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mPDFView = (PDFView) findViewById(R.id.pdf_view);
        mPDFView.fromAsset("Android.pdf").load();
    }
}
