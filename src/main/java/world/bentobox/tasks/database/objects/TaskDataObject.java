//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects;


import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;


/**
 * This class contains data about Tasks Data Object.
 */
@Table(name = "TaskData")
public class TaskDataObject implements DataObject
{
    /**
     * Instantiates a new Tasks object.
     */
    public TaskDataObject()
    {
        // Empty constructor
    }


    /**
     * If owner has a permission that defines infinite task count or has a specific amount then return
     * ownerActiveTaskCount, otherwise return islandActiveTaskCount
     *
     * @return getActiveTaskCount for this object.
     */
    public int getActiveTaskCount()
    {
        return this.ownerActiveTaskCount == -1 || this.ownerActiveTaskCount > 0 ?
            this.ownerActiveTaskCount : this.islandActiveTaskCount;
    }



// ---------------------------------------------------------------------
// Section: Public Setters and Getters
// ---------------------------------------------------------------------


    /**
     * @return the uniqueId
     */
    @Override
    public String getUniqueId()
    {
        return this.uniqueId;
    }


    /**
     * @param uniqueId - unique ID the uniqueId to set
     */
    @Override
    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }


    /**
     * Gets island bundle.
     *
     * @return the island bundle
     */
    @Nullable
    public String getIslandBundle()
    {
        return islandBundle;
    }


    /**
     * Sets island bundle.
     *
     * @param islandBundle the island bundle
     */
    public void setIslandBundle(@Nullable String islandBundle)
    {
        this.islandBundle = islandBundle;
    }


    /**
     * Gets owner bundle.
     *
     * @return the owner bundle
     */
    @Nullable
    public String getOwnerBundle()
    {
        return ownerBundle;
    }


    /**
     * Sets owner bundle.
     *
     * @param ownerBundle the owner bundle
     */
    public void setOwnerBundle(@Nullable String ownerBundle)
    {
        this.ownerBundle = ownerBundle;
    }


    /**
     * Gets active tasks.
     *
     * @return the active tasks
     */
    public Set<String> getActiveTasks()
    {
        return activeTasks;
    }


    /**
     * Sets active tasks.
     *
     * @param activeTasks the active tasks
     */
    public void setActiveTasks(Set<String> activeTasks)
    {
        this.activeTasks = activeTasks;
    }


    /**
     * Gets task status.
     *
     * @return the task status
     */
    public Map<String, Integer> getTaskStatus()
    {
        return taskStatus;
    }


    /**
     * Sets task status.
     *
     * @param taskStatus the task status
     */
    public void setTaskStatus(Map<String, Integer> taskStatus)
    {
        this.taskStatus = taskStatus;
    }


    /**
     * Gets task timestamp.
     *
     * @return the task timestamp
     */
    public Map<String, Long> getTaskTimestamp()
    {
        return taskTimestamp;
    }


    /**
     * Sets task timestamp.
     *
     * @param taskTimestamp the task timestamp
     */
    public void setTaskTimestamp(Map<String, Long> taskTimestamp)
    {
        this.taskTimestamp = taskTimestamp;
    }


    /**
     * Gets island active task count.
     *
     * @return the island active task count
     */
    public int getIslandActiveTaskCount()
    {
        return islandActiveTaskCount;
    }


    /**
     * Sets island active task count.
     *
     * @param islandActiveTaskCount the island active task count
     */
    public void setIslandActiveTaskCount(int islandActiveTaskCount)
    {
        this.islandActiveTaskCount = islandActiveTaskCount;
    }


    /**
     * Gets owner active task count.
     *
     * @return the owner active task count
     */
    public int getOwnerActiveTaskCount()
    {
        return ownerActiveTaskCount;
    }


    /**
     * Sets owner active task count.
     *
     * @param ownerActiveTaskCount the owner active task count
     */
    public void setOwnerActiveTaskCount(int ownerActiveTaskCount)
    {
        this.ownerActiveTaskCount = ownerActiveTaskCount;
    }


    /**
     * Gets task progress.
     *
     * @return the task progress
     */
    public Map<String, Double> getTaskProgress()
    {
        return taskProgress;
    }


    /**
     * Sets task progress.
     *
     * @param taskProgress the task progress
     */
    public void setTaskProgress(Map<String, Double> taskProgress)
    {
        this.taskProgress = taskProgress;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * UniqueId for the object.
     */
    @Expose
    private String uniqueId;

    /**
     * Stores active bundle.
     */
    @Expose
    @Nullable
    private String islandBundle = null;

    /**
     * Stores max amount of active task count.
     */
    @Expose
    private int islandActiveTaskCount = 1;

    /**
     * Stores active bundle by owner permission.
     */
    @Expose
    @Nullable
    private String ownerBundle = null;

    /**
     * Stores max amount of active task count by permission.
     */
    @Expose
    private int ownerActiveTaskCount = 0;

    /**
     * Set of active tasks for a player.
     */
    @Expose
    private Set<String> activeTasks = new HashSet<>();

    /**
     * Task map that contains task ID and number of its completions.
     */
    @Expose
    private Map<String, Integer> taskStatus = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Task map that contains task ID and progress of the task.
     */
    @Expose
    private Map<String, Double> taskProgress = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Task map that contains task ID and timestamp when it was completed last time/
     */
    @Expose
    private Map<String, Long> taskTimestamp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}