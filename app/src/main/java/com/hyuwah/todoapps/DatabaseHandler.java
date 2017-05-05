package com.hyuwah.todoapps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wahyu on 05/05/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "todoManager";

    // Contacts table name
    private static final String TABLE_TODOS = "todos";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TASK = "todo";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TASK + " TEXT" + ")";
        db.execSQL(CREATE_TODOS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
    void addTodo(Task todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK, todo.getTask()); // Contact Name


        // Inserting Row
        db.insert(TABLE_TODOS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Task getTodo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODOS, new String[] { KEY_ID,
                        KEY_TASK}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Task todo = new Task(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return todos
        return todo;
    }

    // Getting first single contact by name
    Task getTodoByName(String task) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODOS, new String[] { KEY_ID,
                        KEY_TASK}, KEY_TASK + "=?",
                new String[] { String.valueOf(task) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Task todo = new Task(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1));
        // return todos
        return todo;
    }

    // Getting All Todos
    public List<Task> getAllTodos() {
        List<Task> taskListList = new ArrayList<Task>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TODOS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task todo = new Task();
                todo.setID(Integer.parseInt(cursor.getString(0)));
                todo.setTask(cursor.getString(1));
                // Adding contact to list
                taskListList.add(todo);
            } while (cursor.moveToNext());
        }

        // return contact list
        return taskListList;
    }

    // Updating single contact
    public int updateTodo(Task todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK, todo.getTask());

        // updating row
        return db.update(TABLE_TODOS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getID()) });
    }

    // Deleting single contact
    public void deleteTodo(Task todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODOS, KEY_ID + " = ?",
                new String[] { String.valueOf(todo.getID()) });
        db.close();
    }


    // Getting contacts Count
    public int getTodosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TODOS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}
