//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;


/**
 * This is a main task processor.
 * This task manages fishing and passes that information to the correct data.
 */
public class FishingTask implements Task, Listener
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
    public FishingTask(Set<ItemStack> itemSet, boolean whitelist, int itemCount)
    {
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
    public Set<ItemStack> getItemSet()
    {
        return itemSet;
    }


    /**
     * Sets item set.
     *
     * @param itemSet the item set
     */
    public void setItemSet(Set<ItemStack> itemSet)
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


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * Item Set.
     */
    @Expose
    private Set<ItemStack> itemSet;

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
