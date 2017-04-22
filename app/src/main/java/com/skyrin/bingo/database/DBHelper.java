package com.skyrin.bingo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.skyrin.bingo.common.log.LogUtil;
import com.skyrin.bingo.database.dal.DMsg;

/**
 * Created by 罗延林 on 2016/9/28 0028.
 * 数据库
 */

public class DBHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "bingo.db";
    /**
     * 数据库版本
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * SQLiteDatabase
     */
    private static SQLiteDatabase database;


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 创建消息列表
     */
    String create_msg = "CREATE TABLE IF NOT EXISTS " + DMsg.TABLE_NAME + "("
            + DMsg.TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + DMsg.TABLE_COLUMN_TEXT_MSG + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_msg);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    /**
     * 获取一个可写数据库对象
     *
     * @return
     */
    public SQLiteDatabase getDatabase() {
        if (database == null) {
            database = this.getWritableDatabase();
        }
        return database;
    }

    /**
     * @param table  要操作的表
     * @param values 此map含行的初始列值。键应该是列名称和列值的值
     *               可选的；可能是空的。SQL不允许插入一个空的行没有指定至少一个列名称。如果您的提供的值是空的，没有列名称是已知的，
     *               并且不能插入空行
     *               。如果没有设置为null，这nullcolumnhack参数为空列的名称明确地插入一个空成在你的价值观是空的。
     * @return long 新插入行的id,如果是-1则表示插入失败
     */
    public long insert(String table, ContentValues values) {
        SQLiteDatabase db = getDatabase();
        return db.insert(table, null, values);
    }

    /**
     * 通过条件语句查询数据表
     *
     * @param sql
     * @param selectionArgs
     * @return 结果集游标
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getDatabase();
            cursor = db.rawQuery(sql, selectionArgs);
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e(e.getMessage());
        }
        return cursor;
    }

    /**
     * 删除行的操作
     *
     * @param table       操作的表名
     * @param whereClause 删除的条件 “username=?”
     * @param whereArgs   删除的条件参数 “john”
     * @return int 受到影响的行数
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getDatabase();
        return db.delete(table, whereClause, whereArgs);
    }

    /**
     * 更新操作
     *
     * @param table       要操作的表名
     * @param values      ContentValues cv = new ContentValues();
     *                    cv.put("password","iHatePopMusic"); 添加要更改的字段及内容
     * @param whereClause "username=?" 修改条件
     * @param whereArgs   {"Jack Johnson"} 修改条件的参数
     * @return int 受到影响的行数
     */
    public int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {
        SQLiteDatabase db = getDatabase();
        return db.update(table, values, whereClause, whereArgs);
    }

    /**
     * 执行一条sql命令
     * @param sql
     */
    public void exeSql(String sql){
        SQLiteDatabase db = getDatabase();
        db.execSQL(sql);
    }

}
