//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.JsonAdapter;

import world.bentobox.tasks.database.adapters.TaskAdapter;


/**
 * Main task processing object.
 */
@JsonAdapter(TaskAdapter.class)
public interface Task
{
    /**
     * This method is called on task starting.
     * Allows to enable listeners.
     */
    void onStart();


    /**
     * This method is called on task stopping.
     * Allows to disable listeners.
     */
    void onStop();
}
