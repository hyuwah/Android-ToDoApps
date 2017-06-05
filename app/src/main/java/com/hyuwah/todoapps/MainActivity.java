package com.hyuwah.todoapps;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    final Context c = this;
    final List<Task> taskToDelete = new ArrayList<>();

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);


        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);
        /**
         * FAB Input add
         */

        final com.getbase.floatingactionbutton.FloatingActionButton fab =
                (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add);
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
                                addTask(taskInput.getText().toString());
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


        /**
         * FAB Input delete
         */

        final com.getbase.floatingactionbutton.FloatingActionButton fabdelete =
                (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_delete);
        fabdelete.startAnimation(animation);
        fabdelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput
                        .setMessage(String.format("Are you sure you want to delete %d tasks?", taskToDelete.size()))
                        .setCancelable(true)
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                //DELETE
                                if (!taskToDelete.isEmpty()) {
                                    // Loop delete tasktoDelete
                                    for (int i = 0; i < taskToDelete.size(); i++) {
                                        deleteTask(taskToDelete.get(i).getTask());
                                    }

                                    if (taskToDelete.size() == 1) {
                                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                                String.format("%s successfuly deleted", taskToDelete.get(0)._task),
                                                Snackbar.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        Snackbar.make(findViewById(R.id.coordinator_layout),
                                                String.format("%d tasks successfuly deleted", taskToDelete.size()),
                                                Snackbar.LENGTH_SHORT)
                                                .show();
                                    }

                                    taskToDelete.clear(); // all deleted, clear list

                                    // Clear all checkboxes
                                    RecyclerView rv = (RecyclerView) findViewById(R.id.my_recycler_view);
                                    for (int j = 0; j < rv.getChildCount(); j++) {
                                        View item = rv.getChildAt(j);
                                        CheckBox cBox = (CheckBox) item.findViewById(R.id.cbTask);
                                        cBox.setChecked(false);
                                    }

                                } else {
                                    Toast.makeText(c, "Nothing to delete", Toast.LENGTH_SHORT).show();
                                }
                                fabdelete.setVisibility(View.INVISIBLE);
                            }
                        })

                        .setPositiveButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });


                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();

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
                com.getbase.floatingactionbutton.FloatingActionButton fabdelete =
                        (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_delete);

                Task task = taskList.get(position); // Get task id

                CheckBox cbTask = (CheckBox) view.findViewById(R.id.cbTask);

                if (cbTask.isChecked()) {//Unselect


                    //Remove from listChecked
                    taskToDelete.remove(task);

                    cbTask.setChecked(false);

                } else if (!cbTask.isChecked()) {//Select


                    // Add to listChecked
                    // taskToDelete.clear();
                    taskToDelete.add(task);

                    cbTask.setChecked(true);
                    //Toast.makeText(getApplicationContext(), task.getTask() + " is selected!", Toast.LENGTH_SHORT).show();
                }

                if (taskToDelete.size() == 0) {
                    fabdelete.setVisibility(View.INVISIBLE);
                } else if (taskToDelete.size() == 1 && fabdelete.getVisibility() == View.INVISIBLE ) {
                    fabdelete.setVisibility(View.VISIBLE);
                    fabdelete.startAnimation(animation);
                } else {
                    fabdelete.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onLongClick(View view, int position) {
                final int pos = position;
                final Task task = taskList.get(pos);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                alertDialogBuilderUserInput
                        .setCancelable(true)
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                final AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(c);
                                confirmationDialog
                                        .setMessage("Are you sure you want to delete this tasks?")
                                        .setCancelable(true)
                                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {

                                                Snackbar.make(findViewById(R.id.coordinator_layout),
                                                        String.format("%s successfuly deleted", task._task),
                                                        Snackbar.LENGTH_SHORT)
                                                        .show();

                                                deleteTask(task.getTask());

                                                taskToDelete.clear(); // all deleted, clear list

                                                // Clear all checkboxes
                                                RecyclerView rv = (RecyclerView) findViewById(R.id.my_recycler_view);
                                                for (int j = 0; j < rv.getChildCount(); j++) {
                                                    View item = rv.getChildAt(j);
                                                    CheckBox cBox = (CheckBox) item.findViewById(R.id.cbTask);
                                                    cBox.setChecked(false);
                                                }

                                                fabdelete.setVisibility(View.INVISIBLE);
                                            }
                                        })
                                        .setPositiveButton("No",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogBox, int id) {
                                                dialogBox.cancel();
                                            }
                                        });

                                AlertDialog confirmationDial = confirmationDialog.create();
                                confirmationDial.show();

                            }
                        })
                        .setMessage(String.format("%s selected", task._task))
                        .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {

                                editTask(task._id);
                                Toast.makeText(c, "Edited", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });


                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        }));

        // FAB HIDE ON SCROLL
        final FloatingActionsMenu fabmenu = (FloatingActionsMenu) findViewById(R.id.fabmenu);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollDist = 0;
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
                } else if (!isVisible && scrollDist < -MINIMUM) {
                    show();
                    scrollDist = 0;
                    isVisible = true;

                }

                if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                    scrollDist += dy;
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
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
//                    addTask();
//                    handled = true;
//                }
//                return handled;
//            }
//        });
        /**
        * Button intro
        */
        final Button button = (Button) findViewById(R.id.btnRemoveCardIntro);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeIntro();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Tap back again to exit",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
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

    public void editTask(final int taskId) {
        final DatabaseHandler db = new DatabaseHandler(this);

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
        final TextView taskInputTV = (TextView) mView.findViewById(R.id.dialogTitle);
        taskInputTV.setText("Edit Task");
        final EditText taskInput = (EditText) mView.findViewById(R.id.eTTask);
        final Task tobeUpdated = db.getTodo(taskId);
        taskInput.setText(tobeUpdated.getTask());
        taskInput.setSelection(taskInput.length());
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
        alertDialogBuilderUserInput.setView(mView);
//        alertDialogBuilderUserInput.setTitle("Edit");
        alertDialogBuilderUserInput.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {


                        if (taskInput.getText().toString() != null) {
                            Log.d("Debug: ", taskInput.getText().toString());
                            tobeUpdated._task = taskInput.getText().toString();

                            db.updateTodo(tobeUpdated);

                            taskList.clear();
                            List<Task> todos = db.getAllTodos();
                            taskList.addAll(todos);
                            mAdapter.notifyDataSetChanged();

                        }

                        taskToDelete.clear();
                        // Clear all checkboxes
                        RecyclerView rv = (RecyclerView) findViewById(R.id.my_recycler_view);
                        for (int j = 0; j < rv.getChildCount(); j++) {
                            View item = rv.getChildAt(j);
                            CheckBox cBox = (CheckBox) item.findViewById(R.id.cbTask);
                            cBox.setChecked(false);
                        }
                        com.getbase.floatingactionbutton.FloatingActionButton fabdelete =
                                (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_delete);
                        fabdelete.setVisibility(View.INVISIBLE);
                    }
                });
        alertDialogBuilderUserInput.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        alertDialogBuilderUserInput.create();
        alertDialogBuilderUserInput.show();


    }


    public void addTask(String taskInput) {

        String task = taskInput;
        if (!task.isEmpty()) {

            DatabaseHandler db = new DatabaseHandler(this);

            String timeStamp = new SimpleDateFormat("EEE, dd/MM/yy - HH:mm:ss").format(Calendar.getInstance().getTime());


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
//                    Snackbar.make(findViewById(R.id.coordinator_layout),
//                            String.format("%s successfuly deleted", td.getTask()),
//                            Snackbar.LENGTH_SHORT)
//                            .show();

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


    public void removeIntro (){
        CardView cv1 = (CardView) findViewById(R.id.cvIntro);
        ViewGroup parent = (ViewGroup) cv1.getParent();
        parent.removeView(cv1);
    }

}
