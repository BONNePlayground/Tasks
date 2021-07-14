//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects;


import com.google.gson.annotations.Expose;

import org.jetbrains.annotations.Nullable;
import java.util.Date;
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
// Section: Processing Methods
// ---------------------------------------------------------------------


    /**
     * This method returns progress of given task.
     * @param taskId Task which progress must be returned.
     * @return double that represents task progress.
     */
    public double getTaskProgress(String taskId)
    {
        // Always insert empty data if missing?
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());

        return taskInternalData.getCurrentTaskProgress();
    }


    /**
     * This method returns if task is completed.
     * @param taskId Task which progress must be returned.
     * @return {@code true} if task has been completed at least once, {@code false} otherwise.
     */
    public boolean isTaskCompleted(String taskId)
    {
        // Always insert empty data if missing?
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());

        return taskInternalData.getCompletionCount() > 0;
    }


    /**
     * This method returns a long that represents a date when task was completed last time.
     * @param taskId Task which progress must be returned.
     * @return long.
     */
    public long getLastCompletionTime(String taskId)
    {
        // Always insert empty data if missing?
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());

        return taskInternalData.getLastCompletionTime();
    }


    /**
     * This method starts the given task by creating new internal data and adding task to the
     * active task set.
     * @param taskId Task that must be started.
     */
    public void startTask(String taskId)
    {
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());

        if (!this.activeTasks.add(taskId))
        {
            // task is already active.
            return;
        }

        // Set current progress as 0.
        taskInternalData.setCurrentTaskProgress(0);
    }


    /**
     * Increase progress of the task.
     *
     * @param taskId the task id
     * @param progress the progress
     * @return the new progress value.
     */
    public double increaseProgress(String taskId, double progress)
    {
        // Always insert empty data if missing?
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());
        taskInternalData.increaseProgress(progress);

        return taskInternalData.getCurrentTaskProgress();
    }


    /**
     * Decrease progress of the task.
     *
     * @param taskId the task id
     * @param progress the progress
     * @return the new progress value.
     */
    public double decreaseProgress(String taskId, double progress)
    {
        // Always insert empty data if missing?
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());
        taskInternalData.decreaseProgress(progress);

        return taskInternalData.getCurrentTaskProgress();
    }


    /**
     * Finishes the task.
     *
     * @param taskId the task id
     */
    public void finishTask(String taskId)
    {
        // Always insert empty data if missing?
        TaskInternalData taskInternalData =
            this.taskStatus.computeIfAbsent(taskId, task -> new TaskInternalData());
        taskInternalData.finishTask();

        // Remove from active task set?
        this.activeTasks.remove(taskId);
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
     * Gets task status.
     *
     * @return the task status
     */
    public Map<String, TaskInternalData> getTaskStatus()
    {
        return taskStatus;
    }


    /**
     * Sets task status.
     *
     * @param taskStatus the task status
     */
    public void setTaskStatus(Map<String, TaskInternalData> taskStatus)
    {
        this.taskStatus = taskStatus;
    }


// ---------------------------------------------------------------------
// Section: Internal Class
// ---------------------------------------------------------------------


    /**
     * This class stores internal data about task progress, completion count and etc.
     */
    private static class TaskInternalData
    {
        /**
         * Increase task progress.
         *
         * @param progress the progress
         */
        public void increaseProgress(double progress)
        {
            this.currentTaskProgress += progress;
        }


        /**
         * Decrease task progress.
         *
         * @param progress the progress
         */
        public void decreaseProgress(double progress)
        {
            this.currentTaskProgress -= progress;
        }


        /**
         * This method finishes current task.
         */
        public void finishTask()
        {
            // Reset progress
            this.currentTaskProgress = 0;
            // Increase completion count.
            this.completionCount++;
            this.currentCompletionCount++;
            // Add last completion date.
            this.lastCompletionTime = new Date().getTime();
        }


        /**
         * This method resets current task completion count.
         */
        public void resetTask()
        {
            // reset current completion task for rest.
            this.currentCompletionCount = 0;
        }


        // ---------------------------------------------------------------------
        // Section: Getters and setters
        // ---------------------------------------------------------------------


        /**
         * Gets current task progress.
         *
         * @return the current task progress
         */
        public double getCurrentTaskProgress()
        {
            return currentTaskProgress;
        }


        /**
         * Sets current task progress.
         *
         * @param currentTaskProgress the current task progress
         */
        public void setCurrentTaskProgress(double currentTaskProgress)
        {
            this.currentTaskProgress = currentTaskProgress;
        }


        /**
         * Gets completion count.
         *
         * @return the completion count
         */
        public long getCompletionCount()
        {
            return completionCount;
        }


        /**
         * Sets completion count.
         *
         * @param completionCount the completion count
         */
        public void setCompletionCount(long completionCount)
        {
            this.completionCount = completionCount;
        }


        /**
         * Gets last completion time.
         *
         * @return the last completion time
         */
        public long getLastCompletionTime()
        {
            return lastCompletionTime;
        }


        /**
         * Sets last completion time.
         *
         * @param lastCompletionTime the last completion time
         */
        public void setLastCompletionTime(long lastCompletionTime)
        {
            this.lastCompletionTime = lastCompletionTime;
        }


        /**
         * Gets current completion count.
         *
         * @return the current completion count
         */
        public long getCurrentCompletionCount()
        {
            return currentCompletionCount;
        }


        /**
         * Sets current completion count.
         *
         * @param currentCompletionCount the current completion count
         */
        public void setCurrentCompletionCount(long currentCompletionCount)
        {
            this.currentCompletionCount = currentCompletionCount;
        }


        // ---------------------------------------------------------------------
        // Section: Variables
        // ---------------------------------------------------------------------


        /**
         * Stores current progress of an active task.
         */
        @Expose
        private double currentTaskProgress = 0;

        /**
         * Stores amount of times when task was completed since last reset.
         */
        @Expose
        private long currentCompletionCount;

        /**
         * Stores amount of times when task was completed.
         */
        @Expose
        private long completionCount;

        /**
         * Stores a last time when task was completed.
         */
        @Expose
        private long lastCompletionTime;
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
     * Task map that contains task ID and it internal data.
     */
    @Expose
    private Map<String, TaskInternalData> taskStatus = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
}