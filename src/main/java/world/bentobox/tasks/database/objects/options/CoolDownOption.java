//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;


/**
 * An option for cool down setting and changing.
 */
public class CoolDownOption implements Option
{
    /**
     * @return OptionType.COOL_DOWN
     */
    @Override
    public OptionType getType()
    {
        return OptionType.COOL_DOWN;
    }


    /**
     * Gets cool down.
     *
     * @return the cool down
     */
    public long getCoolDown()
    {
        return coolDown;
    }


    /**
     * Sets cool down.
     *
     * @param coolDown the cool down
     */
    public void setCoolDown(long coolDown)
    {
        this.coolDown = coolDown;
    }


    /**
     * Stores the coolDown option.
     */
    @Expose
    private long coolDown;
}
