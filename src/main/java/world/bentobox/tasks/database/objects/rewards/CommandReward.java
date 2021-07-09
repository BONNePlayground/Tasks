//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.rewards;


import com.google.gson.annotations.Expose;


/**
 * Rewarded command.
 */
public class CommandReward implements Reward
{
    /**
     * @return RewardType.COMMAND.
     */
    @Override
    public RewardType getType()
    {
        return RewardType.COMMAND;
    }


    /**
     * Gets command.
     *
     * @return the command
     */
    public String getCommand()
    {
        return command;
    }


    /**
     * Sets command.
     *
     * @param command the command
     */
    public void setCommand(String command)
    {
        this.command = command;
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
     * String that stores command.
     */
    @Expose
    private String command;


    /**
     * Boolean that indicate that prize is given to every team member (online).
     */
    @Expose
    private boolean teamPrize;
}
