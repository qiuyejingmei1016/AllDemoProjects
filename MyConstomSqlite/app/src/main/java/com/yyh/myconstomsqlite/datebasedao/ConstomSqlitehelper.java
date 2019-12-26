package com.yyh.myconstomsqlite.datebasedao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.yyh.myconstomsqlite.Config;

/**
 * 数据库版本升级:(对于没有安装过之前版本来说)
 * （1）执行旧版数据库sql语句,则后面还要调用onUpgrade
 * （2）执行新版数据库sql语句，不需要调用onUpgrade
 */

public class ConstomSqlitehelper extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "test.db";

    private static ConstomSqlitehelper mInstance = null;

    private static final int mVersion = 1; //1.0
    private static final int mNewVersion = 2; //2.0
    private static final int mNewSqlVersion = 3; //3.0

    //第一条数据表
    public static final String Collection_Table = "Collection";
    private String CREATE_COLLECTION_TABLE = "create table "
            + Collection_Table + "(_id integer primary key autoincrement, "
            + Config.TYPE + " varchar,"
            + Config.TITLE + " varchar,"
            + Config.CONTENT + " varchar,"
            + Config.NEW_ADD + " varchar,"
            + Config.NEW_ADD_TEST + " varchar,"
            + Config.ADD_LOVE + " varchar,"
            + Config.TIME + " varchar)";

    //第二条数据表
    public static final String Display_Table = "Display";
    private String CREATE_DISPLAY_TABLEE = "create table "
            + Display_Table + "(_id integer primary key autoincrement,"
            + Config.SERIAL_NUMBER + " varchar,"
            + Config.NAME + "name varchar,"
            + Config.DATE + " varchar,"
            + Config.COUNT + " varchar)";

    public ConstomSqlitehelper(Context context) {
        super(context, DATA_BASE_NAME, null, mNewSqlVersion);
    }

    public static ConstomSqlitehelper getInstance(Context context) {
//        if (mInstance == null) {
//            mInstance = new ConstomSqlitehelper(context);
//        }
        if (mInstance == null) {
            synchronized (ConstomSqlitehelper.class) {
                if (mInstance == null)
                    mInstance = new ConstomSqlitehelper(context);
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        int version = sqLiteDatabase.getVersion();
        if (0 == version) {
            sqLiteDatabase.execSQL(CREATE_COLLECTION_TABLE);
            sqLiteDatabase.execSQL(CREATE_DISPLAY_TABLEE);
            Log.e("====onCreate", "sqlite create  sqlVersion:" + version);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("===sqlite  update", " ---> oldVersion:" + oldVersion + "  newVersion:" + newVersion);
        switch (oldVersion) {
            case mVersion:
                upgradeToVersion1(db);
            case mNewVersion:
                upgradeToVersion2(db);
            default:
                break;
        }
    }

    //1.0--->3.0    upgradeToVersion1(db);       upgradeToVersion2(db);因为没有break
    //1.0--->2.0
    private void upgradeToVersion1(SQLiteDatabase db) {
        //新增字段
        String sql = "alter TABLE " + Collection_Table + " add column " + Config.NEW_ADD + " varchar";
        db.execSQL(sql);
        sql = "alter TABLE " + Collection_Table + " add column " + Config.NEW_ADD_TEST + " varchar";
        db.execSQL(sql);
    }

    //2.0--->3.0
    private void upgradeToVersion2(SQLiteDatabase db) {
        String sql = "alter TABLE " + Collection_Table + " add column " + Config.ADD_LOVE + " varchar";
        db.execSQL(sql);
    }
}