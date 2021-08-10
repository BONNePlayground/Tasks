//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages fishing and passes that information to the correct data.
 */
public class FishingTask extends Task implements Listener
{
    /**
     * Instantiates a new fishing task
     */
    public FishingTask()
    {
    }


    /**
     * Instantiates a new fishing task
     *
     * @param itemSet the item type
     * @param whitelist the whitelist
     * @param itemCount the item count
     */
    public FishingTask(String taskId, Set<Material> itemSet, boolean whitelist, int itemCount)
    {
        super(taskId);
        this.itemSet = itemSet;
        this.whitelist = whitelist;
        this.itemCount = itemCount;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishing(PlayerFishEvent event)
    {
        if (!PlayerFishEvent.State.CAUGHT_FISH.equals(event.getState()))
        {
            // Interested only in Player entities.
            return;
        }

        if (event.getCaught() == null || !(event.getCaught() instanceof Item))
        {
            // Interested if "fish" is item.
            return;
        }

        Material caughtType = ((Item) event.getCaught()).getItemStack().getType();

        if (!this.getItemSet().isEmpty() &&
            (this.whitelist && !this.getItemSet().contains(caughtType) ||
                !this.whitelist && this.getItemSet().contains(caughtType)))
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

        if (progress >= this.itemCount)
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
    public Set<Material> getItemSet()
    {
        return itemSet;
    }


    /**
     * Sets item set.
     *
     * @param itemSet the item set
     */
    public void setItemSet(Set<Material> itemSet)
    {
        this.itemSet = itemSet;
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
    public int getItemCount()
    {
        return itemCount;
    }


    /**
     * Sets item count.
     *
     * @param itemCount the item count
     */
    public void setItemCount(int itemCount)
    {
        this.itemCount = itemCount;
    }


    /**
     * @return item count.
     */
    @Override
    public double getTargetAmount()
    {
        return this.itemCount;
    }


    /**
     * Returns type of the task.
     * @return Task Type.
     */
    @Override
    public Type getType()
    {
        return Type.FISHING;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Item Set.
     */
    @Expose
    private Set<Material> itemSet;

    /**
     * White list of blacklist.
     */
    @Expose
    private boolean whitelist;

    /**
     * Number of items.
     */
    @Expose
    private int itemCount;
}
