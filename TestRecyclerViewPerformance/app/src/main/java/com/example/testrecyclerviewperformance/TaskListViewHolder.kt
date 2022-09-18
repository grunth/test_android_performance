package com.example.testrecyclerviewperformance

import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskListViewHolder(
    private val cardView: CardView,
    private val activity: AppCompatActivity
) : RecyclerView.ViewHolder(cardView) {
    private val fieldContainer: LinearLayout = cardView.findViewById(R.id.task_field_container_single)

    private var fields = createVirtualFields()

    fun onBindViewHolder() {
        fillVirtualFields()
    }

    private fun fillVirtualFields() {
        fillSingleVirtualFields()
    }

    private fun fillSingleVirtualFields() {
    }

    private fun createVirtualFields(): List<VFH> {
        val fields = mutableListOf<VFH>()

        fieldContainer.removeAllViews()
        repeat(5) {
            val textLabeled = getTextLabeledView()
            val vfh = VFH(textLabeled)
            fields.add(vfh)
            fieldContainer.addView(vfh.textLabeled)
        }

        return fields
    }

    private fun getTextLabeledView() = TextLabeled(activity)

    private data class VFH(val textLabeled: TextLabeled)
}