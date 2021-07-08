//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.rewards;


import com.google.gson.annotations.Expose;


/**
 * Allows to set rewards experience.
 */
public class ExperienceReward implements Reward
{
    /**
     * Gets experience.
     *
     * @return the experience
     */
    public long getExperience()
    {
        return experience;
    }


    /**
     * Sets experience.
     *
     * @param experience the experience
     */
    public void setExperience(long experience)
    {
        this.experience = experience;
    }


    /**
     * Value of rewarded experience.
     */
    @Expose
    private long experience;
}
