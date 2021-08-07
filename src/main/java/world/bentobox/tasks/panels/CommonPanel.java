//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.panels;


import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import java.util.*;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskBundleObject;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.managers.TasksManager;


public abstract class CommonPanel
{
    /**
     * This is default constructor for all classes that extends CommonPanel.
     *
     * @param addon TasksAddon instance.
     * @param user User who opens panel.
     */
    protected CommonPanel(TasksAddon addon, User user, World world)
    {
        this.addon = addon;
        this.world = world;
        this.manager = addon.getAddonManager();
        this.user = user;

        this.parentPanel = null;
    }


    /**
     * This is default constructor for all classes that extends CommonPanel.
     *
     * @param parentPanel Parent panel of current panel.
     */
    protected CommonPanel(CommonPanel parentPanel)
    {
        this.addon = parentPanel.addon;
        this.manager = parentPanel.manager;
        this.user = parentPanel.user;
        this.world = parentPanel.world;

        this.parentPanel = parentPanel;
    }


    /**
     * This method allows to build panel.
     */
    public abstract void build();


// ---------------------------------------------------------------------
// Section: Common methods
// ---------------------------------------------------------------------


    /**
     * This class generates given task tier description based on input parameters.
     *
     * @param task Tasks which description must be generated.
     * @param isActive Boolean that indicates if task is active.
     * @param isUnlocked Boolean that indicates if task is unlocked.
     * @param islandLevel Long that shows island level.
     * @return List of strings that describes task tier.
     */
    protected List<String> generateTaskDescription(TaskObject task,
        boolean isActive,
        boolean isUnlocked,
        double islandLevel)
    {
        return Collections.emptyList();
    }


    /**
     * Admin should see simplified view. It is not necessary to view all unnecessary things.
     *
     * @param taskObject Task which description must be generated.
     * @return List of strings that describes taskObject tier.
     */
    protected List<String> generateTaskDescription(TaskObject taskObject)
    {
        return Collections.emptyList();
    }


    /**
     * This method generates task type in a single string with using user translations.
     *
     * @param taskObject Generator which treasures must be generated.
     * @return String that contains all rewards in user locale.
     */
    private String generateTypeDescription(TaskObject taskObject)
    {
        return "";
    }


    /**
     * This method generates rewards in a single string with using user translations.
     *
     * @param taskObject Generator which treasures must be generated.
     * @return String that contains all rewards in user locale.
     */
    private String generateRewardsDescription(TaskObject taskObject)
    {
        return "";
    }


    /**
     * This method generates list of requirements in a single string with using user translations.
     *
     * @param taskObject task which requirements must be generated.
     * @param isUnlocked Boolean that shows if level and permissions must be showed.
     * @param islandLevel Island Level for checking if level requirement should be showed.
     * @param complete Shows if all requirements must be showed. (admin)
     * @return String that contains requirements for this taskObject.
     */
    private String generateRequirementsDescription(TaskObject taskObject,
        boolean isUnlocked,
        Long islandLevel,
        boolean complete)
    {
        return "";
    }


    /**
     * This method generates task status string with using user translations.
     *
     * @param taskObject Generator which status must be generated.
     * @param isActive Indicate if task is activated by player.
     * @param isUnlocked Indicate if task is unlocked by player.
     * @return String that contains status for this task.
     */
    private String generateStatusDescription(TaskObject taskObject,
        boolean isActive,
        boolean isUnlocked)
    {
        return "";
    }


    /**
     * Admin should see simplified view. It is not necessary to view all unnecessary things.
     *
     * @param taskBundleObject Bundle which description must be generated.
     * @return List of strings that describes bundle.
     */
    protected List<String> generateBundleDescription(TaskBundleObject taskBundleObject)
    {
        return Collections.emptyList();
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------

    /**
     * This variable allows to access addon object.
     */
    protected final TasksAddon addon;

    /**
     * This variable allows to access addon manager object.
     */
    protected final TasksManager manager;

    /**
     * This variable holds user who opens panel. Without it panel cannot be opened.
     */
    protected final User user;

    /**
     * This variable holds world where panel is operating.
     */
    protected final World world;

    /**
     * This variable allows to create nested panel structure.
     */
    @Nullable
    protected final CommonPanel parentPanel;
}
