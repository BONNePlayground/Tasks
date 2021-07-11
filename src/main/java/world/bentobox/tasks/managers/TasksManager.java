//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.managers;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.TaskBundleObject;
import world.bentobox.tasks.database.objects.TaskDataObject;
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
import world.bentobox.tasks.listeners.tasks.Task;
import world.bentobox.tasks.utils.Constants;
import world.bentobox.tasks.utils.Utils;


/**
 * Main class that manages Tasks Addon data.
 */
public class TasksManager
{
    /**
     * Instantiates a new Tasks manager.
     *
     * @param addon the tasks addon instance.
     */
    public TasksManager(TasksAddon addon)
    {
        this.addon = addon;

        this.taskDatabase = new Database<>(addon, TaskObject.class);
        this.taskCache = new HashMap<>();

        this.taskDataDatabase = new Database<>(addon, TaskDataObject.class);
        this.taskDataCache = new HashMap<>();

        this.taskBundleDatabase = new Database<>(addon, TaskBundleObject.class);
        this.taskBundleCache = new HashMap<>();
    }


    /**
     * This method adds given world as world where addon operates.
     * @param world World where addon operates.
     */
    public void addWorld(World world)
    {
        this.operatingWorlds.add(world);
    }


// ---------------------------------------------------------------------
// Section: Database related methods
// ---------------------------------------------------------------------


    /**
     * Creates generators cache.
     */
    public void load()
    {
        this.taskCache.clear();
        this.taskBundleCache.clear();

        this.addon.log("Loading tasks from database...");

        this.taskDatabase.loadObjects().forEach(this::loadTask);
        this.taskBundleDatabase.loadObjects().forEach(this::loadBundle);

        this.addon.log("Done");
    }


    /**
     * Loads task in cache silently. Used when loading.
     *
     * @param taskObject that must be stored.
     * @return true if successful
     */
    private boolean loadTask(TaskObject taskObject)
    {
        return this.loadTask(taskObject, true, null);
    }


    /**
     * Load taskObject in the cache.
     *
     * @param taskObject - taskObject that must be stored.
     * @param overwrite - true if previous taskObject should be overwritten
     * @param user - user making the request
     * @return - true if imported
     */
    public boolean loadTask(TaskObject taskObject, boolean overwrite, User user)
    {
        if (this.taskCache.containsKey(taskObject.getUniqueId()))
        {
            if (!overwrite)
            {
                return false;
            }
            else
            {
                // Stop old task
                this.taskCache.get(taskObject.getUniqueId()).getTask().onStop();
                // Replace
                this.taskCache.replace(taskObject.getUniqueId(), taskObject);
                // Start new task.
                taskObject.getTask().onStart();

                return true;
            }
        }

        if (user != null)
        {
            Utils.sendMessage(user,
                user.getTranslation(Constants.MESSAGES + "task-loaded",
                    Constants.TASK, taskObject.getUniqueId()));
        }

        this.taskCache.put(taskObject.getUniqueId(), taskObject);

        // Start new task.
        taskObject.getTask().onStart();

        return true;
    }


    /**
     * Loads task bundle in cache silently. Used when loading.
     *
     * @param taskBundleObject that must be stored.
     * @return true if successful
     */
    private boolean loadBundle(TaskBundleObject taskBundleObject)
    {
        return this.loadBundle(taskBundleObject, true, null);
    }


    /**
     * Load task bundle object in the cache.
     *
     * @param taskBundleObject - taskBundleObject that must be stored.
     * @param overwrite - true if previous taskBundleObject should be overwritten
     * @param user - user making the request
     * @return - true if imported
     */
    public boolean loadBundle(TaskBundleObject taskBundleObject, boolean overwrite, User user)
    {
        if (this.taskBundleCache.containsKey(taskBundleObject.getUniqueId()))
        {
            if (!overwrite)
            {
                return false;
            }
            else
            {
                this.taskBundleCache.replace(taskBundleObject.getUniqueId(), taskBundleObject);
                return true;
            }
        }

        if (user != null)
        {
            Utils.sendMessage(user,
                user.getTranslation(Constants.MESSAGES + "bundle-loaded",
                    Constants.BUNDLE, taskBundleObject.getUniqueId()));
        }

        this.taskBundleCache.put(taskBundleObject.getUniqueId(), taskBundleObject);
        return true;
    }


