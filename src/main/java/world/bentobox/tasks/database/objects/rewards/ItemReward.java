//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.rewards;


import com.google.gson.annotations.Expose;
import org.bukkit.inventory.ItemStack;


/**
 * Rewarded items.
 */
public class ItemReward implements Reward
{
    /**
     * @return RewardType.ITEM.
     */
    @Override
    public RewardType getType()
    {
        return RewardType.ITEM;
    }


    /**
     * Gets ItemStack.
     *
     * @return the ItemStack
     */
    public ItemStack getItemStack()
    {
        return itemStack;
    }


    /**
     * Sets ItemStack.
     *
     * @param itemStack the ItemStack
     */
    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }


    /**
     * String that stores ItemStack.
     */
    @Expose
    private ItemStack itemStack;
}
