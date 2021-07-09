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
     * String that stores command.
     */
    @Expose
    private String command;
}
