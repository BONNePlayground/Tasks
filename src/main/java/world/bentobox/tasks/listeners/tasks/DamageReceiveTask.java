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
import org.bukkit.event.entity.EntityDamageEvent;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages damage and passes that information to the correct data.
 */
public class DamageReceiveTask extends Task implements Listener
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
     * @param damageAmount the entity count
     */
    public DamageReceiveTask(String taskId, double damageAmount)
    {
        super(taskId);
        this.damageAmount = damageAmount;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if (!EntityType.PLAYER.equals(event.getEntityType()))
        {
            // Interested only in Player entities.
            return;
        }

        Player player = (Player) event.getEntity();

        TasksManager addonManager = TasksAddon.getInstance().getAddonManager();
        TaskDataObject islandData = addonManager.getIslandData(
            player,
            player.getWorld());

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

        double progress = islandData.increaseProgress(this.getTaskId(), event.getDamage());

        if (progress >= this.damageAmount)
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
     * @return damage amount.
     */
    @Override
    public double getTargetAmount()
    {
        return this.damageAmount;
    }


// ---------------------------------------------------------------------
// Section: Getters and Setters
// ---------------------------------------------------------------------


    /**
     * Gets damage amount.
     *
     * @return the damage amount
     */
    public double getDamageAmount()
    {
        return damageAmount;
    }


    /**
     * Sets damage amount.
     *
     * @param damageAmount the damage amount
     */
    public void setDamageAmount(double damageAmount)
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
    private double damageAmount;
}
