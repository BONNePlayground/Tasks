//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.panels.player;


import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import world.bentobox.bentobox.api.panels.PanelItem;
import world.bentobox.bentobox.api.panels.builders.PanelBuilder;
import world.bentobox.bentobox.api.panels.builders.PanelItemBuilder;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.database.objects.options.Option;
import world.bentobox.tasks.panels.CommonPanel;
import world.bentobox.tasks.utils.Constants;
import world.bentobox.tasks.utils.GuiUtils;
import world.bentobox.tasks.utils.Utils;


public class MainPlayerPanel extends CommonPanel
{
    // ---------------------------------------------------------------------
    // Section: Internal Constructor
    // ---------------------------------------------------------------------


    /**
     * This is internal constructor. It is used internally in current class to avoid creating objects everywhere.
     *
     * @param addon TasksAddon object.
     */
    private MainPlayerPanel(TasksAddon addon,
        World world,
        User user)
    {
        super(addon, user, world);
        this.island = this.addon.getIslands().getIsland(world, user);

        // Get valid user island data
        this.islandData = this.manager.validateIslandData(this.island);

        // Store tasks in local list to avoid building it every time.
        this.availableTasks = this.manager.getAvailableTaskList(world, this.user, this.islandData);
        this.allTasks = this.manager.getAccessibleTaskList(world, this.islandData);
    }


