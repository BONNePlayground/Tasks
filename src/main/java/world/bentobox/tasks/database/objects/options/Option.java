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
}
