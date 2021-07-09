//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import java.util.List;


/**
 * An option for description setting and changing.
 */
public class DescriptionOption implements Option
{
    /**
     * @return OptionType.DESCRIPTION
     */
    @Override
    public OptionType getType()
    {
        return OptionType.DESCRIPTION;
    }


    /**
     * Gets description.
     *
     * @return the description
     */
    public List<String> getDescription()
    {
        return description;
    }


    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(List<String> description)
    {
        this.description = description;
    }


    /**
     * Stores the description option.
     */
    @Expose
    private List<String> description;
}
