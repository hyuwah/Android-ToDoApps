package com.hyuwah.todoapps;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;

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

        /**
         * Button listener assignment
         */
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

        final EditText eTTask = (EditText) findViewById(R.id.eTTask);

        /**
         *  DATABASE OPERATIONs
         */
        DatabaseHandler db = new DatabaseHandler(this);

        /**
         * CRUD Operations
         */

        // Reading all contacts
        Log.d("Reading: ", "Reading all tasks..");
        List<Task> todos = db.getAllTodos();

        /**
         * Recyclerview
         */
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mAdapter = new MyAdapter(taskList); // buat connect recyclerView / adapter ke list Tasklist
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Task task = taskList.get(position);
                Toast.makeText(getApplicationContext(), task.getTask() + " is selected!", Toast.LENGTH_SHORT).show();
                eTTask.setText(task.getTask());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        taskList.clear();
        taskList.addAll(todos);
//        for (Task td : todos) {
//            String log = "Id: " + td.getID() + " ,Task: " + td.getTask();
//            // Writing Contacts to log
//            Log.d("Task: ", log);
//
//            taskList.add(td);
//
//        }



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

        String task = eTTask.getText().toString();
        if (!task.isEmpty()) {

            DatabaseHandler db = new DatabaseHandler(this);

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());


            db.addTodo(new Task(task,timeStamp));

            taskList.clear();
            taskList.addAll(db.getAllTodos());
            Log.d("Result:",Integer.toString(db.getTodoByName(task)._id));

            eTTask.setText("");

            Snackbar.make(findViewById(R.id.coordinator_layout),
                    String.format("%s successfuly added", task),
                    Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "Input task first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTask() {
        EditText eTTask = (EditText) findViewById(R.id.eTTask);

        String task = eTTask.getText().toString();
        if (!task.isEmpty()) {
            try {
                DatabaseHandler db = new DatabaseHandler(this);
                Task td = db.getTodoByName(task);

                Log.d("Result:", td.getID()+" "+td.getTask());

                if (td.getID() >= 0) {
                    String id = Integer.toString(td.getID());
                    taskList.clear();
                    Log.d("Debug is empty ", Boolean.toString(taskList.isEmpty()));
                    db.deleteTodo(td);
                    Log.d("Debug:", id+" "+ td.getTask()+" deleted from db (supposedly)");
                    eTTask.setText("");

                    List<Task> todos = db.getAllTodos();
                    taskList.addAll(todos);
                    mAdapter.notifyDataSetChanged(); // update the recyclerview
                    Snackbar.make(findViewById(R.id.coordinator_layout),
                            String.format("%s successfuly deleted", td.getTask()),
                            Snackbar.LENGTH_SHORT)
                            .show();

                } else {
                    Log.d("Error:", String.format("%s doesn't exist in database", task));

                }

            } catch (Exception err) {

                Log.d("Error:", err.toString());
                Toast.makeText(this, String.format("%s doesn't exist in database", task), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Input task first!", Toast.LENGTH_SHORT).show();
        }

    }


}
