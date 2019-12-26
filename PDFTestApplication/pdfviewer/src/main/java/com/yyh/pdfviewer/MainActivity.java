package com.yyh.pdfviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

public class MainActivity extends AppCompatActivity implements OnPageChangeListener {

    private PDFView mPdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mPdfView = (PDFView) findViewById(R.id.pdf_view);
//        mPdfView.fromFile(new File(localPdfPath))  //pdf地址
//                .defaultPage(1)//默认页面
//                .enableDoubletap(true)
//                .swipeHorizontal(true)//是不是横向查看
//                .onPageChange(this)
//                .enableSwipe(true)
//                .load();

        mPdfView.fromAsset("test.pdf")   //设置pdf文件地址
                .enableDoubletap(true)
                .swipeHorizontal(false)
                .onPageChange(this)  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                .enableSwipe(true)   //是否允许翻页，默认是允许翻页
                // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        Toast.makeText( MainActivity.this , "page= " + page +
                " pageCount= " + pageCount , Toast.LENGTH_SHORT).show();
    }
}
