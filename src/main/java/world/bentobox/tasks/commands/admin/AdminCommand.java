package world.bentobox.tasks.commands.admin;


import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.utils.Constants;


/**
 * This class manages {@code /{admin_command} tasks} command call.
 */
public class AdminCommand extends CompositeCommand
{
    /**
     * This is simple constructor for initializing /{admin_command} generator command.
     *
     * @param addon StoneGeneratorAddon addon.
     * @param parentCommand Parent Command where we hook our command into.
     */
    public AdminCommand(TasksAddon addon, CompositeCommand parentCommand)
    {
        super(addon,
            parentCommand,
            addon.getSettings().getAdminMainCommand().split(" ")[0],
            addon.getSettings().getAdminMainCommand().split(" "));
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
        this.setPermission("admin.tasks");
        this.setParametersHelp(Constants.ADMIN_COMMANDS + "main.parameters");
        this.setDescription(Constants.ADMIN_COMMANDS + "main.description");

        this.setOnlyPlayer(false);
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
        this.showHelp(this, user);
        return true;
    }
}