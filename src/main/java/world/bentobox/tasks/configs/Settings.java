package world.bentobox.tasks.configs;


import com.google.gson.annotations.JsonAdapter;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.configuration.serialization.SerializableAs;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;
import world.bentobox.bentobox.database.objects.adapters.Adapter;
import world.bentobox.tasks.database.adapters.TimeZoneAdapter;


/**
 * Settings that implements ConfigObject is powerful and dynamic Config Objects that
 * does not need custom parsing. If it is correctly loaded, all its values will be available.
 *
 * Without Getter and Setter this class will not work.
 *
 * To specify location for config object to be stored, you should use @StoreAt(filename="{config file name}", path="{Path to your addon}")
 * To save comments in config file you should use @ConfigComment("{message}") that adds any message you want to be in file.
 */
@StoreAt(filename="config.yml", path="addons/Tasks")
@ConfigComment("Tasks Addon Configuration {$version}")
@ConfigComment("This config file is dynamic and saved when the server is shutdown.")
@ConfigComment("")
public class Settings implements ConfigObject
{
    // ---------------------------------------------------------------------
    // Section: Getters and Setters
    // ---------------------------------------------------------------------


    /**
     * This method returns the disabledGameModes value.
     *
     * @return the value of disabledGameModes.
     */
    public Set<String> getDisabledGameModes()
    {
        return disabledGameModes;
    }


    /**
     * This method sets the disabledGameModes value.
     *
     * @param disabledGameModes the disabledGameModes new value.
     */
    public void setDisabledGameModes(Set<String> disabledGameModes)
    {
        this.disabledGameModes = disabledGameModes;
    }


    /**
     * Gets player main command.
     *
     * @return the player main command
     */
    public String getPlayerMainCommand()
    {
        return playerMainCommand;
    }


    /**
     * Sets player main command.
     *
     * @param playerMainCommand the player main command
     */
    public void setPlayerMainCommand(String playerMainCommand)
    {
        this.playerMainCommand = playerMainCommand;
    }


    /**
     * Gets admin main command.
     *
     * @return the admin main command
     */
    public String getAdminMainCommand()
    {
        return adminMainCommand;
    }


    /**
     * Sets admin main command.
     *
     * @param adminMainCommand the admin main command
     */
    public void setAdminMainCommand(String adminMainCommand)
    {
        this.adminMainCommand = adminMainCommand;
    }


    /**
     * Gets default active task count.
     *
     * @return the default active task count
     */
    public int getDefaultActiveTaskCount()
    {
        return defaultActiveTaskCount;
    }


    /**
     * Sets default active task count.
     *
     * @param defaultActiveTaskCount the default active task count
     */
    public void setDefaultActiveTaskCount(int defaultActiveTaskCount)
    {
        this.defaultActiveTaskCount = defaultActiveTaskCount;
    }


    /**
     * Gets time zone.
     *
     * @return the time zone
     */
    public TimeZone getTimeZone()
    {
        return timeZone;
    }


    /**
     * Sets time zone.
     *
     * @param timeZone the time zone
     */
    public void setTimeZone(TimeZone timeZone)
    {
        this.timeZone = timeZone;
    }


    /**
     * Gets date format.
     *
     * @return the date format
     */
    public String getDateFormat()
    {
        return dateFormat;
    }


    /**
     * Sets date format.
     *
     * @param dateFormat the date format
     */
    public void setDateFormat(String dateFormat)
    {
        this.dateFormat = dateFormat;
    }


    /**
     * Is show filters boolean.
     *
     * @return the boolean
     */
    public boolean isShowFilters()
    {
        return showFilters;
    }


    /**
     * Sets show filters.
     *
     * @param showFilters the show filters
     */
    public void setShowFilters(boolean showFilters)
    {
        this.showFilters = showFilters;
    }


    /**
     * Gets border block.
     *
     * @return the border block
     */
    public Material getBorderBlock()
    {
        return borderBlock;
    }


    /**
     * Sets border block.
     *
     * @param borderBlock the border block
     */
    public void setBorderBlock(Material borderBlock)
    {
        this.borderBlock = borderBlock;
    }


    /**
     * Gets border block name.
     *
     * @return the border block name
     */
    public String getBorderBlockName()
    {
        return borderBlockName;
    }


    /**
     * Sets border block name.
     *
     * @param borderBlockName the border block name
     */
    public void setBorderBlockName(String borderBlockName)
    {
        this.borderBlockName = borderBlockName;
    }


    /**
     * Gets click action.
     *
     * @return the click action
     */
    public GuiAction getClickAction()
    {
        return clickAction;
    }


    /**
     * Sets click action.
     *
     * @param clickAction the click action
     */
    public void setClickAction(GuiAction clickAction)
    {
        this.clickAction = clickAction;
    }


    /**
     * Gets left click action.
     *
     * @return the left click action
     */
    public GuiAction getLeftClickAction()
    {
        return leftClickAction;
    }


    /**
     * Sets left click action.
     *
     * @param leftClickAction the left click action
     */
    public void setLeftClickAction(GuiAction leftClickAction)
    {
        this.leftClickAction = leftClickAction;
    }


    /**
     * Gets right click action.
     *
     * @return the right click action
     */
    public GuiAction getRightClickAction()
    {
        return rightClickAction;
    }


