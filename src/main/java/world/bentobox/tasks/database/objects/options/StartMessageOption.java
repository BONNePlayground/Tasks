//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import org.bukkit.ChatColor;

import world.bentobox.bentobox.util.Util;


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
     * Gets message type.
     *
     * @return the message type
     */
    public Type getMessageType()
    {
        return messageType;
    }


    /**
     * Sets message type.
     *
     * @param messageType the message type
     */
    public void setMessageType(Type messageType)
    {
        this.messageType = messageType;
    }


    /**
     * This method returns colored message text.
     * @return Colored message text.
     */
    public String getColoredMessage()
    {
        return Util.stripSpaceAfterColorCodes(
            ChatColor.translateAlternateColorCodes('&', this.getStartMessage()));
    }


    /**
     * This enum stores possible values for message type.
     */
    public enum Type
    {
        /**
         * Message will be send to whole server.
         */
        BROADCAST,
        /**
         * Message will be send to the team.
         */
        TEAM,
        /**
         * Message will be send to starter.
         */
        STARTER
    }


    /**
     * Stores the StartMessage option.
     */
    @Expose
    private String startMessage;

    /**
     * Indicates the type of message that will be send.
     */
    @Expose
    private Type messageType;
}
