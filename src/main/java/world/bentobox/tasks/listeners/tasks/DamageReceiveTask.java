//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import world.bentobox.bentobox.BentoBox;


/**
 * This is a main task processor.
 * This task manages damage and passes that information to the correct data.
 */
public class DamageReceiveTask implements Task, Listener
{
    /**
     * Instantiates a new damage task.
     */
    public DamageReceiveTask()
    {
    }


    /**
     * Instantiates a new damage task.
     *
     * @param entityCount the entity count
     */
    public DamageReceiveTask(long entityCount)
    {
        this.damageAmount = entityCount;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event)
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
     * Gets damage amount.
     *
     * @return the damage amount
     */
    public long getDamageAmount()
    {
        return damageAmount;
    }


    /**
     * Sets damage amount.
     *
     * @param damageAmount the damage amount
     */
    public void setDamageAmount(long damageAmount)
    {
        this.damageAmount = damageAmount;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * Number of damage.
     */
    @Expose
    private long damageAmount;
}
