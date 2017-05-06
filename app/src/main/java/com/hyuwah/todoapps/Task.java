package com.hyuwah.todoapps;

import java.util.Date;

/**
 * Created by Wahyu on 05/05/2017.
 * CLASS Task
 */

public class Task {
    //private variables
    int _id;
    String _task;
    String _date;

    // Empty constructor
    public Task() {

    }

    // constructor
    public Task(int id, String task, String date) {
        this._id = id;
        this._task = task;
        this._date = date;
    }

    // constructor
    public Task(String task, String date) {
        this._task = task;
        this._date = date;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting task
    public String getTask() {
        return this._task;
    }

    // setting task
    public void setTask(String task) {
        this._task = task;
    }

    // getting date
    public String getDate() {
        return this._date;
    }

    // setting date
    public void setDate(String date ) {
        this._date = date;
    }

}

