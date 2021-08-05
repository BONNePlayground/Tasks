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
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages entity killing and passes that information to the correct data.
 */
public class EntityKillTask extends Task implements Listener
{
    /**
     * Instantiates a new Mob kill task.
     */
    public EntityKillTask()
    {
    }


    /**
     * Instantiates a new Mob kill task.
     *
     * @param entityType the entity type
     * @param whitelist the whitelist
     * @param entityCount the entity count
     */
    public EntityKillTask(Set<EntityType> entityType, boolean whitelist, int entityCount)
    {
        this.entityType = entityType;
        this.whitelist = whitelist;
        this.entityCount = entityCount;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityKill(EntityDeathEvent event)
    {
        if (event.getEntity().getKiller() == null)
        {
            // Interested only in Player entities.
            return;
        }

        if (this.whitelist && !this.getEntityType().contains(event.getEntity().getType()) ||
            !this.whitelist && this.getEntityType().contains(event.getEntity().getType()))
        {
            // Not a whitelisted or is blacklisted entities.
            return;
        }

        Player player = event.getEntity().getKiller();

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
     * Gets entity set.
     *
     * @return the entity set
     */
    public Set<EntityType> getEntityType()
    {
        return entityType;
    }


    /**
     * Sets entity set.
     *
     * @param entityType the entity set
     */
    public void setEntityType(Set<EntityType> entityType)
    {
        this.entityType = entityType;
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
     * Gets entity count.
     *
     * @return the entity count
     */
    public int getEntityCount()
    {
        return entityCount;
    }


    /**
     * Sets entity count.
     *
     * @param entityCount the entity count
     */
    public void setEntityCount(int entityCount)
    {
        this.entityCount = entityCount;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Entity Set.
     */
    @Expose
    private Set<EntityType> entityType;

    /**
     * White list of blacklist.
     */
    @Expose
    private boolean whitelist;

    /**
     * Number of blocks.
     */
    @Expose
    private int entityCount;
}
