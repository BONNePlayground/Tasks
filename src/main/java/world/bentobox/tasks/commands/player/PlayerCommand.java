package world.bentobox.tasks.commands.player;


import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.panels.player.MainPlayerPanel;
import world.bentobox.tasks.utils.Constants;


/**
 * This class manages {@code /{player_command} tasks} command call.
 */
public class PlayerCommand extends CompositeCommand
{
    /**
     * This is simple constructor for initializing /{player_command} generator command.
     *
     * @param addon StoneGeneratorAddon addon.
     * @param parentCommand Parent Command where we hook our command into.
     */
    public PlayerCommand(TasksAddon addon, CompositeCommand parentCommand)
    {
        super(addon,
            parentCommand,
            addon.getSettings().getPlayerMainCommand().split(" ")[0],
            addon.getSettings().getPlayerMainCommand().split(" "));
    }


    /**
     * Setups anything that is needed for this command. <br/><br/> It is recommended you do the following in this
     * method:
     * <ul>
     * <li>Register any of the sub-commands of this command;</li>
     * <li>Define the permission required to use this command using {@link
     * CompositeCommand#setPermission(String)};</li>
     * <li>Define whether this command can only be run by players or not using {@link
     * CompositeCommand#setOnlyPlayer(boolean)};</li>
     * </ul>
     */
    @Override
    public void setup()
    {
        this.setPermission("tasks");
        this.setParametersHelp(Constants.PLAYER_COMMANDS + "main.parameters");
        this.setDescription(Constants.PLAYER_COMMANDS + "main.description");

        this.setOnlyPlayer(true);
    }


    /**
     * Defines what will be executed when this command is run.
     *
     * @param user the {@link User} who is executing this command.
     * @param label the label which has been used to execute this command. It can be {@link CompositeCommand#getLabel()}
     * or an alias.
     * @param args the command arguments.
     * @return {@code true} if the command executed successfully, {@code false} otherwise.
     */
    @Override
    public boolean execute(User user, String label, List<String> args)
    {
        MainPlayerPanel.openPanel(this.getAddon(), this.getWorld(), user);
        return true;
    }
}