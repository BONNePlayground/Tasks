//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.managers;


import java.util.HashMap;
import java.util.Map;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskBundleObject;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.utils.Constants;
import world.bentobox.tasks.utils.Utils;


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

        this.taskDatabase = new Database<>(addon, TaskObject.class);
        this.taskCache = new HashMap<>();

        this.taskDataDatabase = new Database<>(addon, TaskDataObject.class);
        this.taskDataCache = new HashMap<>();

        this.taskBundleDatabase = new Database<>(addon, TaskBundleObject.class);
        this.taskBundleCache = new HashMap<>();
    }


// ---------------------------------------------------------------------
// Section: Database related methods
// ---------------------------------------------------------------------


    /**
     * Creates generators cache.
     */
    public void load()
    {
        this.taskCache.clear();
        this.taskBundleCache.clear();

        this.addon.log("Loading tasks from database...");

        this.taskDatabase.loadObjects().forEach(this::loadTask);
        this.taskBundleDatabase.loadObjects().forEach(this::loadBundle);

        this.addon.log("Done");
    }


    /**
     * Loads task in cache silently. Used when loading.
     *
     * @param taskObject that must be stored.
     * @return true if successful
     */
    private boolean loadTask(TaskObject taskObject)
    {
        return this.loadTask(taskObject, true, null);
    }


    /**
     * Load taskObject in the cache.
     *
     * @param taskObject - taskObject that must be stored.
     * @param overwrite - true if previous taskObject should be overwritten
     * @param user - user making the request
     * @return - true if imported
     */
    public boolean loadTask(TaskObject taskObject, boolean overwrite, User user)
    {
        if (this.taskCache.containsKey(taskObject.getUniqueId()))
        {
            if (!overwrite)
            {
                return false;
            }
            else
            {
                this.taskCache.replace(taskObject.getUniqueId(), taskObject);
                return true;
            }
        }

        if (user != null)
        {
            Utils.sendMessage(user,
                user.getTranslation(Constants.MESSAGES + "task-loaded",
                    Constants.TASK, taskObject.getUniqueId()));
        }

        this.taskCache.put(taskObject.getUniqueId(), taskObject);
        return true;
    }


    /**
     * Loads task bundle in cache silently. Used when loading.
     *
     * @param taskBundleObject that must be stored.
     * @return true if successful
     */
    private boolean loadBundle(TaskBundleObject taskBundleObject)
    {
        return this.loadBundle(taskBundleObject, true, null);
    }


    /**
     * Load task bundle object in the cache.
     *
     * @param taskBundleObject - taskBundleObject that must be stored.
     * @param overwrite - true if previous taskBundleObject should be overwritten
     * @param user - user making the request
     * @return - true if imported
     */
    public boolean loadBundle(TaskBundleObject taskBundleObject, boolean overwrite, User user)
    {
        if (this.taskBundleCache.containsKey(taskBundleObject.getUniqueId()))
        {
            if (!overwrite)
            {
                return false;
            }
            else
            {
                this.taskBundleCache.replace(taskBundleObject.getUniqueId(), taskBundleObject);
                return true;
            }
        }

        if (user != null)
        {
            Utils.sendMessage(user,
                user.getTranslation(Constants.MESSAGES + "bundle-loaded",
                    Constants.BUNDLE, taskBundleObject.getUniqueId()));
        }

        this.taskBundleCache.put(taskBundleObject.getUniqueId(), taskBundleObject);
        return true;
    }


    /**
     * Save generator tiers from cache into database
     */
    public void save()
    {
        this.taskCache.values().forEach(this::saveTask);
        this.taskBundleCache.values().forEach(this::saveTaskBundle);
        this.taskDataCache.values().forEach(this::saveTaskData);
    }


    /**
     * @param taskObject object that must be saved.
     */
    void saveTask(TaskObject taskObject)
    {
        this.taskDatabase.saveObjectAsync(taskObject);
    }


    /**
     * @param taskBundleObject object that must be saved.
     */
    private void saveTaskBundle(TaskBundleObject taskBundleObject)
    {
        this.taskBundleDatabase.saveObjectAsync(taskBundleObject);
    }


    /**
     * @param taskDataObject object that must be saved.
     */
    private void saveTaskData(TaskDataObject taskDataObject)
    {
        this.taskDataDatabase.saveObjectAsync(taskDataObject);
    }


// ---------------------------------------------------------------------
// Section: User related data
// ---------------------------------------------------------------------





// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * Instance of Tasks Addon class.
     */
    private final TasksAddon addon;

    /**
     * Variable stores map that links String to loaded tasks object.
     */
    private final Map<String, TaskObject> taskCache;

    /**
     * Variable stores database of tasks objects.
     */
    private final Database<TaskObject> taskDatabase;

    /**
     * Variable stores map that links String to loaded tasks data object.
     */
    private final Map<String, TaskDataObject> taskDataCache;

    /**
     * Variable stores database of tasks data objects.
     */
    private final Database<TaskDataObject> taskDataDatabase;

    /**
     * Variable stores map that links String to loaded tasks bundle object.
     */
    private final Map<String, TaskBundleObject> taskBundleCache;

    /**
     * Variable stores database of tasks bundle objects.
     */
    private final Database<TaskBundleObject> taskBundleDatabase;
}
