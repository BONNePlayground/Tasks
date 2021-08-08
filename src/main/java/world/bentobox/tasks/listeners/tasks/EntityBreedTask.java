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
import org.bukkit.event.entity.EntityBreedEvent;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages entity breeding and passes that information to the correct data.
 */
public class EntityBreedTask extends Task implements Listener
{
    /**
     * Instantiates a new Mob breed task.
     */
    public EntityBreedTask()
    {
    }


    /**
     * Instantiates a new Mob breed task.
     *
     * @param entityType the entity type
     * @param whitelist the whitelist
     * @param entityCount the entity count
     */
    public EntityBreedTask(String taskId, Set<EntityType> entityType, boolean whitelist, int entityCount)
    {
        super(taskId);
        this.entityType = entityType;
        this.whitelist = whitelist;
        this.entityCount = entityCount;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityBreed(EntityBreedEvent event)
    {
        if (event.getBreeder() == null || !EntityType.PLAYER.equals(event.getBreeder().getType()))
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

        Player player = (Player) event.getBreeder();

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


    /**
     * @return entity count.
     */
    @Override
    public double getTargetAmount()
    {
        return this.entityCount;
    }


    /**
     * Returns type of the task.
     * @return Task Type.
     */
    @Override
    public Type getType()
    {
        return Type.BREED;
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
