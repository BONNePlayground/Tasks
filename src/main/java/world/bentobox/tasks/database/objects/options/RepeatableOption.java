//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;


/**
 * An option for repeatable setting and changing.
 */
public class RepeatableOption implements Option
{
    /**
     * @return OptionType.REPEATABLE
     */
    @Override
    public OptionType getType()
    {
        return OptionType.REPEATABLE;
    }


    /**
     * Is repeatable boolean.
     *
     * @return the boolean
     */
    public boolean isRepeatable()
    {
        return repeatable;
    }


    /**
     * Sets repeatable.
     *
     * @param repeatable the repeatable
     */
    public void setRepeatable(boolean repeatable)
    {
        this.repeatable = repeatable;
    }


    /**
     * Stores the repeatable option.
     */
    @Expose
    private boolean repeatable;
}