    /**
     * This method builds this GUI.
     */
    @Override
    public void build()
    {
        if (this.island == null || this.islandData == null)
        {
            Utils.sendMessage(this.user, this.user.getTranslation("general.errors.no-island"));
            return;
        }

        // PanelBuilder is a BentoBox API that provides ability to easy create Panels.
        PanelBuilder panelBuilder = new PanelBuilder().
            user(this.user).
            name(this.user.getTranslation(Constants.TITLE + "main-player-panel"));

        GuiUtils.fillBorder(panelBuilder,
            4,
            this.addon.getSettings().getBorderBlock(),
            this.addon.getSettings().getBorderBlockName());

        panelBuilder.item(12, this.createButton(Button.TASKS));
        panelBuilder.item(14, this.createButton(Button.YEARLY));

        panelBuilder.item(20, this.createButton(Button.DAILY));
        panelBuilder.item(22, this.createButton(Button.WEEKLY));
        panelBuilder.item(24, this.createButton(Button.MONTHLY));

        panelBuilder.item(35, this.createButton(Action.RETURN));

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
    private PanelItem createButton(Button button)
    {
        final String reference = Constants.BUTTON + button.name().toLowerCase();
        String name = this.user.getTranslation(reference + ".name");
        List<String> description = new ArrayList<>();

        long available = 0;
        long count = 0;

        switch (button)
        {
            case TASKS -> {
                available = this.availableTasks.stream().
                    filter(task -> task.getOptionList().stream().noneMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()) ||
                            Option.OptionType.WEEKLY_RESET.equals(option.getType()) ||
                            Option.OptionType.MONTHLY_RESET.equals(option.getType()) ||
                            Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    count();
                count = this.allTasks.stream().
                    filter(task -> task.getOptionList().stream().noneMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()) ||
                            Option.OptionType.WEEKLY_RESET.equals(option.getType()) ||
                            Option.OptionType.MONTHLY_RESET.equals(option.getType()) ||
                            Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    count();
            }
            case DAILY -> {
                available = this.availableTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()))).
                    count();
                count = this.allTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.DAILY_RESET.equals(option.getType()))).
                    count();
            }
            case WEEKLY -> {
                available = this.availableTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.WEEKLY_RESET.equals(option.getType()))).
                    count();
                count = this.allTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.WEEKLY_RESET.equals(option.getType()))).
                    count();
            }
            case MONTHLY -> {
                available = this.availableTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.MONTHLY_RESET.equals(option.getType()))).
                    count();
                count = this.allTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.MONTHLY_RESET.equals(option.getType()))).
                    count();
            }
            case YEARLY -> {
                available = this.availableTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    count();
                count = this.allTasks.stream().
                    filter(task -> task.getOptionList().stream().anyMatch(
                        option -> Option.OptionType.YEARLY_RESET.equals(option.getType()))).
                    count();
            }
        }

        description.add(this.user.getTranslationOrNothing(reference + ".description",
            Constants.AVAILABLE, String.valueOf(available),
            Constants.COUNT, String.valueOf(count)));
        description.add("");

        description.add(this.user.getTranslation(Constants.TIPS + "click-to-view"));

        PanelItem.ClickHandler clickHandler;
        Material material;

        switch (button)
        {
            case TASKS -> {
                material = Material.IRON_SHOVEL;
                clickHandler = (panel, user, clickType, slot) ->
                {
                    TasksPlayerPanel.openPanel(this, this.islandData, TasksPlayerPanel.Type.TASKS_PANEL);
                    return true;
                };
            }
            case DAILY -> {
                material = Material.CLOCK;
                clickHandler = (panel, user, clickType, slot) ->
                {
                    TasksPlayerPanel.openPanel(this, this.islandData, TasksPlayerPanel.Type.DAILY_PANEL);
                    return true;
                };
            }
            case WEEKLY -> {
                material = Material.BELL;
                clickHandler = (panel, user, clickType, slot) ->
                {
                    TasksPlayerPanel.openPanel(this, this.islandData, TasksPlayerPanel.Type.WEEKLY_PANEL);
                    return true;
                };
            }
            case MONTHLY -> {
                material = Material.DROPPER;
                clickHandler = (panel, user, clickType, slot) ->
                {
                    TasksPlayerPanel.openPanel(this, this.islandData, TasksPlayerPanel.Type.MONTHLY_PANEL);
                    return true;
                };
            }
            case YEARLY -> {
                material = Material.CAKE;
                clickHandler = (panel, user, clickType, slot) ->
                {
                    TasksPlayerPanel.openPanel(this, this.islandData, TasksPlayerPanel.Type.YEARLY_PANEL);
                    return true;
                };
            }
            default -> {
                // Just for compilation.
                material = Material.AIR;
                clickHandler = null;
            }
        }

        return new PanelItemBuilder().
            name(name).
            description(description).
            icon(material).
            clickHandler(clickHandler).
            glow(available > 0).
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

        if (button == Action.RETURN)
        {
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

        return new PanelItemBuilder().
            name(name).
            description(description).
            icon(icon).
            amount(count).
            clickHandler(clickHandler).
            build();
    }


    /**
     * This method is used to open UserPanel outside this class. It will be much easier to open panel with single method
     * call then initializing new object.
     *
     * @param addon VisitAddon object
     * @param user User who opens panel
     */
    public static void openPanel(TasksAddon addon,
        World world,
        User user)
    {
        new MainPlayerPanel(addon, world, user).build();
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
         * Button that shows non-repeating tasks.
         */
        TASKS,
        /**
         * Button that shows DAILY tasks.
         */
        DAILY,
        /**
         * Button that shows WEEKLY tasks.
         */
        WEEKLY,
        /**
         * Button that shows MONTHLY tasks.
         */
        MONTHLY,
        /**
         * Button that shows YEARLY tasks.
         */
        YEARLY
    }


    /**
     * This enum holds variable that allows to switch between actions.
     */
    private enum Action
    {
        /**
         * Return button that exists GUI.
         */
        RETURN
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * This variable holds targeted island.
     */
    @Nullable
    private final Island island;

    /**
     * This variable holds user's island data.
     */
    private final TaskDataObject islandData;

    /**
     * This variable stores all available tasks the given world.
     */
    private final List<TaskObject> availableTasks;

    /**
     * This variable stores all tasks the given world.
     */
    private final List<TaskObject> allTasks;
}
