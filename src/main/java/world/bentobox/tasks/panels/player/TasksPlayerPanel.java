//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.panels.player;


import org.bukkit.Material;
import java.util.*;
import java.util.stream.Collectors;

import world.bentobox.bentobox.api.panels.PanelItem;
import world.bentobox.bentobox.api.panels.builders.PanelBuilder;
import world.bentobox.bentobox.api.panels.builders.PanelItemBuilder;
import world.bentobox.tasks.configs.Settings;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.database.objects.options.Option;
import world.bentobox.tasks.panels.CommonPanel;
import world.bentobox.tasks.utils.Constants;
import world.bentobox.tasks.utils.GuiUtils;
import world.bentobox.tasks.utils.Utils;


public class TasksPlayerPanel extends CommonPanel
{
    // ---------------------------------------------------------------------
    // Section: Internal Constructor
    // ---------------------------------------------------------------------


    /**
     * This is internal constructor. It is used internally in current class to avoid creating objects everywhere.
     *
     */
    private TasksPlayerPanel(CommonPanel parent, TaskDataObject islandData, Type type)
    {
        super(parent);
        // Get valid user island data
        this.islandData = islandData;

        // Store tasks in local list to avoid building it every time.

        switch (type)
        {
            case TASKS_PANEL -> {
                this.allTasks = this.manager.getAllTasks(this.world).stream().
                    filter(task -> task.getOptionList().stream().noneMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()) ||
                        Option.OptionType.WEEKLY_RESET.equals(option.getType()) ||
                        Option.OptionType.MONTHLY_RESET.equals(option.getType()) ||
                        Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    toList();
                this.availableTasks = this.manager.getAvailableTaskList(this.world, this.islandData).stream().
                    filter(task -> task.getOptionList().stream().noneMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()) ||
                            Option.OptionType.WEEKLY_RESET.equals(option.getType()) ||
                            Option.OptionType.MONTHLY_RESET.equals(option.getType()) ||
                            Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    toList();
            }
            case DAILY_PANEL -> {
                this.allTasks = this.manager.getAllTasks(this.world).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()))).
                    toList();
                this.availableTasks = this.manager.getAvailableTaskList(this.world, this.islandData).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()))).
                    toList();
            }
            case WEEKLY_PANEL -> {
                this.allTasks = this.manager.getAllTasks(this.world).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.WEEKLY_RESET.equals(option.getType()))).
                    toList();
                this.availableTasks = this.manager.getAvailableTaskList(this.world, this.islandData).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.WEEKLY_RESET.equals(option.getType()))).
                    toList();
            }
            case MONTHLY_PANEL -> {
                this.allTasks = this.manager.getAllTasks(this.world).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.MONTHLY_RESET.equals(option.getType()))).
                    toList();
                this.availableTasks = this.manager.getAvailableTaskList(this.world, this.islandData).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.MONTHLY_RESET.equals(option.getType()))).
                    toList();
            }
            case YEARLY_PANEL -> {
                this.allTasks = this.manager.getAllTasks(this.world).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    toList();
                this.availableTasks = this.manager.getAvailableTaskList(this.world, this.islandData).stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    toList();
            }
            default -> {
                this.allTasks = Collections.emptyList();
                this.availableTasks = Collections.emptyList();
            }
        }

        // Stores how many elements will be in display.
        // Row Count should be influenced by border block type.

        this.rowCount = this.allTasks.size() > 14 ? 3 : this.allTasks.size() > 7 ? 2 : 1;

        this.panel = type;
    }


    /**
     * This method builds this GUI.
     */
    @Override
    public void build()
    {
        if (this.allTasks.isEmpty())
        {
            Utils.sendMessage(this.user, this.user.getTranslation(
                Constants.ERRORS + "no-tasks-in-world",
                Constants.WORLD, this.world.getName()));

            if (this.parentPanel != null)
            {
                this.parentPanel.build();
            }

            return;
        }

        if (this.islandData == null)
        {
            Utils.sendMessage(this.user, this.user.getTranslation("general.errors.no-island"));
            return;
        }

        // PanelBuilder is a BentoBox API that provides ability to easy create Panels.
        PanelBuilder panelBuilder = new PanelBuilder().
            user(this.user).
            name(this.user.getTranslation(Constants.TITLE + this.panel.name().toLowerCase()));

        GuiUtils.fillBorder(panelBuilder,
            this.rowCount + 2,
            this.addon.getSettings().getBorderBlock(),
            this.addon.getSettings().getBorderBlockName());

        this.fillTasks(panelBuilder);

        panelBuilder.item((this.rowCount + 2) * 9 - 1, this.createButton(Action.RETURN));

        // Build panel.
        panelBuilder.build();
    }


