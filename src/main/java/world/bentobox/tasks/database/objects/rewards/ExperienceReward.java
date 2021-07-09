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
     * @return RewardType.EXPERIENCE.
     */
    @Override
    public RewardType getType()
    {
        return RewardType.EXPERIENCE;
    }


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
     * Is team prize boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isTeamPrize()
    {
        return teamPrize;
    }


    /**
     * Sets team prize.
     *
     * @param teamPrize the team prize
     */
    public void setTeamPrize(boolean teamPrize)
    {
        this.teamPrize = teamPrize;
    }


    /**
     * Value of rewarded experience.
     */
    @Expose
    private long experience;

    /**
     * Boolean that indicate that prize is given to every team member (online).
     */
    @Expose
    private boolean teamPrize;
}
