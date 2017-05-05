package com.hyuwah.todoapps;

/**
 * Created by Wahyu on 05/05/2017.
 * CLASS Task
 */

public class Task {
    //private variables
    int _id;
    String _task;

    // Empty constructor
    public Task(){

    }
    // constructor
    public Task(int id, String task){
        this._id = id;
        this._task = task;
    }

    // constructor
    public Task(String task){
        this._task = task;
    }
    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getTask(){
        return this._task;
    }

    // setting name
    public void setTask(String task){
        this._task = task;
    }


}

