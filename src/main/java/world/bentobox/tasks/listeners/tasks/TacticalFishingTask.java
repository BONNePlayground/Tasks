//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages tactical fishing and passes that information to the correct data.
 */
public class TacticalFishingTask extends Task implements Listener
{
    /**
     * Instantiates a new fishing task
     */
    public TacticalFishingTask()
    {
    }


    /**
     * Instantiates a new fishing task
     *
     * @param entityTypes the entity type
     * @param whitelist the whitelist
     * @param entityCount the entity count
     */
    public TacticalFishingTask(Set<EntityType> entityTypes, boolean whitelist, int entityCount)
    {
        this.entityTypes = entityTypes;
        this.whitelist = whitelist;
        this.entityCount = entityCount;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishing(PlayerBucketEntityEvent event)
    {
        if (this.whitelist && !this.getEntityTypes().contains(event.getEntity().getType()) ||
            !this.whitelist && this.getEntityTypes().contains(event.getEntity().getType()))
        {
            // Not a whitelisted or is blacklisted entities.
            return;
        }

        Player player = event.getPlayer();

        TasksManager addonManager = TasksAddon.getInstance().getAddonManager();
        TaskDataObject islandData = addonManager.getIslandData(player, player.getWorld());

        if (islandData == null)
        {
            // There is no data about this player.
            return;
        }

        if (!islandData.getActiveTasks().contains(this.getTaskId()))
        {
            // This is not active task for a player.
            return;
        }

        double progress = islandData.increaseProgress(this.getTaskId(), 1);

        if (progress >= this.entityCount)
        {
            addonManager.onTaskFinish(this.getTaskId(), player, islandData);
        }
        else
        {
            addonManager.onUpdateProgress(this.getTaskId(), player, islandData);
        }
    }


    /**
     * Registers current class as listener to the Bukkit.
     */
    @Override
    public void onStart()
    {
        Bukkit.getPluginManager().registerEvents(this, BentoBox.getInstance());
    }


    /**
     * Unregisters this listener from all handlers.
     */
    @Override
    public void onStop()
    {
        HandlerList.unregisterAll(this);
    }


// ---------------------------------------------------------------------
// Section: Getters and Setters
// ---------------------------------------------------------------------


    /**
     * Gets item set.
     *
     * @return the item set
     */
    public Set<EntityType> getEntityTypes()
    {
        return entityTypes;
    }


    /**
     * Sets item set.
     *
     * @param entityTypes the item set
     */
    public void setEntityTypes(Set<EntityType> entityTypes)
    {
        this.entityTypes = entityTypes;
    }


    /**
     * Is whitelist boolean.
     *
     * @return the boolean
     */
    public boolean isWhitelist()
    {
        return whitelist;
    }


    /**
     * Sets whitelist.
     *
     * @param whitelist the whitelist
     */
    public void setWhitelist(boolean whitelist)
    {
        this.whitelist = whitelist;
    }


    /**
     * Gets item count.
     *
     * @return the item count
     */
    public int getEntityCount()
    {
        return entityCount;
    }


    /**
     * Sets item count.
     *
     * @param entityCount the item count
     */
    public void setEntityCount(int entityCount)
    {
        this.entityCount = entityCount;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Item Set.
     */
    @Expose
    private Set<EntityType> entityTypes;

    /**
     * White list of blacklist.
     */
    @Expose
    private boolean whitelist;

    /**
     * Number of items.
     */
    @Expose
    private int entityCount;
}
