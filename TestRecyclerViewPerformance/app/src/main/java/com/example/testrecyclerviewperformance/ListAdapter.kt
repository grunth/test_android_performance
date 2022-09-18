package com.example.testrecyclerviewperformance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(
    t: List<Task>,
    private val activity: AppCompatActivity
) :
    RecyclerView.Adapter<TaskListViewHolder>() {

    private var tasks: List<Task> = t

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        return TaskListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_task, parent, false) as CardView,
            activity
        )
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.onBindViewHolder()
    }

    override fun getItemCount() = tasks.size
}