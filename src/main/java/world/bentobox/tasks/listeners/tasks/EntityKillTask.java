//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;


/**
 * This is a main task processor.
 * This task manages entity killing and passes that information to the correct data.
 */
public class EntityKillTask implements Task, Listener
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