// ---------------------------------------------------------------------
// Section: Methods
// ---------------------------------------------------------------------


    /**
     * This method creates panel item for given button type.
     *
     * @param button Button type.
     * @return Clickable PanelItem button.
     */
    // TODO: ADD THIS BUTTON.
    private PanelItem createButton(Button button)
    {
        final String reference = Constants.BUTTON + button.name().toLowerCase();
        String name = this.user.getTranslation(reference + ".name");
        List<String> description = new ArrayList<>();
        description.add(this.user.getTranslationOrNothing(reference + ".description"));

        description.add("");
        if (this.activeFilterButton != button)
        {
            description.add(this.user.getTranslation(Constants.TIPS + "click-to-filter-enable"));
        }
        else
        {
            description.add(this.user.getTranslation(Constants.TIPS + "click-to-filter-disable"));
        }

        PanelItem.ClickHandler clickHandler = (panel, user, clickType, i) -> {
            this.activeFilterButton = this.activeFilterButton == button ? Button.NONE : button;
            // Rebuild everything.
            this.build();

            // Always return true.
            return true;
        };

        Material material = Material.PAPER;

        switch (button)
        {
            case NONE:
            {
                material = Material.REDSTONE;
                break;
            }
            case AVAILABLE:
            {
                material = Material.GREEN_STAINED_GLASS_PANE;
                break;
            }
        }

        return new PanelItemBuilder().
            name(name).
            description(description).
            icon(material).
            clickHandler(clickHandler).
            glow(this.activeFilterButton == button).
            build();
    }


    /**
     * This method creates panel item for given button type.
     *
     * @param button Button type.
     * @return Clickable PanelItem button.
     */
    private PanelItem createButton(Action button)
    {
        final String reference = Constants.BUTTON + button.name().toLowerCase();
        String name = this.user.getTranslation(reference + ".name");
        List<String> description = new ArrayList<>();

        PanelItem.ClickHandler clickHandler = (panel, user, clickType, i) -> true;

        Material icon = Material.PAPER;
        int count = 1;

        switch (button)
        {
            case RETURN -> {
                description.add(this.user.getTranslationOrNothing(reference + ".description"));
                description.add("");

                if (this.parentPanel != null)
                {
                    description.add(this.user.getTranslation(Constants.TIPS + "click-to-return"));
                }
                else
                {
                    description.add(this.user.getTranslation(Constants.TIPS + "click-to-quit"));
                }

                clickHandler = (panel, user, clickType, i) ->
                {
                    if (this.parentPanel != null)
                    {
                        this.parentPanel.build();
                    }
                    else
                    {
                        user.closeInventory();
                    }
                    return true;
                };

                icon = Material.OAK_DOOR;
            }
            case PREVIOUS -> {
                count = GuiUtils.getPreviousPage(this.pageIndex, this.maxPageIndex);
                description.add(this.user.getTranslationOrNothing(reference + ".description",
                    Constants.NUMBER, String.valueOf(count)));

                // add empty line
                description.add("");
                description.add(this.user.getTranslation(Constants.TIPS + "click-to-previous"));

                clickHandler = (panel, user, clickType, i) ->
                {
                    this.pageIndex--;
                    this.build();
                    return true;
                };

                icon = Material.TIPPED_ARROW;
            }
            case NEXT -> {
                count = GuiUtils.getNextPage(this.pageIndex, this.maxPageIndex);
                description.add(this.user.getTranslationOrNothing(reference + ".description",
                    Constants.NUMBER, String.valueOf(count)));

                // add empty line
                description.add("");
                description.add(this.user.getTranslation(Constants.TIPS + "click-to-next"));

                clickHandler = (panel, user, clickType, i) ->
                {
                    this.pageIndex++;
                    this.build();
                    return true;
                };

                icon = Material.TIPPED_ARROW;
            }
        }

        return new PanelItemBuilder().
            name(name).
            description(description).
            icon(icon).
            amount(count).
            clickHandler(clickHandler).
            build();
    }


    /**
     * This method fills panel builder empty spaces with generator tiers and adds previous next buttons if necessary.
     *
     * @param panelBuilder PanelBuilder that is necessary to populate.
     */
    private void fillTasks(PanelBuilder panelBuilder)
    {
        int MAX_ELEMENTS = this.rowCount * 7;

        List<TaskObject> filteredList;

        if (this.activeFilterButton == Button.AVAILABLE)
        {
            filteredList = this.availableTasks;
        }
        else
        {
            filteredList = this.allTasks;
        }

        this.maxPageIndex = (int) Math.ceil(1.0 * filteredList.size() / MAX_ELEMENTS) - 1;

        if (this.pageIndex < 0)
        {
            this.pageIndex = filteredList.size() / MAX_ELEMENTS;
        }
        else if (this.pageIndex > (filteredList.size() / MAX_ELEMENTS))
        {
            this.pageIndex = 0;
        }

        // Currently I do not want to deal with situations, when someone disables bored and have more then 36 generators.
        if (filteredList.size() > MAX_ELEMENTS)
        {
            // Navigation buttons if necessary

            panelBuilder.item(9, this.createButton(Action.PREVIOUS));
            panelBuilder.item(17, this.createButton(Action.NEXT));
        }

        int objectIndex = MAX_ELEMENTS * this.pageIndex;

        // I want first row to be only for navigation and return button.
        int index = 10;

        while (objectIndex < ((this.pageIndex + 1) * MAX_ELEMENTS) &&
            objectIndex < filteredList.size() &&
            index < 36)
        {
            if (!panelBuilder.slotOccupied(index))
            {
                panelBuilder.item(index,
                    this.createTaskButton(filteredList.get(objectIndex++)));
            }

            index++;
        }
    }


    /**
     * This method creates button for taskObject.
     *
     * @param taskObject taskObject which button must be created.
     * @return PanelItem for taskObject.
     */
    private PanelItem createTaskButton(TaskObject taskObject)
    {
        final boolean activeTask = this.islandData.getActiveTasks().contains(taskObject.getUniqueId());
        final boolean unlocked = this.availableTasks.contains(taskObject);

        List<String> description = this.generateTaskDescription(taskObject, activeTask, unlocked);

        return new PanelItemBuilder().
            name(taskObject.getName()).
            icon(taskObject.getIcon()).
            description(description).
            clickHandler((panel, user1, clickType, slot) -> {

                // Click handler should work only if user has a permission to change anything.
                // Otherwise just to view.

                if (clickType.isShiftClick())
                {
                    this.processClick(this.addon.getSettings().getShiftClickAction(),
                        activeTask,
                        unlocked,
                        taskObject);
                }
                else if (this.addon.getSettings().getClickAction() != Settings.GuiAction.NONE)
                {
                    this.processClick(this.addon.getSettings().getClickAction(),
                        activeTask,
                        unlocked,
                        taskObject);
                }
                else if (clickType.isRightClick())
                {
                    this.processClick(this.addon.getSettings().getRightClickAction(),
                        activeTask,
                        unlocked,
                        taskObject);
                }
                else
                {
                    this.processClick(this.addon.getSettings().getLeftClickAction(),
                        activeTask,
                        unlocked,
                        taskObject);
                }

                return true;
            }).
            glow(activeTask).
            build();
    }


    /**
     * This class generates given task tier description based on input parameters.
     *
     * @param task Task which description must be generated.
     * @param isActive Boolean that indicates if task is active.
     * @param isUnlocked Boolean that indicates if task is unlocked.
     * @return List of strings that describes task tier.
     */
    protected List<String> generateTaskDescription(TaskObject task,
        boolean isActive,
        boolean isUnlocked)
    {
        List<String> description = super.generateTaskDescription(task,
            isActive,
            isUnlocked,
            this.islandData);

        description.add("");

        if (this.addon.getSettings().getClickAction() != Settings.GuiAction.NONE)
        {
            description.addAll(
                this.generateToolTips(this.addon.getSettings().getClickAction(),
                    Constants.TIPS,
                    isUnlocked,
                    isActive));
        }
        else
        {
            // Add left click tooltip
            description.addAll(
                this.generateToolTips(this.addon.getSettings().getLeftClickAction(),
                    Constants.TIPS + "left-",
                    isUnlocked,
                    isActive));

            // Add right click tooltip
            description.addAll(
                this.generateToolTips(this.addon.getSettings().getRightClickAction(),
                    Constants.TIPS + "right-",
                    isUnlocked,
                    isActive));
        }

        // Add shift click tooltip.
        description.addAll(
            this.generateToolTips(this.addon.getSettings().getShiftClickAction(),
                Constants.TIPS + "shift-",
                isUnlocked,
                isActive));

        return description;
    }


    /**
     * This method generated tool tips based on given parameters for given action.
     *
     * @param action Action that is performed.
     * @param reference Reference to translation string.
     * @param isUnlocked Boolean that indicate if generator is unlocked.
     * @param isActive Boolean that indicate if generator is active.
     * @return List of string that contains tool tips.
     */
    private List<String> generateToolTips(Settings.GuiAction action,
        String reference,
        boolean isUnlocked,
        boolean isActive)
    {
        List<String> description = new ArrayList<>(3);

        switch (action)
        {
            case VIEW -> description.add(this.user.getTranslation(reference + "click-to-view"));
            case TOGGLE -> {
                if (isUnlocked)
                {
                    if (isActive)
                    {
                        description.add(this.user.getTranslation(reference + "click-to-deactivate"));
                    }
                    else
                    {
                        description.add(this.user.getTranslation(reference + "click-to-activate"));
                    }
                }
            }
        }

        return description;
    }


    /**
     * This method process given click action.
     *
     * @param action Action that must be performed
     * @param isActive Indicates if task is active.
     * @param isUnlocked Indicates if task is unlocked.
     * @param taskObject Task Object that will be targeted.
     */
    private void processClick(Settings.GuiAction action,
        boolean isActive,
        boolean isUnlocked,
        TaskObject taskObject)
    {
        switch (action)
        {
            case VIEW -> {
                // Open view panel.
                //TaskViewPanel.openPanel(this, taskObject);
            }
            case TOGGLE -> {
                if (isUnlocked)
                {
                    if (isActive)
                    {
                        this.manager.onTaskStop(taskObject.getUniqueId(), user.getPlayer(), this.islandData);
                        // Rebuild whole gui.
                        this.build();
                    }
                    else if (this.manager.validateTask(taskObject, user, this.islandData))
                    {
                        this.manager.onTaskStart(taskObject.getUniqueId(), user.getPlayer(), this.islandData);
                        // Build whole gui.
                        this.build();
                    }
                }
            }
        }
    }


    /**
     * This method is used to open UserPanel outside this class. It will be much easier to open panel with single method
     * call then initializing new object.
     *
     * @param parent Parent GUI
     * @param islandData Island Data object
     * @param panel Panel type.
     */
    public static void openPanel(CommonPanel parent, TaskDataObject islandData, Type panel)
    {
        new TasksPlayerPanel(parent, islandData, panel).build();
    }


    // ---------------------------------------------------------------------
    // Section: Enums
    // ---------------------------------------------------------------------


    /**
     * This enum holds variable that allows to switch between button creation.
     */
    private enum Button
    {
        /**
         * Button that on click shows only active tasks.
         */
        AVAILABLE,
        /**
         * Filter for none.
         */
        NONE
    }


    /**
     * This enum holds variable that allows to switch between actions.
     */
    private enum Action
    {
        /**
         * Return button that exists GUI.
         */
        RETURN,
        /**
         * Allows selecting previous page in multi-page situation.
         */
        PREVIOUS,
        /**
         * Allows selecting next page in multi-page situation.
         */
        NEXT
    }


    /**
     * This enum stores all available options for this panel.
     */
    public enum Type
    {
        TASKS_PANEL,
        DAILY_PANEL,
        WEEKLY_PANEL,
        MONTHLY_PANEL,
        YEARLY_PANEL
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * This variable holds user's island data.
     */
    private final TaskDataObject islandData;

    /**
     * This variable stores all tasks the given world.
     */
    private final List<TaskObject> allTasks;

    /**
     * This variable stores all tasks the given world.
     */
    private final List<TaskObject> availableTasks;

    /**
     * This variable stores current panel type.
     */
    private final Type panel;

    /**
     * Stores how many elements will be in display.
     */
    private final int rowCount;

    /**
     * This variable holds current pageIndex for multi-page choosing.
     */
    private int pageIndex;

    /**
     * This variable holds current pageIndex for multi-page choosing.
     */
    private int maxPageIndex;

    /**
     * This variable holds current filter button.
     */
    private Button activeFilterButton;
}
