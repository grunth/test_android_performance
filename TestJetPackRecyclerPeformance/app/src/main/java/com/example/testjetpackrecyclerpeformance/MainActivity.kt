package com.example.testjetpackrecyclerpeformance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val tasksRepository = TasksRepository()
            val getAllData = tasksRepository.getAllData()

            LazyColumn(
            contentPadding = PaddingValues(8.dp)) {
                items(items = getAllData) { t ->
                    TaskItemView(task = t)
                }
            }

        }
    }
}