    /**
     * Save generator tiers from cache into database
     * @param withTaskStop indicate that all tasks must be stopped at the end.
     */
    public void save(boolean withTaskStop)
    {
        this.taskCache.values().forEach(this::saveTask);
        this.taskBundleCache.values().forEach(this::saveTaskBundle);
        this.taskDataCache.values().forEach(this::saveTaskData);

        if (withTaskStop)
        {
            // Now stop all active tasks.
            this.taskCache.values().stream().map(TaskObject::getTask).forEach(Task::onStop);
        }
    }


    /**
     * @param taskObject object that must be saved.
     */
    void saveTask(TaskObject taskObject)
    {
        this.taskDatabase.saveObjectAsync(taskObject);
    }


    /**
     * @param taskBundleObject object that must be saved.
     */
    private void saveTaskBundle(TaskBundleObject taskBundleObject)
    {
        this.taskBundleDatabase.saveObjectAsync(taskBundleObject);
    }


    /**
     * @param taskDataObject object that must be saved.
     */
    private void saveTaskData(TaskDataObject taskDataObject)
    {
        this.taskDataDatabase.saveObjectAsync(taskDataObject);
    }


// ---------------------------------------------------------------------
// Section: User related data
// ---------------------------------------------------------------------


    /**
     * This method returns data about player island.
     * @param player Player who island data must be returned.
     * @param world World in which island should be searched.
     * @return TaskDataObject for players island or null.
     */
    @Nullable
    public TaskDataObject getIslandData(Player player, World world)
    {
        if (!this.operatingWorlds.contains(world))
        {
            // Not a world where addon operates.
            return null;
        }

        Island island = this.addon.getIslands().getIsland(world, player.getUniqueId());

        if (island == null)
        {
            // User does not have an island.
            return null;
        }

        this.addIslandData(island);

        // Return island data.
        return this.taskDataCache.get(island.getUniqueId());
    }


    /**
     * This method checks every island in stored worlds for user and loads them in cache.
     *
     * @param uniqueId User unique id.
     */
    public void loadUserIslands(UUID uniqueId)
    {
        BentoBox.getInstance().getIWM().getOverWorlds().stream().
            filter(this.operatingWorlds::contains).
            map(world -> this.addon.getIslands().getIsland(world, uniqueId)).
            filter(Objects::nonNull).
            forEach(island ->
            {
                if (island.getOwner() == uniqueId)
                {
                    // Owner island must be validated.
                    this.validateIslandData(island);
                }
                else
                {
                    // Members does not influence island data.
                    this.addIslandData(island);
                }
            });
    }


    /**
     * Load island from database into the cache or create new island data
     *
     * @param island - island that must be loaded
     */
    private void addIslandData(@NotNull Island island)
    {
        final String uniqueID = island.getUniqueId();

        if (this.taskDataCache.containsKey(uniqueID))
        {
            return;
        }

        // The island is not in the cache
        // Check if the island exists in the database

        if (this.taskDataDatabase.objectExists(uniqueID))
        {
            // Load player from database
            TaskDataObject data = this.taskDataDatabase.loadObject(uniqueID);
            // Store in cache

            if (data != null)
            {
                this.taskDataCache.put(uniqueID, data);
            }
            else
            {
                this.addon.logError("Could not load NULL task data object.");
            }
        }
        else
        {
            // Create the island data
            TaskDataObject pd = new TaskDataObject();
            pd.setUniqueId(uniqueID);

            // Update island data
            pd.setIslandActiveTaskCount(this.addon.getSettings().getDefaultActiveTaskCount());
            pd.setIslandBundle(null);

            // Update owner data.
            this.updateOwnerBundle(island, pd);
            this.updateOwnerTaskCount(island, pd);

            // Save data.
            this.saveTaskData(pd);

            // Add to cache
            this.taskDataCache.put(uniqueID, pd);
        }
    }


