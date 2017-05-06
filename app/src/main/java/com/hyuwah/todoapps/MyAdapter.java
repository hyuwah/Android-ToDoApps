package com.hyuwah.todoapps;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Wahyu on 05/05/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Task> tasklist;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView task, date;

        public MyViewHolder(View view) {
            super(view);
            task = (TextView) view.findViewById(R.id.task_container);
            date = (TextView) view.findViewById(R.id.date_container);
        }
    }

    public MyAdapter(List<Task> tasklist) {
        this.tasklist = tasklist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task task = tasklist.get(position);
        holder.task.setText(task.getTask());
        holder.date.setText(task.getDate());
    }

    @Override
    public int getItemCount() {
        return tasklist.size();
    }
}

