//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import world.bentobox.tasks.database.adapters.TaskAdapter;


/**
 * Main task processing object.
 */
@JsonAdapter(TaskAdapter.class)
public abstract class Task
{
    /**
     * This method is called on task starting.
     * Allows to enable listeners.
     */
    public abstract void onStart();


    /**
     * This method is called on task stopping.
     * Allows to disable listeners.
     */
    public abstract void onStop();


// ---------------------------------------------------------------------
// Section: Getters and setters
// ---------------------------------------------------------------------


    /**
     * Gets task id.
     *
     * @return the task id
     */
    public String getTaskId()
    {
        return taskId;
    }


    /**
     * Sets task id.
     *
     * @param taskId the task id
     */
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Owner TaskId Object.
     */
    @Expose
    private String taskId;
}
