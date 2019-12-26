package com.yyh.myconstomsqlite.datebasedao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yyh.myconstomsqlite.Config;
import com.yyh.myconstomsqlite.model.CollectionModel;

import java.util.ArrayList;
import java.util.List;

public class SqliteOperateUtils {

    private ConstomSqlitehelper mConSqlitehelper;
    private SQLiteDatabase mWritableDatabase;
    private List<CollectionModel> mAllCollectionLists;

    public SqliteOperateUtils(Context context) {
        mConSqlitehelper = ConstomSqlitehelper.getInstance(context);
        mWritableDatabase = mConSqlitehelper.getWritableDatabase();
    }

    /**
     * 保存数据到数据库
     */
    public void savaCollectionData(String type, String title, String content, String time) {
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
            mWritableDatabase.beginTransaction();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Config.TYPE, type);
                contentValues.put(Config.TITLE, title);
                contentValues.put(Config.CONTENT, content);
                contentValues.put(Config.TIME, time);
                mWritableDatabase.insert(ConstomSqlitehelper.Collection_Table, null, contentValues);
                mWritableDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mWritableDatabase.endTransaction();
                mWritableDatabase.close();
            }
        }
    }

    /**
     * 获取数据库缓存数据
     */
    public List getCollectionData() {
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
            Cursor cursor = mWritableDatabase.query(ConstomSqlitehelper.Collection_Table, null, null, null, null, null, null, null);
            Cursor queryCursor = null;
            try {
                if (cursor.getCount() > 0) {
                    int limitNum;
                    if (cursor.getCount() <= 30) {
                        limitNum = cursor.getCount();
                    } else {
                        limitNum = cursor.getCount() - (cursor.getCount() - 30);
                    }
                    //select * from 表名 limit 0,10;
                    String sql = "select * from " + ConstomSqlitehelper.Collection_Table + " limit " + 0 + "," + limitNum;
                    queryCursor = mWritableDatabase.rawQuery(sql, null);
                    mAllCollectionLists = new ArrayList<CollectionModel>();
                    while (queryCursor.moveToNext()) {
                        String type = queryCursor.getString(queryCursor.getColumnIndex(Config.TYPE));
                        String title = queryCursor.getString(queryCursor.getColumnIndex(Config.TITLE));
                        String content = queryCursor.getString(queryCursor.getColumnIndex(Config.CONTENT));
                        String time = queryCursor.getString(queryCursor.getColumnIndex(Config.TIME));
                        mAllCollectionLists.add(new CollectionModel(type, title, content, time));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != queryCursor)
                    queryCursor.close();
                cursor.close();
            }
        }
        return mAllCollectionLists;
    }

    /**
     * 获取数据库缓存数据
     */
    public List getCollectionData2() {
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
            String columns[] = {Config.TYPE, Config.TITLE, Config.CONTENT, Config.TIME};
            Cursor cursor = mWritableDatabase.query(ConstomSqlitehelper.Collection_Table, columns, null, null, null, null, null, null);
            try {
                mAllCollectionLists = new ArrayList<CollectionModel>();
                while (cursor.moveToNext()) {
                    String type = cursor.getString(cursor.getColumnIndex(Config.TYPE));
                    String title = cursor.getString(cursor.getColumnIndex(Config.TITLE));
                    String content = cursor.getString(cursor.getColumnIndex(Config.CONTENT));
                    String time = cursor.getString(cursor.getColumnIndex(Config.TIME));
                    mAllCollectionLists.add(new CollectionModel(type, title, content, time));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return mAllCollectionLists;
    }

    /**
     * 删除数据库指定数据
     */
    public void deleteSpecificData(String title) {
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
            try {
                String whereClauseString = "title=?";
                String[] whereArgsString = {title};
                int delete = mWritableDatabase.delete(ConstomSqlitehelper.Collection_Table, whereClauseString, whereArgsString);
                Log.e("=============delete", delete + "");
//                writableDatabase.delete(ConstomSqlitehelper.Collection_Table, "_id=?", new String[]{String.valueOf(number)});
                mWritableDatabase.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mWritableDatabase.close();
            }
        }
    }

    /**
     * 清空数据库并重置id
     */
    public void deleteCollectionData() {
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
            try {
                String sql = "DELETE FROM " + ConstomSqlitehelper.Collection_Table + ";";
                mWritableDatabase.execSQL(sql);
                String sqlRevert = "update sqlite_sequence set seq=0 where name='" + ConstomSqlitehelper.Collection_Table + "'";
                mWritableDatabase.execSQL(sqlRevert);
                mWritableDatabase.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mWritableDatabase.close();
            }
        }
    }

    /**
     * 更新数据库某个字段
     */
    public void updataCollection(String content, String replace) {
        if (!content.equals(queryDate(content))) {//先查询 再去更新已读
            return;
        }
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
            try {
                ContentValues values = new ContentValues();
                values.put(Config.CONTENT, replace);
                String whereClauseString = "content=?";
                String[] whereArgsString = {content};//要更新的字段
                mWritableDatabase.update(ConstomSqlitehelper.Collection_Table, values, whereClauseString, whereArgsString);
//                int update = writableDatabase.update(ConstomSqlitehelper.Collection_Table, values, "detail_a=?", new String[]{detialUrl});
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mWritableDatabase.close();
            }
        }
    }

    /**
     * 查询数据库某条数据是否存在
     */
    public String queryDate(String content) {
        synchronized (mConSqlitehelper) {
            if (!mWritableDatabase.isOpen()) {
                mWritableDatabase = mConSqlitehelper.getWritableDatabase();
            }
//            String sql = "select detail_a from NewsShow where detail_a='" + detail_a + "'";
//            Cursor cursor = writableDatabase.rawQuery(sql, null);
            String sql = "select " + Config.CONTENT + " from " + ConstomSqlitehelper.Collection_Table + " where " + Config.CONTENT + "='" + content + "'";
            Cursor cursor = mWritableDatabase.rawQuery(sql, null);
            try {
                while (cursor.moveToNext()) {
                    content = cursor.getString(cursor.getColumnIndex(Config.CONTENT));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
                mWritableDatabase.close();
            }
        }
        return content;
    }
}