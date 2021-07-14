//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import java.time.Month;
import java.time.MonthDay;


/**
 * This option allows to reset task monthly.
 */
public class MonthlyResetOption extends DailyResetOption
{
    /**
     * @return OptionType.MONTHLY_RESET
     */
    @Override
    public OptionType getType()
    {
        return OptionType.MONTHLY_RESET;
    }


    /**
     * Gets day.
     *
     * @return the day 1 till 31
     */
    public int getDay()
    {
        return day;
    }


    /**
     * Sets day.
     *
     * @param day the day
     */
    public void setDay(int day)
    {
        this.day = Math.max(1, Math.abs(day % 31));
    }


    /**
     * Day of the month.
     */
    @Expose
    protected int day;
}
