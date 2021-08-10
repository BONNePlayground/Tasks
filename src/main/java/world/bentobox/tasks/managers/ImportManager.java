//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.managers;


import com.google.common.base.Enums;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Month;
import java.util.*;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.util.ItemParser;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.database.objects.options.*;
import world.bentobox.tasks.database.objects.requirements.LevelRequirement;
import world.bentobox.tasks.database.objects.requirements.MoneyRequirement;
import world.bentobox.tasks.database.objects.requirements.PermissionRequirement;
import world.bentobox.tasks.database.objects.requirements.TaskRequirement;
import world.bentobox.tasks.database.objects.rewards.CommandReward;
import world.bentobox.tasks.database.objects.rewards.ExperienceReward;
import world.bentobox.tasks.database.objects.rewards.ItemReward;
import world.bentobox.tasks.database.objects.rewards.MoneyReward;
import world.bentobox.tasks.listeners.tasks.*;
import world.bentobox.tasks.utils.Constants;
import world.bentobox.tasks.utils.Utils;


/**
 * This class manages data importing/exporting.
 */
public class ImportManager
{
    /**
     * Instantiates a new Import manager.
     *
     * @param addon the addon
     */
    public ImportManager(TasksAddon addon)
    {
        this.addon = addon;

        this.templateFile = new File(this.addon.getDataFolder(), "tasksTemplate.yml");

        if (!this.templateFile.exists())
        {
            this.addon.saveResource("tasksTemplate.yml", false);
        }
    }


    // ---------------------------------------------------------------------
    // Section: Template Methods
    // ---------------------------------------------------------------------


    /**
     * This method imports tasks from template
     *
     * @param user - user
     * @param world - world to import into
     * @return true if successful
     */
    public boolean importFile(@Nullable User user, World world)
    {
        if (!this.templateFile.exists())
        {
            if (user != null)
            {
                Utils.sendMessage(user,
                    user.getTranslation(Constants.ERRORS + "no-file",
                        Constants.FILE, this.templateFile.getName()));
            }

            return false;
        }

        return this.importFile(user, world, this.templateFile.getName());
    }


    /**
     * This method imports tasks from template
     *
     * @param user - user
     * @param world - world to import into
     * @param file - file that must be imported
     * @return true if successful
     */
    public boolean importFile(@Nullable User user, World world, String file)
    {
        File generatorFile = new File(this.addon.getDataFolder(), file.endsWith(".yml") ? file : file + ".yml");

        if (!generatorFile.exists())
        {
            if (user != null)
            {
                Utils.sendMessage(user,
                    user.getTranslation(Constants.ERRORS + "no-file",
                        Constants.FILE, file));
            }

            return false;
        }

        YamlConfiguration config = new YamlConfiguration();

        try
        {
            config.load(generatorFile);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            if (user != null)
            {
                Utils.sendMessage(user,
                    user.getTranslation(Constants.ERRORS + "no-load",
                        Constants.FILE, file,
                        TextVariables.DESCRIPTION, e.getMessage()));
            }
            else
            {
                this.addon.logError("Exception when loading file. " + e.getMessage());
            }

            return false;
        }

        Optional<GameModeAddon> optional = this.addon.getPlugin().getIWM().getAddon(world);

        if (!optional.isPresent())
        {
            if (user != null)
            {
                Utils.sendMessage(user,
                    user.getTranslation(Constants.ERRORS + "not-a-gamemode-world",
                        Constants.WORLD, world.getName()));
            }
            else
            {
                this.addon.logWarning("Given world is not a gamemode world.");
            }

            return false;
        }

        this.addon.getAddonManager().wipeGameModeTasks(optional.get());
        this.createTasks(config, user, optional.get());

        return true;
    }


