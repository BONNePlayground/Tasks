//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;


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
     * Is broadcast boolean.
     *
     * @return the boolean
     */
    public boolean isBroadcast()
    {
        return broadcast;
    }


    /**
     * Sets broadcast.
     *
     * @param broadcast the broadcast
     */
    public void setBroadcast(boolean broadcast)
    {
        this.broadcast = broadcast;
    }


    /**
     * Is finisher boolean.
     *
     * @return the boolean
     */
    public boolean isFinisher()
    {
        return finisher;
    }


    /**
     * Sets finisher.
     *
     * @param finisher the finisher
     */
    public void setFinisher(boolean finisher)
    {
        this.finisher = finisher;
    }


    /**
     * Stores the FinishMessage option.
     */
    @Expose
    private String finishMessage;

    /**
     * Indicates that message must be broadcast to the whole server.
     */
    @Expose
    private boolean broadcast;

    /**
     * Indicates that message must be send only to the user who finishes.
     * Otherwise it will be send to the whole team.
     */
    @Expose
    private boolean finisher;
}
