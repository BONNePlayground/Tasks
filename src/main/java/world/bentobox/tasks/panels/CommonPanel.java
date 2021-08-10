//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.panels;


import org.bukkit.ChatColor;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.Util;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskBundleObject;
import world.bentobox.tasks.database.objects.TaskDataObject;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.database.objects.rewards.*;
import world.bentobox.tasks.listeners.tasks.*;
import world.bentobox.tasks.managers.TasksManager;
import world.bentobox.tasks.utils.Constants;
import world.bentobox.tasks.utils.Utils;


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
     * @return List of strings that describes task tier.
     */
    protected List<String> generateTaskDescription(TaskObject task,
        boolean isActive,
        boolean isUnlocked,
        TaskDataObject islandData)
    {
        final String reference = Constants.DESCRIPTIONS + "task.";

        // Get description in single string
        String description = Util.stripSpaceAfterColorCodes(
            ChatColor.translateAlternateColorCodes('&',
                String.join("\n", task.getDescription())));

        // Non-memory optimal code used for easier debugging and nicer code layout for my eye :)
        // Get requirements in single string
        String requirements = this.generateRequirementsDescription(task, isUnlocked, islandData);
        // Get type in single string
        String type = this.generateTypeDescription(task);
        // Get rewards in single string
        String rewards = this.generateRewardsDescription(task);
        // Get status in single string
        String status = this.generateStatusDescription(task, isActive, isUnlocked, islandData);

        if (!description.replaceAll("(?m)^[ \\t]*\\r?\\n", "").isEmpty())
        {
            String returnString = this.user.getTranslation(reference + "lore",
                Constants.REWARDS, rewards,
                Constants.REQUIREMENTS, requirements,
                Constants.TYPE, type,
                Constants.STATUS, status);

            // remove empty lines from the generated text.
            List<String> collect = Arrays.stream(
                returnString.replaceAll("(?m)^[ \\t]*\\r?\\n", "").
                    replaceAll("\\|", "\n").
                    split("\n")).
                collect(Collectors.toList());

            // find and replace description from collected blocks.

            for (int i = 0; i < collect.size(); i++)
            {
                if (collect.get(i).contains(Constants.DESCRIPTION))
                {
                    collect.set(i, collect.get(i).replace(Constants.DESCRIPTION, description));
                }
            }

            return collect;
        }
        else
        {
            String returnString = this.user.getTranslation(reference + "lore",
                Constants.DESCRIPTION, description,
                Constants.REWARDS, rewards,
                Constants.REQUIREMENTS, requirements,
                Constants.TYPE, type,
                Constants.STATUS, status);

            // Remove empty lines and returns as a list.

            return Arrays.stream(returnString.
                    replaceAll("(?m)^[ \\t]*\\r?\\n", "").
                    replaceAll("\\|", "\n").
                    split("\n")).
                collect(Collectors.toList());
        }
    }


    /**
     * This method generates task type in a single string with using user translations.
     *
     * @param taskObject Generator which treasures must be generated.
     * @return String that contains all rewards in user locale.
     */
    private String generateTypeDescription(TaskObject taskObject)
    {
        final String reference = Constants.DESCRIPTIONS + "task.type.";
        String taskBuilder = "";

        switch (taskObject.getTask().getType())
        {
            case BLOCK -> {
                BlockTask task = taskObject.getTask();

                final String localReference;

                if (task.isPlace())
                {
                    localReference = reference + "block-place.";
                }
                else
                {
                    localReference = reference + "block-break.";
                }

                StringBuilder blocks = new StringBuilder();

                if (task.isWhitelist())
                {
                    if (task.getMaterialSet().isEmpty())
                    {
                        blocks.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getBlockCount())));
                    }
                    else if (task.getMaterialSet().size() == 1)
                    {
                        blocks.append(this.user.getTranslationOrNothing(localReference + "whitelist-title-one",
                            Constants.NUMBER, String.valueOf(task.getBlockCount()),
                            Constants.BLOCK, Utils.prettifyObject(this.user, task.getMaterialSet().iterator().next())));
                    }
                    else
                    {
                        blocks.append(this.user.getTranslationOrNothing(localReference + "whitelist-title",
                            Constants.NUMBER, String.valueOf(task.getBlockCount())));
                        blocks.append("\n");
                        task.getMaterialSet().stream().sorted().forEachOrdered(material ->
                            blocks.append(this.user.getTranslationOrNothing(localReference + "whitelist-block",
                                Constants.BLOCK, Utils.prettifyObject(this.user, material))).append("\n"));
                    }
                }
                else
                {
                    if (task.getMaterialSet().isEmpty())
                    {
                        blocks.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getBlockCount())));
                    }
                    else if (task.getMaterialSet().size() == 1)
                    {
                        blocks.append(this.user.getTranslationOrNothing(localReference + "blacklist-title-one",
                            Constants.NUMBER, String.valueOf(task.getBlockCount()),
                            Constants.BLOCK, Utils.prettifyObject(this.user, task.getMaterialSet().iterator().next())));
                    }
                    else
                    {
                        blocks.append(this.user.getTranslationOrNothing(localReference + "blacklist-title",
                            Constants.NUMBER, String.valueOf(task.getBlockCount())));
                        blocks.append("\n");
                        task.getMaterialSet().stream().sorted().forEachOrdered(material ->
                            blocks.append(this.user.getTranslationOrNothing(localReference + "blacklist-block",
                                Constants.BLOCK, Utils.prettifyObject(this.user, material))).append("\n"));
                    }
                }

                taskBuilder = blocks.toString();
            }
            case CONSUME -> {
                ConsumeTask task = taskObject.getTask();

                final String localReference = reference + "consume.";

                StringBuilder items = new StringBuilder();

                if (task.isWhitelist())
                {
                    if (task.getMaterialSet().isEmpty())
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                    }
                    else if (task.getMaterialSet().size() == 1)
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "whitelist-title-one",
                            Constants.NUMBER, String.valueOf(task.getItemCount()),
                            Constants.ITEM, Utils.prettifyObject(this.user, task.getMaterialSet().iterator().next())));
                    }
                    else
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "whitelist-title",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                        items.append("\n");
                        task.getMaterialSet().stream().sorted().forEachOrdered(material ->
                            items.append(this.user.getTranslationOrNothing(localReference + "whitelist-item",
                                Constants.ITEM, Utils.prettifyObject(this.user, material))).append("\n"));
                    }
                }
                else
                {
                    if (task.getMaterialSet().isEmpty())
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                    }
                    else if (task.getMaterialSet().size() == 1)
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "blacklist-title-one",
                            Constants.NUMBER, String.valueOf(task.getItemCount()),
                            Constants.ITEM, Utils.prettifyObject(this.user, task.getMaterialSet().iterator().next())));
                    }
                    else
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "blacklist-title",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                        items.append("\n");
                        task.getMaterialSet().stream().sorted().forEachOrdered(material ->
                            items.append(this.user.getTranslationOrNothing(localReference + "blacklist-item",
                                Constants.ITEM, Utils.prettifyObject(this.user, material))).append("\n"));
                    }
                }

                taskBuilder = items.toString();
            }
            case DAMAGE_DEAL -> {
                DamageDealTask task = taskObject.getTask();

                final String localReference = reference + "damage-deal.";

                taskBuilder = this.user.getTranslationOrNothing(localReference + "title",
                    Constants.NUMBER, String.valueOf(task.getDamageAmount()));
            }
            case DAMAGE_RECEIVE -> {
                DamageReceiveTask task = taskObject.getTask();

                final String localReference = reference + "damage-receive.";

                taskBuilder = this.user.getTranslationOrNothing(localReference + "title",
                    Constants.NUMBER, String.valueOf(task.getDamageAmount()));
            }
            case BREED -> {
                EntityBreedTask task = taskObject.getTask();

                final String localReference = reference + "breed.";

                StringBuilder entities = new StringBuilder();

                if (task.isWhitelist())
                {
                    if (task.getEntityType().isEmpty())
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                    }
                    else if (task.getEntityType().size() == 1)
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-title-one",
                            Constants.NUMBER, String.valueOf(task.getEntityCount()),
                            Constants.ENTITY, Utils.prettifyObject(this.user, task.getEntityType().iterator().next())));
                    }
                    else
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-title",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                        entities.append("\n");
                        task.getEntityType().stream().sorted().forEachOrdered(entity ->
                            entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-entity",
                                Constants.ENTITY, Utils.prettifyObject(this.user, entity))).append("\n"));
                    }
                }
                else
                {
                    if (task.getEntityType().isEmpty())
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                    }
                    else if (task.getEntityType().size() == 1)
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-title-one",
                            Constants.NUMBER, String.valueOf(task.getEntityCount()),
                            Constants.ENTITY, Utils.prettifyObject(this.user, task.getEntityType().iterator().next())));
                    }
                    else
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-title",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                        entities.append("\n");
                        task.getEntityType().stream().sorted().forEachOrdered(entity ->
                            entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-entity",
                                Constants.ENTITY, Utils.prettifyObject(this.user, entity))).append("\n"));
                    }
                }

                taskBuilder = entities.toString();
            }
            case KILL -> {
                EntityKillTask task = taskObject.getTask();

                final String localReference = reference + "kill.";

                StringBuilder entities = new StringBuilder();

                if (task.isWhitelist())
                {
                    if (task.getEntityType().isEmpty())
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                    }
                    else if (task.getEntityType().size() == 1)
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-title-one",
                            Constants.NUMBER, String.valueOf(task.getEntityCount()),
                            Constants.ENTITY, Utils.prettifyObject(this.user, task.getEntityType().iterator().next())));
                    }
                    else
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-title",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                        entities.append("\n");
                        task.getEntityType().stream().sorted().forEachOrdered(entity ->
                            entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-entity",
                                Constants.ENTITY, Utils.prettifyObject(this.user, entity))).append("\n"));
                    }
                }
                else
                {
                    if (task.getEntityType().isEmpty())
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                    }
                    else if (task.getEntityType().size() == 1)
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-title-one",
                            Constants.NUMBER, String.valueOf(task.getEntityCount()),
                            Constants.ENTITY, Utils.prettifyObject(this.user, task.getEntityType().iterator().next())));
                    }
                    else
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-title",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                        entities.append("\n");
                        task.getEntityType().stream().sorted().forEachOrdered(entity ->
                            entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-entity",
                                Constants.ENTITY, Utils.prettifyObject(this.user, entity))).append("\n"));
                    }
                }

                taskBuilder = entities.toString();
            }
            case FISHING -> {
                FishingTask task = taskObject.getTask();

                final String localReference = reference + "fishing.";

                StringBuilder items = new StringBuilder();

                if (task.isWhitelist())
                {
                    if (task.getItemSet().isEmpty())
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                    }
                    else if (task.getItemSet().size() == 1)
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "whitelist-title-one",
                            Constants.NUMBER, String.valueOf(task.getItemCount()),
                            Constants.ITEM, Utils.prettifyObject(this.user, task.getItemSet().iterator().next())));
                    }
                    else
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "whitelist-title",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                        items.append("\n");
                        task.getItemSet().stream().sorted().forEachOrdered(material ->
                            items.append(this.user.getTranslationOrNothing(localReference + "whitelist-item",
                                Constants.ITEM, Utils.prettifyObject(this.user, material))).append("\n"));
                    }
                }
                else
                {
                    if (task.getItemSet().isEmpty())
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                    }
                    else if (task.getItemSet().size() == 1)
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "blacklist-title-one",
                            Constants.NUMBER, String.valueOf(task.getItemCount()),
                            Constants.ITEM, Utils.prettifyObject(this.user, task.getItemSet().iterator().next())));
                    }
                    else
                    {
                        items.append(this.user.getTranslationOrNothing(localReference + "blacklist-title",
                            Constants.NUMBER, String.valueOf(task.getItemCount())));
                        items.append("\n");
                        task.getItemSet().stream().sorted().forEachOrdered(material ->
                            items.append(this.user.getTranslationOrNothing(localReference + "blacklist-item",
                                Constants.ITEM, Utils.prettifyObject(this.user, material))).append("\n"));
                    }
                }

                taskBuilder = items.toString();
            }
            case TACTICAL_FISHING -> {
                TacticalFishingTask task = taskObject.getTask();

                final String localReference = reference + "tactical-fishing.";

                StringBuilder entities = new StringBuilder();

                if (task.isWhitelist())
                {
                    if (task.getEntityType().isEmpty())
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                    }
                    else if (task.getEntityType().size() == 1)
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-title-one",
                            Constants.NUMBER, String.valueOf(task.getEntityCount()),
                            Constants.ENTITY, Utils.prettifyObject(this.user, task.getEntityType().iterator().next())));
                    }
                    else
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-title",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                        entities.append("\n");
                        task.getEntityType().stream().sorted().forEachOrdered(entity ->
                            entities.append(this.user.getTranslationOrNothing(localReference + "whitelist-entity",
                                Constants.ENTITY, Utils.prettifyObject(this.user, entity))).append("\n"));
                    }
                }
                else
                {
                    if (task.getEntityType().isEmpty())
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "title-any",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                    }
                    else if (task.getEntityType().size() == 1)
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-title-one",
                            Constants.NUMBER, String.valueOf(task.getEntityCount()),
                            Constants.ENTITY, Utils.prettifyObject(this.user, task.getEntityType().iterator().next())));
                    }
                    else
                    {
                        entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-title",
                            Constants.NUMBER, String.valueOf(task.getEntityCount())));
                        entities.append("\n");
                        task.getEntityType().stream().sorted().forEachOrdered(entity ->
                            entities.append(this.user.getTranslationOrNothing(localReference + "blacklist-entity",
                                Constants.ENTITY, Utils.prettifyObject(this.user, entity))).append("\n"));
                    }
                }

                taskBuilder = entities.toString();
            }
        }

        return taskBuilder;
    }


    /**
     * This method generates rewards in a single string with using user translations.
     *
     * @param taskObject Generator which treasures must be generated.
     * @return String that contains all rewards in user locale.
     */
    private String generateRewardsDescription(TaskObject taskObject)
    {
        final String reference = Constants.DESCRIPTIONS + "task.rewards.";

        StringBuilder rewardItems = new StringBuilder();
        StringBuilder rewardCommands = new StringBuilder();
        StringBuilder rewardExperience = new StringBuilder();
        StringBuilder rewardMoney = new StringBuilder();

        taskObject.getRewardList().stream().filter(reward -> !reward.isTeamPrize()).
            sorted(Comparator.comparing(Reward::getType)).
            forEachOrdered(reward -> {
                switch (reward.getType())
                {
                    case COMMAND -> rewardCommands.append(this.user.getTranslationOrNothing(reference + "command",
                        Constants.VALUE, ((CommandReward) reward).getCommand()));
                    case EXPERIENCE -> rewardExperience.append(this.user.getTranslationOrNothing(reference + "experience",
                        Constants.VALUE, String.valueOf(((ExperienceReward) reward).getExperience())));
                    case ITEM -> rewardItems.append(this.user.getTranslationOrNothing(reference + "item",
                        Constants.VALUE, Utils.prettifyObject(this.user, ((ItemReward) reward).getItemStack())));
                    case MONEY -> rewardMoney.append(this.user.getTranslationOrNothing(reference + "money",
                        Constants.VALUE, String.valueOf(((MoneyReward) reward).getMoney())));
                }
            });

        if (rewardItems.length() > 0)
        {
            rewardItems.insert(0, this.user.getTranslationOrNothing(reference + "item-title") + "\n");
        }

        if (rewardCommands.length() > 0)
        {
            rewardCommands.insert(0, this.user.getTranslationOrNothing(reference + "command-title") + "\n");
        }

        StringBuilder teamRewardItems = new StringBuilder();
        StringBuilder teamRewardCommands = new StringBuilder();
        StringBuilder teamRewardExperience = new StringBuilder();
        StringBuilder teamRewardMoney = new StringBuilder();

        taskObject.getRewardList().stream().filter(Reward::isTeamPrize).
            sorted(Comparator.comparing(Reward::getType)).
            forEachOrdered(reward -> {
                switch (reward.getType())
                {
                    case COMMAND -> teamRewardCommands.append(this.user.getTranslationOrNothing(reference + "command",
                        Constants.VALUE, ((CommandReward) reward).getCommand()));
                    case EXPERIENCE -> teamRewardExperience.append(this.user.getTranslationOrNothing(reference + "experience",
                        Constants.VALUE, String.valueOf(((ExperienceReward) reward).getExperience())));
                    case ITEM -> teamRewardItems.append(this.user.getTranslationOrNothing(reference + "item",
                        Constants.VALUE, Utils.prettifyObject(this.user, ((ItemReward) reward).getItemStack())));
                    case MONEY -> teamRewardMoney.append(this.user.getTranslationOrNothing(reference + "money",
                        Constants.VALUE, String.valueOf(((MoneyReward) reward).getMoney())));
                }
            });

        if (!teamRewardItems.isEmpty())
        {
            teamRewardItems.insert(0, this.user.getTranslationOrNothing(reference + "item-title") + "\n");
        }

        if (!teamRewardCommands.isEmpty())
        {
            teamRewardCommands.insert(0, this.user.getTranslationOrNothing(reference + "command-title") + "\n");
        }

        String reward = this.user.getTranslationOrNothing(reference + "lore",
            Constants.ITEMS, rewardItems.toString(),
            Constants.COMMAND, rewardCommands.toString(),
            Constants.EXPERIENCE, rewardExperience.toString(),
            Constants.MONEY, rewardMoney.toString());

        String teamReward = this.user.getTranslationOrNothing(reference + "lore",
            Constants.ITEMS, teamRewardItems.toString(),
            Constants.COMMAND, teamRewardCommands.toString(),
            Constants.EXPERIENCE, teamRewardExperience.toString(),
            Constants.MONEY, teamRewardMoney.toString());

        if (!reward.isBlank())
        {
            reward = this.user.getTranslationOrNothing(reference + "player-rewards") + "\n" + reward;
        }

        if (!teamReward.isBlank())
        {
            teamReward = this.user.getTranslationOrNothing(reference + "team-rewards") + "\n" + teamReward;
        }

        return this.user.getTranslationOrNothing(reference + "description",
            Constants.TEAM, teamReward,
            Constants.PLAYER, reward);
    }


    /**
     * This method generates list of requirements in a single string with using user translations.
     *
     * @param taskObject task which requirements must be generated.
     * @param isUnlocked Boolean that shows if level and permissions must be showed.
     * @return String that contains requirements for this taskObject.
     */
    private String generateRequirementsDescription(TaskObject taskObject,
        boolean isUnlocked,
        TaskDataObject islandData)
    {
        final String reference = Constants.DESCRIPTIONS + "task.requirements.";

        String level;
        double requiredLevel = taskObject.getRequiredLevel();

        if (this.addon.hasLevelHook() &&
            requiredLevel > 0 &&
            requiredLevel > this.manager.getIslandLevel(islandData.getUniqueId()))
        {
            level = this.user.getTranslationOrNothing(reference + "level",
                Constants.NUMBER, String.valueOf(requiredLevel));
        }
        else
        {
            level = "";
        }

        String money;
        double requiredMoney = taskObject.getRequiredMoney();

        if (this.addon.isEconomyProvided() &&
            requiredMoney > 0 &&
            requiredMoney > this.addon.getEconomyProvider().getBalance(this.user))
        {
            money = this.user.getTranslationOrNothing(reference + "money",
                Constants.NUMBER, String.valueOf(requiredMoney));
        }
        else
        {
            money = "";
        }

        StringBuilder permissions = new StringBuilder();
        List<String> requiredPermissions = taskObject.getRequiredPermissions();

        if (!requiredPermissions.isEmpty())
        {
            // Yes list duplication for complete menu.
            List<String> missingPermissions = requiredPermissions.stream().
                filter(permission -> !this.user.hasPermission(permission)).
                sorted().
                collect(Collectors.toList());

            if (!missingPermissions.isEmpty())
            {
                permissions.append(this.user.getTranslationOrNothing(reference + "permission-title"));
                missingPermissions.forEach(permission ->
                {
                    permissions.append("\n");
                    permissions.append(this.user.getTranslationOrNothing(reference + "permission",
                        Constants.PERMISSION, permission));
                });
            }
        }

        StringBuilder tasks = new StringBuilder();
        List<String> requiredTasks = taskObject.getRequiredTasks();

        if (!requiredTasks.isEmpty())
        {
            // Yes list duplication for complete menu.
            List<String> missingTasks = requiredTasks.stream().
                filter(islandData::isTaskCompleted).
                map(this.manager::getTaskById).
                filter(Objects::nonNull).
                map(TaskObject::getName).
                sorted().
                collect(Collectors.toList());

            if (!missingTasks.isEmpty())
            {
                tasks.append(this.user.getTranslationOrNothing(reference + "task-title"));
                missingTasks.forEach(task ->
                {
                    tasks.append("\n");
                    tasks.append(this.user.getTranslationOrNothing(reference + "task",
                        Constants.TASK, task));
                });
            }
        }

        return this.user.getTranslationOrNothing(reference + "description",
            Constants.LEVEL, level,
            Constants.MONEY, money,
            Constants.MISSING_TASKS, tasks.toString(),
            Constants.MISSING_PERMISSIONS, permissions.toString());
    }


    /**
     * This method generates task status string with using user translations.
     *
     * @param task Task which status must be generated.
     * @param isActive Indicate if task is activated by player.
     * @param isUnlocked Indicate if task is unlocked by player.
     * @return String that contains status for this task.
     */
    private String generateStatusDescription(TaskObject task,
        boolean isActive,
        boolean isUnlocked,
        TaskDataObject islandData)
    {
        final String reference = Constants.DESCRIPTIONS + "task.status.";

        String active = isActive ? this.user.getTranslationOrNothing(reference + "active") : "";
        String enabled = !task.isEnabled() ? this.user.getTranslationOrNothing(reference + "disabled") : "";

        long startDate = task.getStartDate();
        String startDateText;

        if (startDate > 0)
        {
            Calendar instance = Calendar.getInstance(this.addon.getSettings().getTimeZone(), this.user.getLocale());
            instance.setTime(new Date(startDate));

            startDateText = this.user.getTranslationOrNothing(reference + "start-date",
                Constants.DATE, DateFormat.getDateInstance(DateFormat.MEDIUM, user.getLocale()).format(instance.getTime()));
        }
        else
        {
            startDateText = "";
        }

        long endDate = task.getFinishDate();
        String endDateText;

        if (endDate > 0)
        {
            Calendar instance = Calendar.getInstance(this.addon.getSettings().getTimeZone(), this.user.getLocale());
            instance.setTime(new Date(endDate));

            endDateText = this.user.getTranslationOrNothing(reference + "end-date",
                Constants.DATE, DateFormat.getDateInstance(DateFormat.MEDIUM, user.getLocale()).format(instance.getTime()));
        }
        else
        {
            endDateText = "";
        }

        String progress;

        if (isActive)
        {
            double taskProgress = islandData.getTaskProgress(task.getUniqueId());
            double target = task.getTask().getTargetAmount();

            progress =  this.user.getTranslationOrNothing(reference + "progress",
                Constants.NUMBER, String.valueOf(taskProgress),
                Constants.VALUE, String.valueOf(target));
        }
        else
        {
            progress = "";
        }

        String completion;
        long completionCount = islandData.getNumberOfAllCompletions(task.getUniqueId());

        if (task.isRepeatable() && task.getRepeats() > completionCount)
        {
            if (task.getRepeats() > 0)
            {
                completion = this.user.getTranslationOrNothing(reference + "completed-repeatable-infinite",
                    Constants.NUMBER, String.valueOf(completionCount),
                    Constants.VALUE, String.valueOf(task.getRepeats()));
            }
            else
            {
                completion = this.user.getTranslationOrNothing(reference + "completed-repeatable",
                    Constants.NUMBER, String.valueOf(completionCount));
            }
        }
        else if (islandData.isTaskCompleted(task.getUniqueId()))
        {
            completion = this.user.getTranslationOrNothing(reference + "completed");
        }
        else
        {
            completion = "";
        }

        return this.user.getTranslationOrNothing(reference + "description",
            Constants.ACTIVE, active,
            Constants.DISABLED, enabled,
            Constants.START_DATE, startDateText,
            Constants.END_DATE, endDateText,
            Constants.COMPLETION, completion,
            Constants.PROGRESS, progress);
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