    /**
     * This method creates task object from config file.
     *
     * @param config YamlConfiguration that contains all generators.
     * @param user User who calls reading.
     * @param gameMode GameMode in which tasks must be imported
     */
    private void createTasks(YamlConfiguration config, @Nullable User user, GameModeAddon gameMode)
    {
        final String prefix = gameMode.getDescription().getName().toLowerCase() + "_";

        int numberOfTasks = 0;
        int numberOfBundles = 0;

        ConfigurationSection reader = config.getConfigurationSection(GeneralConstant.TASKS.getValue());

        for (String taskId : reader.getKeys(false))
        {
            TaskObject taskObject = new TaskObject();
            taskObject.setUniqueId(prefix + taskId.toLowerCase());

            ConfigurationSection details = reader.getConfigurationSection(taskId);

            if (details != null)
            {
                taskObject.setEnabled(details.getBoolean(GeneralConstant.ENABLED.getValue(), true));
                // Populate task name.
                taskObject.setName(details.getString(GeneralConstant.NAME.getValue(), taskId));

                // Populate Task options.
                this.populateOptions(taskObject, details.getConfigurationSection(GeneralConstant.OPTIONS.getValue()));

                // Populate Task data.
                this.populateTask(taskObject, details.getConfigurationSection(GeneralConstant.DATA.getValue()));

                // Populate Task requirements
                this.populateRequirements(taskObject, details.getConfigurationSection(GeneralConstant.REQUIREMENTS.getValue()));

                // Populate Task rewards
                this.populateRewards(taskObject, details.getConfigurationSection(GeneralConstant.REWARDS.getValue()));
            }

            // Save object in database.
            this.addon.getAddonManager().saveTask(taskObject);
            this.addon.getAddonManager().loadTask(taskObject, false, null);
            numberOfTasks++;
        }

        if (user != null)
        {
            Utils.sendMessage(user,
                user.getTranslation(Constants.MESSAGES + "import-count",
                    Constants.TASK, String.valueOf(numberOfTasks),
                    Constants.BUNDLE, String.valueOf(numberOfBundles)));
        }

        this.addon.log("Imported " + numberOfTasks + " tasks and " + numberOfBundles + " bundles");
    }


