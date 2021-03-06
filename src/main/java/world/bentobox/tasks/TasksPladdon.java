//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks;


import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


/**
 * This is a dummy class that allows to load addon via Bukkit Plugin loader.
 * @author BONNe
 */
@Plugin(name="TasksPladdon", version="1.0")
@ApiVersion(ApiVersion.Target.v1_17)
@Dependency(value = "BentoBox")
public class TasksPladdon extends Pladdon
{
    @Override
    public Addon getAddon()
    {
        return new TasksAddon();
    }
}