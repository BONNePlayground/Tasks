//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.requirements;


import com.google.gson.annotations.Expose;


/**
 * Level requirement.
 */
public class LevelRequirement implements Requirement
{
    /**
     * Gets level.
     *
     * @return the level
     */
    public double getLevel()
    {
        return level;
    }


    /**
     * Sets level.
     *
     * @param level the level
     */
    public void setLevel(double level)
    {
        this.level = level;
    }


    /**
     * Variable that stores level.
     */
    @Expose
    private double level;
}
