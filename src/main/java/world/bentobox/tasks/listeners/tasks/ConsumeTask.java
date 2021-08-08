//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.listeners.tasks;


import com.google.gson.annotations.Expose;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;
import java.util.Set;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is a main task processor.
 * This task manages eating or drinking task passes that information to the correct data.
 */
public class ConsumeTask extends Task implements Listener
{
    /**
     * Instantiates a new fishing task
     */
    public ConsumeTask()
    {
    }


    /**
     * Instantiates a new Consume task.
     *
     * @param materialSet the material set
     * @param whitelist the whitelist
     * @param itemCount the item count
     */
    public ConsumeTask(String taskId, Set<Material> materialSet, boolean whitelist, int itemCount)
    {
        super(taskId);
        this.materialSet = materialSet;
        this.whitelist = whitelist;
        this.itemCount = itemCount;

        this.potionEffectTypes = null;
    }


    /**
     * Instantiates a new Consume task.
     *
     * @param materialSet the material set
     * @param potionEffectTypes the potion effect types
     * @param whitelist the whitelist
     * @param itemCount the item count
     */
    public ConsumeTask(Set<Material> materialSet, Set<PotionEffectType> potionEffectTypes, boolean whitelist, int itemCount)
    {
        this.materialSet = materialSet;
        this.whitelist = whitelist;
        this.itemCount = itemCount;

        this.potionEffectTypes = potionEffectTypes;
    }


// ---------------------------------------------------------------------
// Section: Main processing methods
// ---------------------------------------------------------------------


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFishing(PlayerItemConsumeEvent event)
    {
        if (this.whitelist && !this.getMaterialSet().contains(event.getItem().getType()) ||
            !this.whitelist && this.getMaterialSet().contains(event.getItem().getType()))
        {
            // Not a whitelisted or is blacklisted material.
            return;
        }

        if (Material.POTION.equals(event.getItem().getType()) && this.potionEffectTypes != null)
        {
            // Check potion effects for validation.
            PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
            PotionEffectType type = meta != null ? meta.getBasePotionData().getType().getEffectType() : null;

            if (this.whitelist && !this.getPotionEffectTypes().contains(type) ||
                !this.whitelist && this.getPotionEffectTypes().contains(type))
            {
                // Not a whitelisted or is blacklisted type.
                return;
            }
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
        return Type.CONSUME;
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
     * Gets potion effect types.
     *
     * @return the potion effect types
     */
    public Set<PotionEffectType> getPotionEffectTypes()
    {
        return potionEffectTypes;
    }


    /**
     * Sets potion effect types.
     *
     * @param potionEffectTypes the potion effect types
     */
    public void setPotionEffectTypes(Set<PotionEffectType> potionEffectTypes)
    {
        this.potionEffectTypes = potionEffectTypes;
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
    private Set<Material> materialSet;

    /**
     * Set of potion effects. Only if material set contains bottle of enchantment.
     */
    @Expose
    private Set<PotionEffectType> potionEffectTypes;

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
