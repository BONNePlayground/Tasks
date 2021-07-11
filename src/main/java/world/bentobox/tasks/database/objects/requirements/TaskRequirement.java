//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.requirements;


import com.google.gson.annotations.Expose;


/**
 * Task requirement.
 */
public class TaskRequirement implements Requirement
{
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


    /**
     * @return RequirementType.TASK
     */
    @Override
    public RequirementType getType()
    {
        return RequirementType.TASK;
    }


    /**
     * String that stores task id.
     */
    @Expose
    private String taskId;
}
