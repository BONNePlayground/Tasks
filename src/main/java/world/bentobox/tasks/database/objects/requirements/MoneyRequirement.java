//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.requirements;


import com.google.gson.annotations.Expose;


/**
 * Money requirement.
 */
public class MoneyRequirement implements Requirement
{
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
     * Variable that stores money.
     */
    @Expose
    private double money;
}
