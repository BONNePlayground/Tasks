//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import java.time.DayOfWeek;


/**
 * This option allows to reset task weekly.
 */
public class WeeklyResetOption extends DailyResetOption
{
    /**
     * @return OptionType.WEEKLY_RESET
     */
    @Override
    public OptionType getType()
    {
        return OptionType.WEEKLY_RESET;
    }


    /**
     * Gets day.
     *
     * @return the day
     */
    public DayOfWeek getDay()
    {
        return day;
    }


    /**
     * Sets day.
     *
     * @param day the day
     */
    public void setDay(DayOfWeek day)
    {
        this.day = day;
    }


    /**
     * Stores a day of week when reset will happen.
     */
    @Expose
    protected DayOfWeek day;
}
