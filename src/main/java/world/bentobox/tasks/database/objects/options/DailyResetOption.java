//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;


/**
 * This option allows to reset task daily.
 */
public class DailyResetOption implements Option
{
    /**
     * @return OptionType.DAILY_RESET
     */
    @Override
    public OptionType getType()
    {
        return OptionType.DAILY_RESET;
    }


    /**
     * Gets hour.
     *
     * @return the hour of day (0 - 23)
     */
    public int getHour()
    {
        return hour;
    }


    /**
     * Sets hour.
     *
     * @param hour the hour
     */
    public void setHour(int hour)
    {
        if (hour < 0 || hour > 24)
        {
            // Allow to set from 0 - 23
            this.hour = Math.abs(hour % 23);
        }
        else
        {
            this.hour = hour;
        }
    }


    /**
     * Stores a h of day when reset will happen.
     */
    @Expose
    private int hour;
}
