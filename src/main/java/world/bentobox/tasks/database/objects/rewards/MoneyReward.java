//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.rewards;


import com.google.gson.annotations.Expose;


/**
 * Allows to set rewards money.
 */
public class MoneyReward implements Reward
{
    /**
     * @return RewardType.MONEY.
     */
    @Override
    public RewardType getType()
    {
        return RewardType.MONEY;
    }


    /**
     * Gets money.
     *
     * @return the money
     */
    public double getMoney()
    {
        return money;
    }


    /**
     * Sets money.
     *
     * @param money the money
     */
    public void setMoney(double money)
    {
        this.money = money;
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
     * Value of rewarded money.
     */
    @Expose
    private double money;

    /**
     * Boolean that indicate that prize is given to every team member (online).
     */
    @Expose
    private boolean teamPrize;
}
