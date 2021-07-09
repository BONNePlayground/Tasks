//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import java.util.Date;


/**
 * An option for StartDate setting and changing.
 */
public class StartDateOption implements Option
{
    /**
     * @return OptionType.START_DATE
     */
    @Override
    public OptionType getType()
    {
        return OptionType.START_DATE;
    }


    /**
     * Gets StartDate.
     *
     * @return the StartDate
     */
    public Date getStartDate()
    {
        return startDate;
    }


    /**
     * Sets StartDate.
     *
     * @param startDate the StartDate
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }


    /**
     * Stores the StartDate option.
     */
    @Expose
    private Date startDate;
}
