//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages block breaking and placing and passes that information to the correct data.
 */
public class BlockTask extends Task implements Listener
{
    /**
     * Instantiates a new Block task.
     */
    public BlockTask()
    {
    }


    /**
     * Instantiates a new Block task.
     *
     * @param materialSet the material set
     * @param whitelist the whitelist
     * @param blockCount the block count
     * @param place the place
     */
    public BlockTask(Set<Material> materialSet, boolean whitelist, int blockCount, boolean place)
    {
        this.materialSet = materialSet;
        this.whitelist = whitelist;
        this.blockCount = blockCount;
        this.place = place;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    /**
     * Process block placing events.
     * @param event Block Place Event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        TasksManager addonManager = TasksAddon.getInstance().getAddonManager();
        TaskDataObject islandData = addonManager.getIslandData(event.getPlayer(), event.getBlock().getWorld());

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

        if (this.whitelist && !this.getMaterialSet().contains(event.getBlock().getType()) ||
            !this.whitelist && this.getMaterialSet().contains(event.getBlock().getType()))
        {
            // Not a whitelisted or is blacklisted material.
            return;
        }

        if (this.place)
        {
            double progress = islandData.increaseProgress(this.getTaskId(), 1);

            if (progress >= this.blockCount)
            {
                addonManager.onTaskFinish(this.getTaskId(), event.getPlayer(), islandData);
            }
            else
            {
                addonManager.onUpdateProgress(this.getTaskId(), event.getPlayer(), islandData);
            }
        }
        else
        {
            islandData.decreaseProgress(this.getTaskId(), 1);
            addonManager.onUpdateProgress(this.getTaskId(), event.getPlayer(), islandData);
        }
    }


    /**
     * Process block break event.
     * @param event Block break event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (this.whitelist && !this.getMaterialSet().contains(event.getBlock().getType()) ||
            !this.whitelist && this.getMaterialSet().contains(event.getBlock().getType()))
        {
            // Not a whitelisted or is blacklisted material.
            return;
        }

        TasksManager addonManager = TasksAddon.getInstance().getAddonManager();
        TaskDataObject islandData = addonManager.getIslandData(event.getPlayer(), event.getBlock().getWorld());

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

        if (!this.place)
        {
            double progress = islandData.increaseProgress(this.getTaskId(), 1);

            if (progress >= this.blockCount)
            {
                addonManager.onTaskFinish(this.getTaskId(), event.getPlayer(), islandData);
            }
            else
            {
                addonManager.onUpdateProgress(this.getTaskId(), event.getPlayer(), islandData);
            }
        }
        else
        {
            islandData.decreaseProgress(this.getTaskId(), 1);
            addonManager.onUpdateProgress(this.getTaskId(), event.getPlayer(), islandData);
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
     * Gets material set.
     *
     * @return the material set
     */
    public Set<Material> getMaterialSet()
    {
        return materialSet;
    }


    /**
     * Sets material set.
     *
     * @param materialSet the material set
     */
    public void setMaterialSet(Set<Material> materialSet)
    {
        this.materialSet = materialSet;
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
     * Gets block count.
     *
     * @return the block count
     */
    public int getBlockCount()
    {
        return blockCount;
    }


    /**
     * Sets block count.
     *
     * @param blockCount the block count
     */
    public void setBlockCount(int blockCount)
    {
        this.blockCount = blockCount;
    }


    /**
     * Is place boolean.
     *
     * @return the boolean
     */
    public boolean isPlace()
    {
        return place;
    }


    /**
     * Sets place.
     *
     * @param place the place
     */
    public void setPlace(boolean place)
    {
        this.place = place;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Material Set.
     */
    @Expose
    private Set<Material> materialSet;

    /**
     * White list of blacklist.
     */
    @Expose
    private boolean whitelist;

    /**
     * Number of blocks.
     */
    @Expose
    private int blockCount;

    /**
     * Place or break blocks.
     */
    @Expose
    private boolean place;
}
