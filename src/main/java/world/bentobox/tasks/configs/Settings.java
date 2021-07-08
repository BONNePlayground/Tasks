package world.bentobox.tasks.configs;


import java.util.HashSet;
import java.util.Set;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;


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


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------


    @ConfigComment("Default number of active tasks that can be done at once.")
    @ConfigComment("0 or less will mean that there is no limitation.")
    @ConfigComment("Can be changed with a permission `[gamemode].tasks.active-tasks.[number]`")
    @ConfigComment("By default it is 1.")
    @ConfigEntry(path = "default-active-tasks")
    private int defaultActiveTaskCount = 1;

    @ConfigComment("")
    @ConfigComment("This list stores GameModes in which QuestsAddon should not work.")
    @ConfigComment("To disable addon it is necessary to write its name in new line that starts with -. Example:")
    @ConfigComment("disabled-gamemodes:")
    @ConfigComment(" - BSkyBlock")
    @ConfigEntry(path = "disabled-gamemodes")
    private Set<String> disabledGameModes = new HashSet<>();

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
