//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.managers;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskBundleObject;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.listeners.tasks.Task;
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
                // Stop old task
                this.taskCache.get(taskObject.getUniqueId()).getTask().onStop();
                // Replace
                this.taskCache.replace(taskObject.getUniqueId(), taskObject);
                // Start new task.
                taskObject.getTask().onStart();

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

        // Start new task.
        taskObject.getTask().onStart();

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
     * @param withTaskStop indicate that all tasks must be stopped at the end.
     */
    public void save(boolean withTaskStop)
    {
        this.taskCache.values().forEach(this::saveTask);
        this.taskBundleCache.values().forEach(this::saveTaskBundle);
        this.taskDataCache.values().forEach(this::saveTaskData);

        if (withTaskStop)
        {
            // Now stop all active tasks.
            this.taskCache.values().stream().map(TaskObject::getTask).forEach(Task::onStop);
        }
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


    /**
     * This method checks every island in stored worlds for user and loads them in cache.
     *
     * @param uniqueId User unique id.
     */
    public void loadUserIslands(UUID uniqueId)
    {
        BentoBox.getInstance().getIWM().getOverWorlds().stream().
            map(world -> this.addon.getIslands().getIsland(world, uniqueId)).
            filter(Objects::nonNull).
            forEach(island ->
            {
                if (island.getOwner() == uniqueId)
                {
                    // Owner island must be validated.
                    this.validateIslandData(island);
                }
                else
                {
                    // Members does not influence island data.
                    this.addIslandData(island);
                }
            });
    }


    /**
     * Load island from database into the cache or create new island data
     *
     * @param island - island that must be loaded
     */
    private void addIslandData(@NotNull Island island)
    {
        final String uniqueID = island.getUniqueId();

        if (this.taskDataCache.containsKey(uniqueID))
        {
            return;
        }

        // The island is not in the cache
        // Check if the island exists in the database

        if (this.taskDataDatabase.objectExists(uniqueID))
        {
            // Load player from database
            TaskDataObject data = this.taskDataDatabase.loadObject(uniqueID);
            // Store in cache

            if (data != null)
            {
                this.taskDataCache.put(uniqueID, data);
            }
            else
            {
                this.addon.logError("Could not load NULL task data object.");
            }
        }
        else
        {
            // Create the island data
            TaskDataObject pd = new TaskDataObject();
            pd.setUniqueId(uniqueID);

            // Update island data
            pd.setIslandActiveTaskCount(this.addon.getSettings().getDefaultActiveTaskCount());
            pd.setIslandBundle(null);

            // Update owner data.
            this.updateOwnerBundle(island, pd);
            this.updateOwnerTaskCount(island, pd);

            // Save data.
            this.saveTaskData(pd);

            // Add to cache
            this.taskDataCache.put(uniqueID, pd);
        }
    }


    /**
     * This method adds, validates and returns island tasks data for given island.
     *
     * @param island Island which data must be returned.
     * @return TaskDataObject or null if failed to create.
     */
    @Nullable
    public TaskDataObject validateIslandData(@Nullable Island island)
    {
        if (island == null || island.getOwner() == null)
        {
            return null;
        }

        this.addIslandData(island);
        TaskDataObject dataObject = this.taskDataCache.get(island.getUniqueId());

        if (dataObject == null)
        {
            return null;
        }

        // Validate data in generator object.
        this.updateOwnerBundle(island, dataObject);
        this.updateOwnerTaskCount(island, dataObject);

        // Remove Tasks From Active tasks List:
        dataObject.getActiveTasks().removeIf(generator ->
        {
            // TODO: Remove tasks that are disabled.
            return false;
        });

        if (dataObject.getActiveTaskCount() > 0 &&
            dataObject.getActiveTaskCount() < dataObject.getActiveTasks().size())
        {
            // There are more active tasks then allowed.
            // Start to remove from first element.

            Iterator<String> activeTasks =
                new ArrayList<>(dataObject.getActiveTasks()).iterator();

            while (dataObject.getActiveTasks().size() > dataObject.getActiveTaskCount() &&
                activeTasks.hasNext())
            {
                dataObject.getActiveTasks().remove(activeTasks.next());
            }
        }

        return dataObject;
    }


    /**
     * This method updates owner active tasks count.
     *
     * @param island Island object that requires update.
     * @param dataObject Data Object that need to be populated.
     */
    private void updateOwnerTaskCount(@NotNull Island island, @NotNull TaskDataObject dataObject)
    {
        User owner = User.getInstance(island.getOwner());

        // Permission check can be done only to a player object.
        if (owner != null && owner.isPlayer())
        {
            // Update max owner max task count.
            int permissionSize = Utils.getPermissionValue(owner,
                Utils.getPermissionString(island.getWorld(), "[gamemode].tasks.active-tasks"),
                0);
            dataObject.setOwnerActiveTaskCount(permissionSize);
        }
    }


    /**
     * This method updates owner bundle for island.
     *
     * @param island Island object that requires update.
     * @param dataObject Data Object that need to be populated.
     */
    private void updateOwnerBundle(@NotNull Island island, @NotNull TaskDataObject dataObject)
    {
        User owner = User.getInstance(island.getOwner());

        // Permission check can be done only to a player object.
        if (owner != null && owner.isPlayer())
        {
            // Update owner bundle.
            String permissionBundle = Utils.getPermissionValue(owner,
                Utils.getPermissionString(island.getWorld(), "[gamemode].tasks.bundle"),
                null);
            dataObject.setOwnerBundle(permissionBundle);
        }
    }


    /**
     * This method removes data from tasks data cache and database with given id.
     * @param uniqueId Id that must be removed.
     */
    public void wipeIslandData(String uniqueId)
    {
        this.taskDataCache.remove(uniqueId);
        this.taskDataDatabase.deleteID(uniqueId);
    }


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
