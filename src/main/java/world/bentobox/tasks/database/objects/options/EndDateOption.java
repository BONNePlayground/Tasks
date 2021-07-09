//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import java.util.Date;


/**
 * An option for EndDate setting and changing.
 */
public class EndDateOption implements Option
{
    /**
     * @return OptionType.END_DATE
     */
    @Override
    public OptionType getType()
    {
        return OptionType.END_DATE;
    }


    /**
     * Gets EndDate.
     *
     * @return the EndDate
     */
    public Date getEndDate()
    {
        return endDate;
    }


    /**
     * Sets EndDate.
     *
     * @param endDate the EndDate
     */
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }


    /**
     * Stores the EndDate option.
     */
    @Expose
    private Date endDate;
}
