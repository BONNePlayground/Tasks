//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects.options;


import com.google.gson.annotations.Expose;
import org.bukkit.inventory.ItemStack;


/**
 * An option for Icon setting and changing.
 */
public class IconOption implements Option
{
    /**
     * @return OptionType.ICON
     */
    @Override
    public OptionType getType()
    {
        return OptionType.ICON;
    }


    /**
     * Gets Icon.
     *
     * @return the Icon
     */
    public ItemStack getIcon()
    {
        return icon;
    }


    /**
     * Sets Icon.
     *
     * @param icon the Icon
     */
    public void setIcon(ItemStack icon)
    {
        this.icon = icon;
    }


    /**
     * Stores the Icon option.
     */
    @Expose
    private ItemStack icon;
}
