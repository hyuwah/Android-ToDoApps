package com.hyuwah.todoapps;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

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
    final Context c = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        /**
         * FAB Input add
         */

        final com.getbase.floatingactionbutton.FloatingActionButton fab = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add);
        fab.startAnimation(animation);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput.setView(mView);

                final EditText taskInput = (EditText) mView.findViewById(R.id.eTTask);
                alertDialogBuilderUserInput
                        .setCancelable(true)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                setTask(taskInput.getText().toString());
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();


            }
        });

        final List<Task> taskToDelete = new ArrayList<>();
        /**
         * FAB Input delete
         */

        final com.getbase.floatingactionbutton.FloatingActionButton fabdelete = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_delete);
        fabdelete.startAnimation(animation);
        fabdelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!taskToDelete.isEmpty()) {
                    deleteTask(taskToDelete.get(0).getTask());
                    fabdelete.setVisibility(View.INVISIBLE);
                }else{
                    Toast.makeText(c, "Nothing to delete", Toast.LENGTH_SHORT).show();
                    fabdelete.setVisibility(View.INVISIBLE);
                }
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
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        // Biar yang atas yang paling baru
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        // TOUCH LISTENER
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Task task = taskList.get(position);
                Toast.makeText(getApplicationContext(), task.getTask() + " is selected!", Toast.LENGTH_SHORT).show();

                com.getbase.floatingactionbutton.FloatingActionButton fabdelete = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_delete);

                fabdelete.setVisibility(View.VISIBLE);
                fabdelete.startAnimation(animation);

                taskToDelete.clear();
                taskToDelete.add(task);
            }

            @Override
            public void onLongClick(View view, int position) {
                Task task = taskList.get(position);
                deleteTask(task.getTask());
            }
        }));

        // FAB HIDE ON SCROLL
        final FloatingActionsMenu fabmenu = (FloatingActionsMenu) findViewById(R.id.fabmenu);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {   int scrollDist = 0;
            boolean isVisible = true;
            static final float MINIMUM = 25;


            public void show() {
                fabmenu.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            public void hide() {
                fabmenu.collapse();
                fabmenu.animate().translationY(fabmenu.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isVisible && scrollDist > MINIMUM) {
                    hide();
                    scrollDist = 0;
                    isVisible = false;
                }
                else if (!isVisible && scrollDist < -MINIMUM) {
                    show();
                    scrollDist = 0;
                    isVisible = true;

                }

                if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                    scrollDist += dy;
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    fabmenu.setVisibility(View.VISIBLE);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        taskList.clear();
        taskList.addAll(todos);


//        eTTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                boolean handled = false;
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
//                    setTask();
//                    handled = true;
//                }
//                return handled;
//            }
//        });

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


    public void setTask(String taskInput) {

        String task = taskInput;
        if (!task.isEmpty()) {

            DatabaseHandler db = new DatabaseHandler(this);

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());


            db.addTodo(new Task(task, timeStamp));

            taskList.clear();
            List<Task> listTodos = db.getAllTodos();
            taskList.addAll(listTodos);
            Log.d("Result:", Integer.toString(db.getTodoByName(task)._id));

            mAdapter.notifyItemInserted(listTodos.size());
            recyclerView.scrollToPosition(listTodos.size() - 1);
            Snackbar.make(findViewById(R.id.coordinator_layout),
                    String.format("%s successfuly added", task),
                    Snackbar.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(this, "Input task first!", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteTask(String taskInput) {


        String task = taskInput;
        if (!task.isEmpty()) {
            try {
                DatabaseHandler db = new DatabaseHandler(this);
                Task td = db.getTodoByName(task);

                Log.d("Result:", td.getID() + " " + td.getTask());

                if (td.getID() >= 0) {
                    String id = Integer.toString(td.getID());
                    taskList.clear();
                    Log.d("Debug is empty ", Boolean.toString(taskList.isEmpty()));
                    db.deleteTodo(td);
                    Log.d("Debug:", id + " " + td.getTask() + " deleted from db (supposedly)");

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
