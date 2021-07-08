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
     * Stores active bundle by owner permission.
     */
    @Expose
    @Nullable
    private String ownerBundle = null;

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
     * Task map that contains task ID and timestamp when it was completed last time/
     */
    @Expose
    private Map<String, Long> taskTimestamp = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}