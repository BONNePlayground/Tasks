//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.managers;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskObject;
import world.bentobox.tasks.database.objects.options.DescriptionOption;
import world.bentobox.tasks.database.objects.requirements.PermissionRequirement;
import world.bentobox.tasks.database.objects.rewards.ExperienceReward;
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

        // TODO: wipe or replace exisitng?
        // this.addon.getAddonManager().wipeTasks(optional);
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

        ConfigurationSection reader = config.getConfigurationSection(GeneralConstant.TASKS.getValue());

        for (String taskId : reader.getKeys(false))
        {
            TaskObject taskObject = new TaskObject();
            taskObject.setUniqueId(prefix + taskId.toLowerCase());

            ConfigurationSection details = reader.getConfigurationSection(taskId);

            if (details != null)
            {
                taskObject.setEnabled(details.getBoolean(GeneralConstant.ENABLED.getValue(), false));

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
                    Constants.TASK, String.valueOf(numberOfTasks)));
        }

        this.addon.log("Imported " + numberOfTasks + " tasks");
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

            if (section.contains(OptionConstant.ICON.getValue()))
            {

            }

            if (section.contains(OptionConstant.START_DATE.getValue()))
            {

            }

            if (section.contains(OptionConstant.END_DATE.getValue()))
            {

            }

            if (section.contains(OptionConstant.REPEATABLE.getValue()))
            {

            }

            if (section.contains(OptionConstant.REGULARITY.getValue()))
            {

            }

            if (section.contains(OptionConstant.TIMEOUT.getValue()))
            {

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
                        section.getConfigurationSection(DataConstant.BLOCKS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case BLOCK_PLACE -> {
                    BlockTask task = new BlockTask();
                    task.setPlace(true);
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setBlockCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setMaterialSet(this.readMaterials(taskObject,
                        section.getConfigurationSection(DataConstant.BLOCKS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case CONSUME -> {
                    ConsumeTask task = new ConsumeTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setItemCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setMaterialSet(this.readMaterials(taskObject,
                        section.getConfigurationSection(DataConstant.MATERIALS.getValue())));
                    task.setPotionEffectTypes(this.readPotions(taskObject,
                        section.getConfigurationSection(DataConstant.POTIONS.getValue())));

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
                        section.getConfigurationSection(DataConstant.ENTITIES.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case ENTITY_KILL -> {
                    EntityKillTask task = new EntityKillTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setEntityCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setEntityType(this.readEntities(taskObject,
                        section.getConfigurationSection(DataConstant.ENTITIES.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case FISHING -> {
                    FishingTask task = new FishingTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setItemCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setItemSet(this.readMaterials(taskObject,
                        section.getConfigurationSection(DataConstant.MATERIALS.getValue())));

                    task.setTaskId(taskObject.getUniqueId());
                    taskObject.setTask(task);
                }
                case TACTICAL_FISHING -> {
                    TacticalFishingTask task = new TacticalFishingTask();
                    task.setWhitelist(section.getBoolean(DataConstant.WHITELIST.getValue(), true));
                    task.setEntityCount(section.getInt(DataConstant.NUMBER.getValue(), 0));
                    task.setEntityTypes(this.readEntities(taskObject,
                        section.getConfigurationSection(DataConstant.ENTITIES.getValue())));

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
    private Set<Material> readMaterials(TaskObject object, ConfigurationSection section)
    {
        Set<Material> materialSet = new HashSet<>();

        if (section != null)
        {
            for (String materialKey : section.getKeys(false))
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
    private Set<EntityType> readEntities(TaskObject object, ConfigurationSection section)
    {
        Set<EntityType> entitySet = new HashSet<>();

        if (section != null)
        {
            for (String entityKey : section.getKeys(false))
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
    private Set<PotionEffectType> readPotions(TaskObject object, ConfigurationSection section)
    {
        Set<PotionEffectType> potionSet = new HashSet<>();

        if (section != null)
        {
            for (String potionKey : section.getKeys(false))
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
            if (section.contains(RewardConstant.EXPERIENCE.getValue()))
            {
                long experience = section.getLong(RewardConstant.EXPERIENCE.getValue(), 0);

                if (experience > 0)
                {
                    ExperienceReward reward = new ExperienceReward();
                    reward.setExperience(experience);
                    // Add to taskObject
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
        REWARDS("rewards");


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
        TIMEOUT("timeout"),
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
        COMMAND("commands");

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