    /**
     * Sets right click action.
     *
     * @param rightClickAction the right click action
     */
    public void setRightClickAction(GuiAction rightClickAction)
    {
        this.rightClickAction = rightClickAction;
    }


    /**
     * Gets shift click action.
     *
     * @return the shift click action
     */
    public GuiAction getShiftClickAction()
    {
        return shiftClickAction;
    }


    /**
     * Sets shift click action.
     *
     * @param shiftClickAction the shift click action
     */
    public void setShiftClickAction(GuiAction shiftClickAction)
    {
        this.shiftClickAction = shiftClickAction;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * This enum holds options for choosing on click setting.
     */
    public enum GuiAction
    {
        /**
         * Opens detailed menu GUI.
         */
        VIEW,
        /**
         * Tries to activate/deactivate task
         */
        TOGGLE,
        /**
         * No action is done.
         */
        NONE
    }


    @ConfigComment("Default number of active tasks that can be done at once.")
    @ConfigComment("0 or less will mean that there is no limitation.")
    @ConfigComment("Can be changed with a permission `[gamemode].tasks.active-tasks.[number]`")
    @ConfigComment("By default it is 1.")
    @ConfigEntry(path = "default-active-tasks")
    private int defaultActiveTaskCount = 1;

    @ConfigComment("Stores the TimeZone for the all date calculations.")
    @ConfigComment("By default it is System Timezone.")
    @ConfigEntry(path = "timezone")
    @Adapter(TimeZoneAdapter.class)
    private TimeZone timeZone = TimeZone.getDefault();

    @ConfigComment("Stores the date format used from template importing.")
    @ConfigComment("Date format for importing.")
    @ConfigEntry(path = "date-format")
    private String dateFormat = "YYYY-MM-dd";

    @ConfigComment("")
    @ConfigComment("This list stores GameModes in which QuestsAddon should not work.")
    @ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
    @ConfigComment("disabled-gamemodes:")
    @ConfigComment(" - BSkyBlock")
    @ConfigEntry(path = "disabled-gamemodes")
    private Set<String> disabledGameModes = new HashSet<>();

    @ConfigComment("This allows to toggle if filters in Tasks User Panel should be showed.")
    @ConfigEntry(path = "gui.show-filters")
    private boolean showFilters = true;

    @ConfigComment("This allows to change main border block in player panel.")
    @ConfigEntry(path = "gui.border-block")
    private Material borderBlock = Material.MAGENTA_STAINED_GLASS_PANE;

    @ConfigComment("This allows to change border block display name.")
    @ConfigEntry(path = "gui.border-block-name")
    private String borderBlockName = " ";

    @ConfigComment("Allows to change what action will be performed when user clicks on task.")
    @ConfigComment("NOTE!! This action will overwrite left-click and right-click actions.")
    @ConfigComment("Supported values:")
    @ConfigComment("- TOGGLE - tries to activate/deactivate task.")
    @ConfigComment("- VIEW - opens detailed view for task.")
    @ConfigComment("- NONE - no actions are performed.")
    @ConfigEntry(path = "gui.actions.click-action")
    private GuiAction clickAction = GuiAction.NONE;

    @ConfigComment("Allows to change what action will be performed when user left clicks on task.")
    @ConfigComment("NOTE!! This action will be overwritten by click-action.")
    @ConfigComment("Supported values:")
    @ConfigComment("- TOGGLE - tries to activate/deactivate task.")
    @ConfigComment("- VIEW - opens detailed view for task.")
    @ConfigComment("- NONE - no actions are performed.")
    @ConfigEntry(path = "gui.actions.left-click-action")
    private GuiAction leftClickAction = GuiAction.TOGGLE;

    @ConfigComment("Allows to change what action will be performed when user right clicks on task.")
    @ConfigComment("NOTE!! This action will be overwritten by click-action.")
    @ConfigComment("Supported values:")
    @ConfigComment("- TOGGLE - tries to activate/deactivate task.")
    @ConfigComment("- VIEW - opens detailed view for task.")
    @ConfigComment("- NONE - no actions are performed.")
    @ConfigEntry(path = "gui.actions.right-click-action")
    private GuiAction rightClickAction = GuiAction.VIEW;

    @ConfigComment("Allows to change what action will be performed when user shift-clicks on task.")
    @ConfigComment("Supported values:")
    @ConfigComment("- TOGGLE - tries to activate/deactivate task.")
    @ConfigComment("- VIEW - opens detailed view for task.")
    @ConfigComment("- NONE - no actions are performed.")
    @ConfigEntry(path = "gui.actions.shift-click-action")
    private GuiAction shiftClickAction = GuiAction.NONE;

    @ConfigComment("Player main sub-command to access the addon.")
    @ConfigComment("This command label will be required to write after gamemode admin command label, f.e. /[label] tasks")
    @ConfigComment("Each alias must be separated with an empty space.")
    @ConfigEntry(path = "commands.player.main", needsRestart = true)
    private String playerMainCommand = "tasks";

    @ConfigComment("Admin main sub-command to access the addon.")
    @ConfigComment("This command label will be required to write after gamemode admin command label, f.e. /[label] tasks")
    @ConfigComment("Each alias must be separated with an empty space.")
    @ConfigEntry(path = "commands.admin.main", needsRestart = true)
    private String adminMainCommand = "tasks";
}
