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
        COOL_DOWN,
        DESCRIPTION,
        END_DATE,
        FINISH_MESSAGE,
        ICON,
        REPEATABLE,
        REPEAT_TIMER,
        START_DATE,
        START_MESSAGE,
        UNKNOWN
    }
}
