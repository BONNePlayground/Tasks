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
     * Stores the FinishMessage option.
     */
    @Expose
    private String finishMessage;
}
