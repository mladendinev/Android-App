package database.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mladen on 9/28/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "reviewApp.db";
    public static final String TABLE_USER = "user";
    public static final String USER_COLUMN_USERNAME = "username";
    public static final String USER_COLUMN_PASSWORD = "password";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_ID = "_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //        private static final String DICTIONARY_TABLE_NAME = "dictionary";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + " (" +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                USER_COLUMN_USERNAME + " TEXT," +
                USER_COLUMN_PASSWORD + " TEXT," +
                USER_COLUMN_EMAIL + " TEXT" + ")");

        Log.v("Data", "Database tables created");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }


    public void insertUser(String username, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_USERNAME, username);
        contentValues.put(USER_COLUMN_PASSWORD, password);
        contentValues.put(USER_COLUMN_EMAIL, email);
        db.insert(TABLE_USER, null, contentValues);
        db.close();

        Log.d("Data", "New user inserted into sqlite");
    }

//    public boolean updateUser(Integer id, String name) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(USER_COLUMN_NAME, name);
//        db.update(USER_TABLE_NAME, contentValues, REVIEW_COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
//        return true;
//    }

    public Cursor getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TABLE_USER + " WHERE " +
                USER_COLUMN_ID + "=?", new String[] { Integer.toString(id) } );
        return res;
    }

    public void deleteUserData(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
    }
}
