package com.hyuwah.todoapps;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btnSubmitTask = (Button) findViewById(R.id.btnSubmitTask);
        btnSubmitTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTask();
            }
        });

        Button btnDeleteTask = (Button) findViewById(R.id.btnDeleteTask);
        btnDeleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        /**
         *  DATABASE OPERATIONs
         */
        DatabaseHandler db = new DatabaseHandler(this);

        /**
         * CRUD Operations
         * */
        TextView tvTaskList = (TextView) findViewById(R.id.tVTaskList);

        // Reading all contacts
        Log.d("Reading: ", "Reading all tasks..");
        List<Task> todos = db.getAllTodos();
        String allTask = "";

        /**
         * ListView
         */
        ListView lv = (ListView) findViewById(R.id.listView1);
        for (Task td : todos) {
            String log = "Id: " + td.getID() + " ,Task: " + td.getTask();
            // Writing Contacts to log
            Log.d("Task: ", log);

            allTask += td.getTask() + "\n";

//            TextView childTv = new TextView();
//            childTv.setText(td.getTask());
//            lv.addView(childTv);
        }
        tvTaskList.setText(allTask);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTask() {
        EditText eTTask = (EditText) findViewById(R.id.eTTask);
        TextView tVStatus = (TextView) findViewById(R.id.tVTask);
        TextView tvTaskList = (TextView) findViewById(R.id.tVTaskList);

        String task = eTTask.getText().toString();
        if (!task.isEmpty()) {
            tVStatus.setText(String.format("Added task : %s", task));

            DatabaseHandler db = new DatabaseHandler(this);
            db.addTodo(new Task(task));

            List<Task> todos = db.getAllTodos();
            String taskList = "";
            for (Task td : todos) {
                taskList += td._task + "\n";
            }
            tvTaskList.setText(taskList);
            eTTask.setText("");
        } else {
            tVStatus.setText("Input task first!");
        }
    }

    public void deleteTask() {
        EditText eTTask = (EditText) findViewById(R.id.eTTask);
        TextView tVStatus = (TextView) findViewById(R.id.tVTask);
        TextView tvTaskList = (TextView) findViewById(R.id.tVTaskList);

        String task = eTTask.getText().toString();
        if (!task.isEmpty()) {
            try {
                DatabaseHandler db = new DatabaseHandler(this);
                Task td = db.getTodoByName(task);
                String taskTD = td.getTask();
                Log.d("Result:", taskTD);

                if (td.getID() > 0) {
                    db.deleteTodo(td);

                    List<Task> todos = db.getAllTodos();
                    String taskList = "";
                    for (Task tds : todos) {
                        taskList += tds._task + "\n";
                    }
                    tvTaskList.setText(taskList);
                } else {
                    Log.d("Error:", String.format("%d doesn't exist in database", task));
                }

                eTTask.setText("");
                tVStatus.setText(String.format("Deleted task : %s", task));

            } catch (Exception err) {

                Log.d("Error:", err.toString());
                tVStatus.setText(String.format("%s doesn't exist in database", task));
            }
        } else {
            tVStatus.setText("Input task first!");
        }

    }

}
