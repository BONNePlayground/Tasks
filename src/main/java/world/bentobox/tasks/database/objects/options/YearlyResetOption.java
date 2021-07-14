//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import java.time.Month;


/**
 * This option allows to reset task yearly.
 */
public class YearlyResetOption extends MonthlyResetOption
{
    /**
     * @return OptionType.YEARLY_RESET
     */
    @Override
    public OptionType getType()
    {
        return OptionType.YEARLY_RESET;
    }


    /**
     * Gets month.
     *
     * @return the month
     */
    public Month getMonth()
    {
        return month;
    }


    /**
     * Sets month.
     *
     * @param month the month
     */
    public void setMonth(Month month)
    {
        this.month = month;
    }


    /**
     * Limits setting day of month.
     * @param day the day
     */
    @Override
    public void setDay(int day)
    {
        super.setDay(day % this.month.maxLength());
    }


    /**
     * Stores the month when reset should happen.
     */
    @Expose
    private Month month;
}
