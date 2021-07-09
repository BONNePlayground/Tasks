//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.rewards;


import com.google.gson.annotations.JsonAdapter;

import world.bentobox.tasks.database.adapters.RewardAdapter;


@JsonAdapter(RewardAdapter.class)
public interface Reward
{
    /**
     * This method returns the type of the reward.
     * @return RewardType UNKNOWN.
     */
    default RewardType getType()
    {
        return RewardType.UNKNOWN;
    }


    /**
     * Stores the type of rewards.
     */
    enum RewardType
    {
        /**
         * Type constant for command reward.
         */
        COMMAND,
        /**
         * Type constant for experience reward.
         */
        EXPERIENCE,
        /**
         * Type constant for item reward.
         */
        ITEM,
        /**
         * Type constant for money reward.
         */
        MONEY,
        /**
         * Type constant for unknown reward.
         */
        UNKNOWN
    }
}
