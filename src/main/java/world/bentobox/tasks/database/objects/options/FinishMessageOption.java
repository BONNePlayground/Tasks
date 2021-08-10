//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import org.bukkit.ChatColor;

import world.bentobox.bentobox.util.Util;


/**
 * An option for FinishMessage setting and changing.
 */
public class FinishMessageOption implements Option
{
    /**
     * @return OptionType.FINISH_MESSAGE
     */
    @Override
    public OptionType getType()
    {
        return OptionType.FINISH_MESSAGE;
    }


    /**
     * Gets FinishMessage.
     *
     * @return the FinishMessage
     */
    public String getFinishMessage()
    {
        return finishMessage;
    }


    /**
     * Sets FinishMessage.
     *
     * @param finishMessage the FinishMessage
     */
    public void setFinishMessage(String finishMessage)
    {
        this.finishMessage = finishMessage;
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
            ChatColor.translateAlternateColorCodes('&', this.getFinishMessage()));
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
         * Message will be send to finisher.
         */
        FINISHER
    }


    /**
     * Stores the FinishMessage option.
     */
    @Expose
    private String finishMessage;

    /**
     * Indicates which type of message will be sent.
     */
    @Expose
    private Type messageType;
}
