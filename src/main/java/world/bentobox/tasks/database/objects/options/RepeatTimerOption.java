//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;


/**
 * An option for repeat timer setting and changing.
 */
public class RepeatTimerOption implements Option
{
    /**
     * @return OptionType.REPEAT_TIMER
     */
    @Override
    public OptionType getType()
    {
        return OptionType.REPEAT_TIMER;
    }


    /**
     * Gets repeat timer.
     *
     * @return the repeat timer
     */
    public long getRepeatTimer()
    {
        return repeatTimer;
    }


    /**
     * Sets repeat timer.
     *
     * @param repeatTimer the repeat timer
     */
    public void setRepeatTimer(long repeatTimer)
    {
        this.repeatTimer = repeatTimer;
    }


    /**
     * Stores the repeatTimer option.
     */
    @Expose
    private long repeatTimer;
}