    /**
     * This method adds, validates and returns island tasks data for given island.
     *
     * @param island Island which data must be returned.
     * @return TaskDataObject or null if failed to create.
     */
    @Nullable
    public TaskDataObject validateIslandData(@Nullable Island island)
    {
        if (island == null || island.getOwner() == null)
        {
            return null;
        }

        this.addIslandData(island);
        TaskDataObject dataObject = this.taskDataCache.get(island.getUniqueId());

        if (dataObject == null)
        {
            return null;
        }

        // Validate data in generator object.
        this.updateOwnerBundle(island, dataObject);
        this.updateOwnerTaskCount(island, dataObject);

        // Remove Tasks From Active tasks List:
        dataObject.getActiveTasks().removeIf(generator ->
        {
            // TODO: Remove tasks that are disabled.
            return false;
        });

        if (dataObject.getActiveTaskCount() > 0 &&
            dataObject.getActiveTaskCount() < dataObject.getActiveTasks().size())
        {
            // There are more active tasks then allowed.
            // Start to remove from first element.

            // TODO: Cancel task if too many is active at the same time.
//            Iterator<String> activeTasks =
//                new ArrayList<>(dataObject.getActiveTasks()).iterator();
//
//            while (dataObject.getActiveTasks().size() > dataObject.getActiveTaskCount() &&
//                activeTasks.hasNext())
//            {
//                dataObject.getActiveTasks().remove(activeTasks.next());
//            }
        }

        return dataObject;
    }


    /**
     * This method updates owner active tasks count.
     *
     * @param island Island object that requires update.
     * @param dataObject Data Object that need to be populated.
     */
    private void updateOwnerTaskCount(@NotNull Island island, @NotNull TaskDataObject dataObject)
    {
        User owner = User.getInstance(island.getOwner());

        // Permission check can be done only to a player object.
        if (owner != null && owner.isPlayer())
        {
            // Update max owner max task count.
            int permissionSize = Utils.getPermissionValue(owner,
                Utils.getPermissionString(island.getWorld(), "[gamemode].tasks.active-tasks"),
                0);
            dataObject.setOwnerActiveTaskCount(permissionSize);
        }
    }


    /**
     * This method updates owner bundle for island.
     *
     * @param island Island object that requires update.
     * @param dataObject Data Object that need to be populated.
     */
    private void updateOwnerBundle(@NotNull Island island, @NotNull TaskDataObject dataObject)
    {
        User owner = User.getInstance(island.getOwner());

        // Permission check can be done only to a player object.
        if (owner != null && owner.isPlayer())
        {
            // Update owner bundle.
            String permissionBundle = Utils.getPermissionValue(owner,
                Utils.getPermissionString(island.getWorld(), "[gamemode].tasks.bundle"),
                null);
            dataObject.setOwnerBundle(permissionBundle);
        }
    }


    /**
     * This method removes data from tasks data cache and database with given id.
     * @param uniqueId Id that must be removed.
     */
    public void wipeIslandData(String uniqueId)
    {
        this.taskDataCache.remove(uniqueId);
        this.taskDataDatabase.deleteID(uniqueId);
    }


// ---------------------------------------------------------------------
// Section: Processing Methods
// ---------------------------------------------------------------------


