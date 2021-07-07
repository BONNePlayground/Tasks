//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.managers;


import world.bentobox.tasks.TasksAddon;


/**
 * Main class that manages Tasks Addon data.
 */
public class TasksManager
{
    /**
     * Instantiates a new Tasks manager.
     *
     * @param addon the tasks addon instance.
     */
    public TasksManager(TasksAddon addon)
    {
        this.addon = addon;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    /**
     * Instance of Tasks Addon class.
     */
    private final TasksAddon addon;
}
