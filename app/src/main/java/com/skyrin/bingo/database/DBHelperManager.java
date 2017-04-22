package com.skyrin.bingo.database;

import android.content.Context;

/**
 * Created by 罗延林 on 2016/10/8 0008.
 */

public class DBHelperManager {

    static DBHelper dbHelper = null;

    public synchronized static DBHelper getDBHelper(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context,
                    DBHelper.DATABASE_NAME, null, DBHelper.DATABASE_VERSION);
        }
        return dbHelper;
    }

    public synchronized static void closeDB() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        dbHelper = null;
    }
}