    /**
     * This method populates task object with options from given section.
     * @param taskObject Object that must be populated.
     * @param section Section that contains options.
     */
    private void populateOptions(TaskObject taskObject, ConfigurationSection section)
    {
        if (section != null)
        {
            // Check description
            if (section.contains(OptionConstant.DESCRIPTION.getValue()))
            {
                if (section.isList(OptionConstant.DESCRIPTION.getValue()))
                {
                    DescriptionOption option = new DescriptionOption();
                    option.setDescription(section.getStringList(OptionConstant.DESCRIPTION.getValue()));
                    // Add to taskObject
                    taskObject.addOption(option);
                }
                else if (section.isString(OptionConstant.DESCRIPTION.getValue()))
                {
                    String description = section.getString(OptionConstant.DESCRIPTION.getValue());

                    if (description != null)
                    {
                        DescriptionOption option = new DescriptionOption();
                        // Define as list.
                        option.setDescription(Arrays.asList(
                            description.replaceAll("\\|", "\n").
                                split("\n").
                                clone()));
                        // Add to taskObject
                        taskObject.addOption(option);
                    }
                }
            }

            // Check start-message
            if (section.contains(OptionConstant.START_MESSAGE.getValue()))
            {
                ConfigurationSection startConfiguration =
                    section.getConfigurationSection(OptionConstant.START_MESSAGE.getValue());

                String value = startConfiguration.getString(DataConstant.TYPE.getValue(),
                    StartMessageOption.Type.TEAM.name());

                StartMessageOption option = new StartMessageOption();
                option.setMessageType(Enums.getIfPresent(StartMessageOption.Type.class, value).
                    or(StartMessageOption.Type.TEAM));

                if (startConfiguration.isList(DataConstant.MESSAGE.getValue()))
                {
                    StringBuilder builder = new StringBuilder();
                    startConfiguration.getStringList(DataConstant.MESSAGE.getValue()).forEach(line -> builder.append(line).append("\n"));
                    option.setStartMessage(builder.toString());
                }
                else if (startConfiguration.isString(DataConstant.MESSAGE.getValue()))
                {
                    option.setStartMessage(startConfiguration.getString(DataConstant.MESSAGE.getValue()));
                }

                taskObject.addOption(option);
            }

            // Check finish-message
            if (section.contains(OptionConstant.FINISH_MESSAGE.getValue()))
            {
                ConfigurationSection finishConfiguration =
                    section.getConfigurationSection(OptionConstant.FINISH_MESSAGE.getValue());

                String value = finishConfiguration.getString(DataConstant.TYPE.getValue(),
                    FinishMessageOption.Type.TEAM.name());

                FinishMessageOption option = new FinishMessageOption();
                option.setMessageType(Enums.getIfPresent(FinishMessageOption.Type.class, value).
                    or(FinishMessageOption.Type.TEAM));

                if (finishConfiguration.isList(DataConstant.MESSAGE.getValue()))
                {
                    StringBuilder builder = new StringBuilder();
                    finishConfiguration.getStringList(DataConstant.MESSAGE.getValue()).forEach(line -> builder.append(line).append("\n"));
                    option.setFinishMessage(builder.toString());
                }
                else if (finishConfiguration.isString(DataConstant.MESSAGE.getValue()))
                {
                    option.setFinishMessage(finishConfiguration.getString(DataConstant.MESSAGE.getValue()));
                }

                taskObject.addOption(option);
            }

            // Check icon
            if (section.contains(OptionConstant.ICON.getValue()))
            {
                ItemStack parsedIcon = ItemParser.parse(section.getString(OptionConstant.ICON.getValue()));

                if (parsedIcon != null)
                {
                    IconOption option = new IconOption();
                    option.setIcon(parsedIcon);
                    taskObject.addOption(option);
                }
            }

            // Check start date
            if (section.contains(OptionConstant.START_DATE.getValue()))
            {
                DateFormat dateFormat = new SimpleDateFormat(this.addon.getSettings().getDateFormat());
                dateFormat.setTimeZone(this.addon.getSettings().getTimeZone());

                try
                {
                    StartDateOption option = new StartDateOption();
                    option.setStartDate(dateFormat.parse(section.getString(OptionConstant.START_DATE.getValue())));
                    taskObject.addOption(option);
                }
                catch (ParseException e)
                {
                    this.addon.logWarning("Cannot parse Starting Date.");
                }
            }

            // Check end date
            if (section.contains(OptionConstant.END_DATE.getValue()))
            {
                DateFormat dateFormat = new SimpleDateFormat(this.addon.getSettings().getDateFormat());
                dateFormat.setTimeZone(this.addon.getSettings().getTimeZone());

                try
                {
                    EndDateOption option = new EndDateOption();
                    option.setEndDate(dateFormat.parse(section.getString(OptionConstant.END_DATE.getValue())));
                    taskObject.addOption(option);
                }
                catch (ParseException e)
                {
                    this.addon.logWarning("Cannot parse End Date.");
                }
            }

            // Check repeatable option
            if (section.contains(OptionConstant.REPEATABLE.getValue()))
            {
                RepeatableOption option = new RepeatableOption();
                int value = section.getInt(OptionConstant.REPEATABLE.getValue(), 1);
                option.setRepeatable(value != 1);
                option.setNumberOfRepeats(value);
                taskObject.addOption(option);
            }

            // Check regular task reset.
            if (section.contains(OptionConstant.REGULARITY.getValue()))
            {
                ConfigurationSection regularity =
                    section.getConfigurationSection(OptionConstant.REGULARITY.getValue());

                String type = regularity.getString(DataConstant.TYPE.getValue(), RegularityConstants.NONE.name());
                RegularityConstants typeConstants = Enums.getIfPresent(RegularityConstants.class, type).
                    or(RegularityConstants.NONE);

                int hour = Math.max(0, Math.min(23, regularity.getInt(DataConstant.HOUR.getValue(), 0)));
                int day = Math.max(1, Math.min(31, regularity.getInt(DataConstant.DAY.getValue(), 1)));
                int month = Math.max(1, Math.min(12, regularity.getInt(DataConstant.MONTH.getValue(), 1)));

                switch (typeConstants)
                {
                    case DAILY -> {
                        DailyResetOption option = new DailyResetOption();
                        option.setHour(hour);
                        taskObject.addOption(option);
                    }
                    case WEEKLY -> {
                        WeeklyResetOption option = new WeeklyResetOption();
                        option.setHour(hour);
                        option.setDay(DayOfWeek.of(Math.min(7, day)));
                        taskObject.addOption(option);
                    }
                    case MONTHLY -> {
                        MonthlyResetOption option = new MonthlyResetOption();
                        option.setHour(hour);
                        option.setDay(day);
                        taskObject.addOption(option);
                    }
                    case YEARLY -> {
                        YearlyResetOption option = new YearlyResetOption();
                        option.setHour(hour);
                        option.setDay(day);
                        option.setMonth(Month.of(month));
                        taskObject.addOption(option);
                    }
                }
            }

            // Check task cool down
            if (section.contains(OptionConstant.COOL_DOWN.getValue()))
            {
                CoolDownOption option = new CoolDownOption();
                int value = section.getInt(OptionConstant.COOL_DOWN.getValue(), 10);
                option.setCoolDown(value);
                taskObject.addOption(option);
            }
        }
    }


