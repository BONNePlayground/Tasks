//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects;


import com.google.gson.annotations.Expose;

import java.util.Collections;
import java.util.Set;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;


/**
 * This class contains data about Tasks Bundle Object.
 */
@Table(name = "TaskBundle")
public class TaskBundleObject implements DataObject
{
    /**
     * Instantiates a new Tasks object.
     */
    public TaskBundleObject()
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
     * Gets task set.
     *
     * @return the task set
     */
    public Set<String> getTaskSet()
    {
        return taskSet;
    }


    /**
     * Sets task set.
     *
     * @param taskSet the task set
     */
    public void setTaskSet(Set<String> taskSet)
    {
        this.taskSet = taskSet;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Set of tasks in current bundle.
     */
    @Expose
    private Set<String> taskSet = Collections.emptySet();

    /**
     * UniqueId for the object.
     */
    @Expose
    private String uniqueId;
}