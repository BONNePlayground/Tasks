//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;


/**
 * An option for StartMessage setting and changing.
 */
public class StartMessageOption implements Option
{
    /**
     * @return OptionType.START_MESSAGE
     */
    @Override
    public OptionType getType()
    {
        return OptionType.START_MESSAGE;
    }


    /**
     * Gets StartMessage.
     *
     * @return the description
     */
    public String getStartMessage()
    {
        return startMessage;
    }


    /**
     * Sets StartMessage.
     *
     * @param startMessage the description
     */
    public void setStartMessage(String startMessage)
    {
        this.startMessage = startMessage;
    }


    /**
     * Stores the StartMessage option.
     */
    @Expose
    private String startMessage;
}
