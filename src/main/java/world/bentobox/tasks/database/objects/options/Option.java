//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.JsonAdapter;

import world.bentobox.tasks.database.adapters.OptionAdapter;


@JsonAdapter(OptionAdapter.class)
public interface Option
{
    /**
     * This method returns the type of option.
     * @return OptionType.UNKNOWN
     */
    default OptionType getType()
    {
        return OptionType.UNKNOWN;
    }


    /**
     * Enum that stores all possible option types.
     */
    enum OptionType
    {
        ICON,
        DESCRIPTION,
        START_MESSAGE,
        FINISH_MESSAGE,
        START_DATE,
        END_DATE,
        REPEATABLE,
        COOL_DOWN,
        DAILY_RESET,
        WEEKLY_RESET,
        MONTHLY_RESET,
        YEARLY_RESET,
        UNKNOWN
    }
}