    /**
     * This method will process task starting.
     * @param taskId TaskId that must be started.
     * @param player Player who started the task.
     * @param islandData Island Data object.
     */
    public void onTaskStart(String taskId, Player player, TaskDataObject islandData)
    {
        TaskObject taskObject = this.taskCache.get(taskId);

        if (taskObject == null)
        {
            // Hmm... weird.
            return;
        }

        // Validation that task can be started.
        if (!this.validateTask(taskObject, User.getInstance(player), islandData))
        {
            this.sendMessage(User.getInstance(player),
                "Cannot start the task.",
                User.getInstance(player),
                taskObject.getName());
            return;
        }

        // Save island data.
        islandData.startTask(taskId);
        this.saveTaskData(islandData);

        Optional<Island> optional = this.addon.getIslands().getIslandById(islandData.getUniqueId());

        if (optional.isEmpty())
        {
            // Hmm... very very weird.
            return;
        }

        // Gets the island object.
        Island island = optional.get();

        // Process start messages.
        taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.START_MESSAGE.equals(option.getType())).
            map(option -> (StartMessageOption) option).
            forEach(startMessage -> {
                switch (startMessage.getMessageType())
                {
                    case BROADCAST ->
                        Bukkit.getOnlinePlayers().stream().map(User::getInstance).forEach(user ->
                            this.sendMessage(user,
                                startMessage.getStartMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case TEAM ->
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.sendMessage(user,
                                startMessage.getStartMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case STARTER ->
                        this.sendMessage(User.getInstance(player),
                            startMessage.getStartMessage(),
                            User.getInstance(player),
                            taskObject.getName());
                }
            });
    }


    /**
     * This method process some things that need to be updated when player progress their task.
     * @param taskId TaskId that must be started.
     * @param player Player who progressed the task.
     * @param islandData Island Data object.
     */
    public void onUpdateProgress(String taskId, Player player, TaskDataObject islandData)
    {
        // Save island data.
        this.saveTaskData(islandData);

        TaskObject taskObject = this.taskCache.get(taskId);

        if (taskObject == null)
        {
            // Hmm... weird.
            return;
        }

        Optional<Island> optional = this.addon.getIslands().getIslandById(islandData.getUniqueId());

        if (optional.isEmpty())
        {
            // Hmm... very very weird.
            return;
        }

        // Gets the island object.
        Island island = optional.get();

        // ToDo: If there is a boss bar, then update it.
    }


    /**
     * This method process task finishing.
     * @param taskId Task ID that is finished.
     * @param player Player who finished the task.
     * @param islandData Data Object for island.
     */
    public void onTaskFinish(String taskId, Player player, TaskDataObject islandData)
    {
        // Save island data.
        islandData.finishTask(taskId);
        this.saveTaskData(islandData);

        TaskObject taskObject = this.taskCache.get(taskId);

        if (taskObject == null)
        {
            // Hmm... weird.
            return;
        }

        Optional<Island> optional = this.addon.getIslands().getIslandById(islandData.getUniqueId());

        if (optional.isEmpty())
        {
            // Hmm... very very weird.
            return;
        }

        // Gets the island object.
        Island island = optional.get();

        // Process all rewards.
        taskObject.getRewardList().forEach(reward ->
        {
            switch (reward.getType())
            {
                case COMMAND -> {
                    if (reward.isTeamPrize())
                    {
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.processCommandReward(user, (CommandReward) reward));
                    }
                    else
                    {
                        this.processCommandReward(User.getInstance(player), (CommandReward) reward);
                    }
                }
                case EXPERIENCE -> {
                    if (reward.isTeamPrize())
                    {
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.processExperienceReward(user, (ExperienceReward) reward));
                    }
                    else
                    {
                        this.processExperienceReward(User.getInstance(player), (ExperienceReward) reward);
                    }
                }
                case ITEM -> {
                    if (reward.isTeamPrize())
                    {
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.processItemReward(user, (ItemReward) reward));
                    }
                    else
                    {
                        this.processItemReward(User.getInstance(player), (ItemReward) reward);
                    }
                }
                case MONEY -> {
                    if (reward.isTeamPrize())
                    {
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.processMoneyReward(user, (MoneyReward) reward));
                    }
                    else
                    {
                        this.processMoneyReward(User.getInstance(player), (MoneyReward) reward);
                    }
                }
            }
        });

        // Process end messages.
        taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.FINISH_MESSAGE.equals(option.getType())).
            map(option -> (FinishMessageOption) option).
            forEach(finishMessage -> {
                switch (finishMessage.getMessageType())
                {
                    case BROADCAST ->
                        Bukkit.getOnlinePlayers().stream().map(User::getInstance).forEach(user ->
                            this.sendMessage(user,
                                finishMessage.getFinishMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case TEAM ->
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.sendMessage(user,
                                finishMessage.getFinishMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case FINISHER ->
                        this.sendMessage(User.getInstance(player),
                            finishMessage.getFinishMessage(),
                            User.getInstance(player),
                            taskObject.getName());
                }
            });
    }


    /**
     * This method returns if given task is valid for starting.
     * @param taskObject Task Object that must be checked.
     * @param user User who started the task.
     * @param islandData Island data that contains tasks.
     * @return {@code true} if task is valid for starting, {@code false} otherwise.
     */
    public boolean validateTask(TaskObject taskObject, User user, TaskDataObject islandData)
    {
        if (islandData.getActiveTaskCount() > 0 &&
            islandData.getActiveTasks().size() > islandData.getActiveTaskCount())
        {
            // To many active tasks.
            return false;
        }

        if (islandData.getActiveTasks().contains(taskObject.getUniqueId()))
        {
            // Task is already started
            return false;
        }

        // TODO: need to adjust start date based on repeating sequence.

        Optional<StartDateOption> startDate = taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.START_DATE.equals(option.getType())).
            map(option -> (StartDateOption) option).
            findFirst();

        if (startDate.isPresent() && !startDate.get().getStartDate().before(new Date()))
        {
            // Task is not started by date.
            return false;
        }

        Optional<EndDateOption> endDate = taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.END_DATE.equals(option.getType())).
            map(option -> (EndDateOption) option).
            findFirst();

        if (endDate.isPresent() && !endDate.get().getEndDate().after(new Date()))
        {
            // Task is already closed by date.
            return false;
        }

        // Check tasks with data in it.
        if (islandData.getTaskStatus().containsKey(taskObject.getUniqueId()))
        {
            boolean completed = islandData.isTaskCompleted(taskObject.getUniqueId());

            if (completed && !taskObject.getOptionList().stream().
                filter(option -> Option.OptionType.REPEATABLE.equals(option.getType())).
                map(option -> ((RepeatableOption) option).isRepeatable()).
                findFirst().
                orElse(false))
            {
                // Task is not repeatable.
                return false;
            }

            Optional<CoolDownOption> coolDown = taskObject.getOptionList().stream().
                filter(option -> Option.OptionType.COOL_DOWN.equals(option.getType())).
                map(option -> (CoolDownOption) option).
                findFirst();

            if (coolDown.isPresent())
            {
                long lastCompletionTime = islandData.getLastCompletionTime(taskObject.getUniqueId());

                if (new Date().getTime() < lastCompletionTime + coolDown.get().getCoolDown())
                {
                    // Cooldown time is not passed.
                    return false;
                }
            }
        }

        // Check task requirements
        return taskObject.getRequirementList().stream().anyMatch(requirement ->
        {
            switch (requirement.getType())
            {
                case EXPERIENCE -> {
                    // TODO: Experience Requirement?
                    return user.getPlayer().getTotalExperience() < 0;
                }
                case LEVEL -> {
                    // Check if user island has large enough level.
                    return this.getIslandLevel(islandData.getUniqueId()) < ((LevelRequirement) requirement).getLevel();
                }
                case MONEY -> {
                    // Check if user has enough balance.
                    return this.getBalance(user) < ((MoneyRequirement) requirement).getMoney();
                }
                case PERMISSION -> {
                    // Check is user has required permission.
                    return !user.hasPermission(((PermissionRequirement) requirement).getPermission());
                }
                case TASK -> {
                    // Check if task is not completed yet.
                    return !islandData.isTaskCompleted(((TaskRequirement) requirement).getTaskId());
                }
                default -> {
                    // Default behaviour
                    return false;
                }
            }
        });
    }


    /**
     * This method returns island level based given island UUID.
     * @param islandUUID island UUID.
     * @return island level.
     */
    private double getIslandLevel(String islandUUID)
    {
        if (!this.addon.hasLevelHook())
        {
            return Double.MAX_VALUE;
        }

        Optional<Island> islandById = this.addon.getIslands().getIslandById(islandUUID);

        if (islandById.isEmpty())
        {
            return 0;
        }

        // Get island level via hook.
        return this.addon.getLevelHook().getIslandLevel(
            islandById.get().getWorld(),
            islandById.get().getOwner());
    }


    /**
     * This method returns user balance in given world.
     * @param user User who balance must be returned.
     * @return Balance of the user.
     */
    private double getBalance(User user)
    {
        if (!this.addon.isEconomyProvided())
        {
            // No economy addon. Return max value.
            return Double.MAX_VALUE;
        }

        return this.addon.getEconomyProvider().getBalance(user, user.getWorld());
    }


    /**
     * This method process rewarded commands to the user.
     * @param user User who will be awarded.
     * @param reward Reward object.
     */
    private void processCommandReward(@Nullable User user, CommandReward reward)
    {
        if (user == null)
        {
            // Null-pointer check.
            return;
        }

        String rewardCommand = reward.getCommand();

        if (rewardCommand.startsWith("[SELF]"))
        {
            String alert = "Running command '" + rewardCommand + "' as " + user.getName();
            this.addon.getLogger().info(alert);
            rewardCommand = rewardCommand.substring(6).replace("[player]", user.getName()).trim();

            try
            {
                if (!user.performCommand(rewardCommand))
                {
                    this.showError(rewardCommand);
                }
            }
            catch (Exception e)
            {
                this.showError(rewardCommand);
                return;
            }
        }

        // Substitute in any references to player
        try
        {
            if (!this.addon.getServer().dispatchCommand(this.addon.getServer().getConsoleSender(),
                rewardCommand.replace("[player]", user.getName())))
            {
                this.showError(rewardCommand);
            }
        }
        catch (Exception e)
        {
            this.showError(rewardCommand);
        }
    }


    /**
     * This method process experience reward.
     * @param user User who receive reward.
     * @param reward Reward object.
     */
    private void processExperienceReward(@Nullable User user, ExperienceReward reward)
    {
        // Experience can be assigned only to online players.
        // TODO: if really necessary, we could add pending rewards?

        if (user != null && user.isOnline())
        {
            user.getPlayer().giveExp((int) reward.getExperience());
        }
    }


    /**
     * This method process item reward.
     * @param user User who receive reward.
     * @param reward Reward object.
     */
    private void processItemReward(@Nullable User user, ItemReward reward)
    {
        if (user != null && user.isOnline())
        {
            // For each item that cannot go into inventory, drop it on the ground.
            user.getInventory().addItem(reward.getItemStack().clone()).forEach((k, v) ->
                user.getWorld().dropItem(user.getLocation(), v));
        }
    }


    /**
     * This method process money reward.
     * @param user User who receive reward.
     * @param reward Reward object.
     */
    private void processMoneyReward(@Nullable User user, MoneyReward reward)
    {
        // Money Reward
        if (user != null && this.addon.isEconomyProvided())
        {
            this.addon.getEconomyProvider().deposit(user, reward.getMoney());
        }
    }


    /**
     * This method sends given message to the receiver, and puts sender as parameter.
     * @param receiver User who receives the message.
     * @param message Message string.
     * @param sender User who triggered message,
     * @param taskName Name of the task.
     */
    private void sendMessage(@Nullable User receiver, String message, User sender, String taskName)
    {
        if (receiver != null && receiver.isOnline())
        {
            Utils.sendMessage(receiver, receiver.getTranslation(message,
                "[player]", sender.getName(),
                "[task]", taskName));
        }
    }


    /**
     * Throws error message.
     * @param command Error message that appear after failing some command.
     */
    private void showError(final String command)
    {
        this.addon.getLogger().severe("Problem executing command executed by player - skipping!");
        this.addon.getLogger().severe(() -> "Command was : " + command);
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * Instance of Tasks Addon class.
     */
    private final TasksAddon addon;

    /**
     * Variable stores map that links String to loaded tasks object.
     */
    private final Map<String, TaskObject> taskCache;

    /**
     * Variable stores database of tasks objects.
     */
    private final Database<TaskObject> taskDatabase;

    /**
     * Variable stores map that links String to loaded tasks data object.
     */
    private final Map<String, TaskDataObject> taskDataCache;

    /**
     * Variable stores database of tasks data objects.
     */
    private final Database<TaskDataObject> taskDataDatabase;

    /**
     * Variable stores map that links String to loaded tasks bundle object.
     */
    private final Map<String, TaskBundleObject> taskBundleCache;

    /**
     * Variable stores database of tasks bundle objects.
     */
    private final Database<TaskBundleObject> taskBundleDatabase;

    /**
     * Set of worlds where gamemode operates.
     */
    public final Set<World> operatingWorlds = new HashSet<>();
}
