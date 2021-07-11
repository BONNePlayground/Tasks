//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.requirements;


import com.google.gson.annotations.JsonAdapter;

import world.bentobox.tasks.database.adapters.RequirementAdapter;
import world.bentobox.tasks.database.objects.rewards.Reward;


@JsonAdapter(RequirementAdapter.class)
public interface Requirement
{
    /**
     * This method returns the type of the requirement.
     * @return RequirementType UNKNOWN.
     */
    default RequirementType getType()
    {
        return RequirementType.UNKNOWN;
    }


    /**
     * Stores the type of requirements.
     */
    enum RequirementType
    {
        /**
         * Type constant for experience requirement.
         */
        EXPERIENCE,
        /**
         * Type constant for level requirement.
         */
        LEVEL,
        /**
         * Type constant for money requirement.
         */
        MONEY,
        /**
         * Type constant for permission requirement.
         */
        PERMISSION,
        /**
         * Type constant for task requirement.
         */
        TASK,
        /**
         * Type constant for unknown requirement.
         */
        UNKNOWN
    }
}
