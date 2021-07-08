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
}