    /**
     * This method populates task object with tasks from given section.
     * @param taskObject Object that must be populated.
     * @param section Section that contains options.
     */
    private void populateTask(TaskObject taskObject, ConfigurationSection section)
    {
        if (section != null)
        {
            switch (TypeConstant.valueOf(section.getString(DataConstant.TYPE.getValue())))
            {
                case BLOCK_BREAK -> {
                    BlockTask task = new BlockTask();
                    task.setPlace(false);
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setBlockCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setMaterialSet(this.readMaterials(taskObject,
                        section.getStringList(DataConstant.BLOCKS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case BLOCK_PLACE -> {
                    BlockTask task = new BlockTask();
                    task.setPlace(true);
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setBlockCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setMaterialSet(this.readMaterials(taskObject,
                        section.getStringList(DataConstant.BLOCKS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case CONSUME -> {
                    ConsumeTask task = new ConsumeTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setItemCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setMaterialSet(this.readMaterials(taskObject,
                        section.getStringList(DataConstant.MATERIALS.getValue())));
                    task.setPotionEffectTypes(this.readPotions(taskObject,
                        section.getStringList(DataConstant.POTIONS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case DAMAGE_DEAL -> {
                    DamageDealTask task = new DamageDealTask();
                    task.setDamageAmount(section.getDouble(DataConstant.NUMBER.getValue(), 0));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case DAMAGE_RECEIVE -> {
                    DamageReceiveTask task = new DamageReceiveTask();
                    task.setDamageAmount(section.getDouble(DataConstant.NUMBER.getValue(), 0));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case ENTITY_BREED -> {
                    EntityBreedTask task = new EntityBreedTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setEntityCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setEntityType(this.readEntities(taskObject,
                        section.getStringList(DataConstant.ENTITIES.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case ENTITY_KILL -> {
                    EntityKillTask task = new EntityKillTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setEntityCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setEntityType(this.readEntities(taskObject,
                        section.getStringList(DataConstant.ENTITIES.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case FISHING -> {
                    FishingTask task = new FishingTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setItemCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setItemSet(this.readMaterials(taskObject,
                        section.getStringList(DataConstant.MATERIALS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case TACTICAL_FISHING -> {
                    TacticalFishingTask task = new TacticalFishingTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setEntityCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setEntityType(this.readEntities(taskObject,
                        section.getStringList(DataConstant.ENTITIES.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
            }
        }
    }


    /**
     * This method creates a set of materials from given configuration section.
     * @param object Object that will be reported as failed if cannot parse material.
     * @param section Section that contains materials.
     * @return Set of materials from config section.
     */
    private Set<Material> readMaterials(TaskObject object, List<String> section)
    {
        Set<Material> materialSet = new HashSet<>();

        if (section != null)
        {
            for (String materialKey : section)
            {
                try
                {
                    materialSet.add(Material.valueOf(materialKey.toUpperCase()));
                }
                catch (Exception e)
                {
                    this.addon.logWarning("Unknown material (" + materialKey +
                        ") in template blocks section for task " +
                        object.getUniqueId() + ". Skipping...");
                }
            }
        }

        return materialSet;
    }


    /**
     * This method creates a set of entities from given configuration section.
     * @param object Object that will be reported as failed if cannot parse entities.
     * @param section Section that contains entities.
     * @return Set of entities from config section.
     */
    private Set<EntityType> readEntities(TaskObject object, List<String> section)
    {
        Set<EntityType> entitySet = new HashSet<>();

        if (section != null)
        {
            for (String entityKey : section)
            {
                try
                {
                    entitySet.add(EntityType.valueOf(entityKey.toUpperCase()));
                }
                catch (Exception e)
                {
                    this.addon.logWarning("Unknown entityType (" + entityKey +
                        ") in template entity section for task " +
                        object.getUniqueId() + ". Skipping...");
                }
            }
        }

        return entitySet;
    }


    /**
     * This method creates a set of potions from given configuration section.
     * @param object Object that will be reported as failed if cannot parse potions.
     * @param section Section that contains potions.
     * @return Set of potions from config section.
     */
    private Set<PotionEffectType> readPotions(TaskObject object, List<String> section)
    {
        Set<PotionEffectType> potionSet = new HashSet<>();

        if (section != null)
        {
            for (String potionKey : section)
            {
                try
                {
                    potionSet.add(PotionEffectType.getByName(potionKey.toLowerCase()));
                }
                catch (Exception e)
                {
                    this.addon.logWarning("Unknown potionType (" + potionKey +
                        ") in template potions section for task " +
                        object.getUniqueId() + ". Skipping...");
                }
            }
        }

        return potionSet;
    }


    /**
     * This method populates task object with requirements from given section.
     * @param taskObject Object that must be populated.
     * @param section Section that contains options.
     */
    private void populateRequirements(TaskObject taskObject, ConfigurationSection section)
    {
        if (section != null)
        {
            // Populate permissions
            if (section.contains(RequirementConstant.PERMISSIONS.getValue()))
            {
                if (section.isList(RequirementConstant.PERMISSIONS.getValue()))
                {
                    section.getStringList(RequirementConstant.PERMISSIONS.getValue()).
                        forEach(permission -> {
                            PermissionRequirement requirement = new PermissionRequirement();
                            requirement.setPermission(permission);
                            // Add to taskObject
                            taskObject.addRequirement(requirement);
                        });


                }
                else if (section.isString(RequirementConstant.PERMISSIONS.getValue()))
                {
                    String permission = section.getString(RequirementConstant.PERMISSIONS.getValue());

                    if (permission != null)
                    {
                        PermissionRequirement requirement = new PermissionRequirement();
                        requirement.setPermission(permission);
                        // Add to taskObject
                        taskObject.addRequirement(requirement);
                    }
                }
            }

            // Populate money
            if (section.contains(RequirementConstant.MONEY.getValue()))
            {
                MoneyRequirement requirement = new MoneyRequirement();
                requirement.setMoney(section.getDouble(RequirementConstant.MONEY.getValue(), 0.0));
                taskObject.addRequirement(requirement);
            }

            // Populate level
            if (section.contains(RequirementConstant.LEVEL.getValue()))
            {
                LevelRequirement requirement = new LevelRequirement();
                requirement.setLevel(section.getDouble(RequirementConstant.LEVEL.getValue(), 0.0));
                taskObject.addRequirement(requirement);
            }

            // Populate tasks
            if (section.contains(RequirementConstant.TASKS.getValue()))
            {
                // TODO: it is possible that taskId requires adding [gamemode]_
                if (section.isList(RequirementConstant.TASKS.getValue()))
                {
                    section.getStringList(RequirementConstant.TASKS.getValue()).
                        forEach(taskId -> {
                            TaskRequirement requirement = new TaskRequirement();
                            requirement.setTaskId(taskId);
                            // Add to taskObject
                            taskObject.addRequirement(requirement);
                        });


                }
                else if (section.isString(RequirementConstant.TASKS.getValue()))
                {
                    String taskId = section.getString(RequirementConstant.TASKS.getValue());

                    if (taskId != null)
                    {
                        TaskRequirement requirement = new TaskRequirement();
                        requirement.setTaskId(taskId);
                        // Add to taskObject
                        taskObject.addRequirement(requirement);
                    }
                }
            }
        }
    }


    /**
     * This method populates task object with rewards from given section.
     * @param taskObject Object that must be populated.
     * @param section Section that contains options.
     */
    private void populateRewards(TaskObject taskObject, ConfigurationSection section)
    {
        if (section != null)
        {
            ConfigurationSection teamSection = section.getConfigurationSection(RewardConstant.TEAM.getValue());

            if (teamSection != null)
            {
                this.populateRewards(taskObject, teamSection, true);
            }

            ConfigurationSection playerSection = section.getConfigurationSection(RewardConstant.PLAYER.getValue());

            if (playerSection != null)
            {
                this.populateRewards(taskObject, playerSection, false);
            }
        }
    }


    /**
     * This method populates task object with rewards from given section.
     * @param taskObject Object that must be populated.
     * @param section Section that contains options.
     * @param teamReward Indicates if this is team reward.
     */
    private void populateRewards(TaskObject taskObject, ConfigurationSection section, boolean teamReward)
    {
        // Experience reward population
        if (section.contains(RewardConstant.EXPERIENCE.getValue()))
        {
            long experience = section.getLong(RewardConstant.EXPERIENCE.getValue(), 0);

            if (experience > 0)
            {
                ExperienceReward reward = new ExperienceReward();
                reward.setExperience(experience);
                reward.setTeamPrize(teamReward);
                taskObject.addReward(reward);
            }
        }

        // Experience reward population
        if (section.contains(RewardConstant.MONEY.getValue()))
        {
            double money = section.getDouble(RewardConstant.MONEY.getValue(), 0.0);

            if (money > 0)
            {
                MoneyReward reward = new MoneyReward();
                reward.setMoney(money);
                reward.setTeamPrize(teamReward);
                taskObject.addReward(reward);
            }
        }

        // Item reward population
        if (section.contains(RewardConstant.ITEMS.getValue()))
        {
            if (section.isList(RewardConstant.ITEMS.getValue()))
            {
                section.getStringList(RewardConstant.ITEMS.getValue()).stream().
                    map(ItemParser::parse).
                    filter(Objects::nonNull).
                    forEach(item -> {
                        ItemReward reward = new ItemReward();
                        reward.setItemStack(item);
                        reward.setTeamPrize(teamReward);
                        taskObject.addReward(reward);
                    });
            }
            else if (section.isString(RewardConstant.ITEMS.getValue()))
            {
                ItemStack item = ItemParser.parse(section.getString(RewardConstant.ITEMS.getValue()));

                if (item != null)
                {
                    ItemReward reward = new ItemReward();
                    reward.setItemStack(item);
                    reward.setTeamPrize(teamReward);
                    taskObject.addReward(reward);
                }
            }
        }

        // Command reward population
        if (section.contains(RewardConstant.COMMAND.getValue()))
        {
            if (section.isList(RewardConstant.COMMAND.getValue()))
            {
                section.getStringList(RewardConstant.COMMAND.getValue()).
                    forEach(command -> {
                        CommandReward reward = new CommandReward();
                        reward.setCommand(command);
                        reward.setTeamPrize(teamReward);
                        taskObject.addReward(reward);
                    });


            }
            else if (section.isString(RewardConstant.COMMAND.getValue()))
            {
                String command = section.getString(RewardConstant.COMMAND.getValue());

                if (command != null)
                {
                    CommandReward reward = new CommandReward();
                    reward.setCommand(command);
                    reward.setTeamPrize(teamReward);
                    taskObject.addReward(reward);
                }
            }
        }
    }


    // ---------------------------------------------------------------------
    // Section: Enums
    // ---------------------------------------------------------------------


    /**
     * Enum that holds general constants which can be set to each task.
     */
    public enum GeneralConstant
    {
        /**
         * Option for setting tasks.
         */
        TASKS("tasks"),
        /**
         * Option for setting task enabled.
         */
        ENABLED("enabled"),
        /**
         * Option for setting task options.
         */
        OPTIONS("options"),
        /**
         * Option for allowing task data.
         */
        DATA("data"),
        /**
         * Option for setting task requirements.
         */
        REQUIREMENTS("requirements"),
        /**
         * Option for setting task rewards.
         */
        REWARDS("rewards"),
        /**
         * Option for setting task name.
         */
        NAME("name");


        /**
         * @param value String constant.
         */
        GeneralConstant(String value)
        {
            this.constant = value;
        }


        /**
         * Gets constant.
         *
         * @return the constant
         */
        public String getValue()
        {
            return this.constant;
        }


        /**
         * Constant value for the option.
         */
        private final String constant;
    }


    /**
     * Enum that holds options which can be set to each task.
     */
    public enum OptionConstant
    {
        /**
         * Option for setting task description.
         */
        DESCRIPTION("description"),
        /**
         * Option for setting start message.
         */
        START_MESSAGE("start-message"),
        /**
         * Option for setting finish message.
         */
        FINISH_MESSAGE("finish-message"),
        /**
         * Option for setting task icon.
         */
        ICON("icon"),
        /**
         * Option for allowing task repeating.
         */
        REPEATABLE("repeatable"),
        /**
         * Option for setting task timeout.
         */
        COOL_DOWN("cool-down"),
        /**
         * Option for setting task start date.
         */
        START_DATE("start-date"),
        /**
         * Option for setting task end date.
         */
        END_DATE("end-date"),
        /**
         * Option for setting task regularity.
         */
        REGULARITY("regularity");


        /**
         * @param value String constant.
         */
        OptionConstant(String value)
        {
            this.constant = value;
        }


        /**
         * Gets constant.
         *
         * @return the constant
         */
        public String getValue()
        {
            return this.constant;
        }


        /**
         * Constant value for the option.
         */
        private final String constant;
    }


    /**
     * Constants for regularity option.
     */
    public enum RegularityConstants
    {
        NONE,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }


    /**
     * Enum that holds data type which can be set to each task type.
     */
    public enum TypeConstant
    {
        BLOCK_BREAK,
        BLOCK_PLACE,
        CONSUME,
        DAMAGE_DEAL,
        DAMAGE_RECEIVE,
        ENTITY_BREED,
        ENTITY_KILL,
        FISHING,
        TACTICAL_FISHING
    }


    /**
     * Enum that holds data constants which can be set to each data.
     */
    public enum DataConstant
    {
        TYPE("type"),
        BLOCKS("blocks"),
        ENTITIES("entities"),
        MATERIALS("materials"),
        POTIONS("potions"),
        WHITELIST("whitelist"),
        HOUR("hour"),
        DAY("day"),
        MONTH("month"),
        MESSAGE("message"),
        NUMBER("number");


        /**
         * @param value String constant.
         */
        DataConstant(String value)
        {
            this.constant = value;
        }


        /**
         * Gets constant.
         *
         * @return the constant
         */
        public String getValue()
        {
            return this.constant;
        }


        /**
         * Constant value for the option.
         */
        private final String constant;
    }


    /**
     * Enum that holds data constants which can be set to each requirement.
     */
    public enum RequirementConstant
    {
        PERMISSIONS("permissions"),
        TASKS("completed-tasks"),
        LEVEL("island-level"),
        MONEY("money");

        /**
         * @param value String constant.
         */
        RequirementConstant(String value)
        {
            this.constant = value;
        }


        /**
         * Gets constant.
         *
         * @return the constant
         */
        public String getValue()
        {
            return this.constant;
        }


        /**
         * Constant value for the option.
         */
        private final String constant;
    }


    /**
     * Enum that holds data constants which can be set to each reward.
     */
    public enum RewardConstant
    {
        EXPERIENCE("experience"),
        MONEY("money"),
        ITEMS("items"),
        COMMAND("commands"),
        TEAM("team"),
        PLAYER("player");

        /**
         * @param value String constant.
         */
        RewardConstant(String value)
        {
            this.constant = value;
        }


        /**
         * Gets constant.
         *
         * @return the constant
         */
        public String getValue()
        {
            return this.constant;
        }


        /**
         * Constant value for the option.
         */
        private final String constant;
    }


    // ---------------------------------------------------------------------
    // Section: Variables
    // ---------------------------------------------------------------------

    /**
     * Addon class instance.
     */
    private final TasksAddon addon;

    /**
     * Variable stores template file location
     */
    private final File templateFile;
}
