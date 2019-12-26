package com.yyh.myconstomsqlite;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.yyh.myconstomsqlite.datebasedao.ConstomSqlitehelper;
import com.yyh.myconstomsqlite.datebasedao.SqliteOperateUtils;
import com.yyh.myconstomsqlite.model.CollectionModel;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SqliteOperateUtils mSqliteOperateUtils;
    private ListView mListView;
    private ShowListAdapter mShowListAdapter;

    private ShowListAdapter getShowListAdapter() {
        if (mShowListAdapter == null) {
            mShowListAdapter = new ShowListAdapter(this);
        }
        return mShowListAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mSqliteOperateUtils = new SqliteOperateUtils(this);
    }

    private void initView() {
        findViewById(R.id.sava).setOnClickListener(this);
        findViewById(R.id.get).setOnClickListener(this);
        findViewById(R.id.updata).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);
        findViewById(R.id.export).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(getShowListAdapter());
    }

    private void setDataList(List<CollectionModel> list) {
        ShowListAdapter adapter = getShowListAdapter();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sava) {
            for (int i = 0; i < 10; i++) {
                mSqliteOperateUtils.savaCollectionData("type" + i, "title" + i, "content" + i, "time" + i);
            }
        } else if (viewId == R.id.get) {
            List list = mSqliteOperateUtils.getCollectionData2();
            if (list != null && !list.isEmpty()) {
                setDataList(list);
            }
        } else if (viewId == R.id.updata) {
            mSqliteOperateUtils.updataCollection("content" + 1, "content" + 666);

        } else if (viewId == R.id.delete) {
            mSqliteOperateUtils.deleteSpecificData("title" + 3);
        } else if (viewId == R.id.export) {
            onGetDataAction();
        }
    }

    private boolean onGetDataAction() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                copyDatabase(this, ConstomSqlitehelper.DATA_BASE_NAME, "testdb.txt");
                Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
                return true;
            } catch (Exception e) {
                Log.e("======", "Get data failed", e);
                Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }


    private void copyDatabase(Activity activity, String dbName, String copyDbName) {
        File databaseFile = activity.getDatabasePath(dbName);
        copyDatabase(activity, databaseFile, copyDbName);
    }

    private void copyDatabase(Activity activity, File databaseFile, String copyDbName) {
        if (databaseFile.exists()) {
            File copyFile = new File(Environment.getExternalStorageDirectory(), copyDbName);
            File parentFile = copyFile.getParentFile();
            parentFile.mkdirs();
            CommonUtils.copyFile(databaseFile, copyFile);
        }
    }
}
