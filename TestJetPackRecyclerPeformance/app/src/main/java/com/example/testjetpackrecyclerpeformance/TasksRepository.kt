package com.example.testjetpackrecyclerpeformance

class TasksRepository {
    private val tasks = arrayListOf<Task>()

    fun getAllData(): List<Task> {

        for (i in 1..10000) {
            var task: Task = Task()
            tasks.add(task)
        }
        return tasks
    }
}