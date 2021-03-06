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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.Database;
import world.bentobox.bentobox.database.objects.Island;
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
            this.taskCache.values().stream().map(TaskObject::getTask).forEach(task -> ((Task) task).onStop());
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


    /**
     * Wipe game mode tasks and bundles.
     *
     * @param gameModeAddon the game mode addon
     */
    public void wipeGameModeTasks(GameModeAddon gameModeAddon)
    {
        if (!this.operatingWorlds.contains(gameModeAddon.getOverWorld()))
        {
            return;
        }

        final String objectKey = gameModeAddon.getDescription().getName().toLowerCase();

        // Collect all generators
        List<String> keySet = new ArrayList<>(this.taskCache.keySet());

        // Remove everything that starts with gamemode name.
        keySet.forEach(uniqueId ->
        {
            if (uniqueId.startsWith(objectKey))
            {
                this.taskCache.remove(uniqueId);
                this.taskDatabase.deleteID(uniqueId);
            }
        });

        this.addon.log("All tasks for " + objectKey + " are removed!");

        // Collect all bundles
        keySet = new ArrayList<>(this.taskBundleCache.keySet());

        // Remove everything that starts with gamemode name.
        keySet.forEach(uniqueId ->
        {
            if (uniqueId.startsWith(objectKey))
            {
                this.taskBundleCache.remove(uniqueId);
                this.taskBundleDatabase.deleteID(uniqueId);
            }
        });

        this.addon.log("All bundles for " + objectKey + " are removed!");
    }


// ---------------------------------------------------------------------
// Section: User related data
// ---------------------------------------------------------------------


    /**
     * This method returns list of all tasks per given world.
     * @param world World in which tasks must be returned.
     * @return Sorted task list.
     */
    public List<TaskObject> getAllTasks(World world)
    {
        String gameMode = this.addon.getPlugin().getIWM().getAddon(world).map(
            gameModeAddon -> gameModeAddon.getDescription().getName()).orElse("");

        if (gameMode.isEmpty())
        {
            // If not a gamemode world then return.
            return Collections.emptyList();
        }

        // Filter Tasks for the cache.
        // Get tasks for current gamemode.
        // Sort by task name.
        return this.taskCache.values().stream().
            filter(task -> task.getUniqueId().startsWith(gameMode.toLowerCase())).
            sorted(Comparator.comparing(TaskObject::getName)).
            collect(Collectors.toList());
    }


    /**
     * This method returns accessible tasks for the given islandData.
     * @param world World where tasks must be searched.
     * @param islandData IslandData that contains some info.
     * @return List of tasks objects that are accessible for user.
     */
    public List<TaskObject> getAccessibleTaskList(World world, @Nullable TaskDataObject islandData)
    {
        // Optimization could be done by generating bundles for each situation, but currently I do not
        // think it should be an actual problem here.

        Stream<TaskObject> taskObjectList = this.getAllTasks(world).stream().filter(TaskObject::isEnabled);

        if (islandData != null)
        {
            // Owner bundle has larger priority then island bundle.
            if (islandData.getOwnerBundle() != null &&
                this.taskBundleCache.containsKey(islandData.getOwnerBundle()))
            {
                TaskBundleObject bundle = this.taskBundleCache.get(islandData.getOwnerBundle());

                return taskObjectList.
                    filter(taskObject -> bundle.getTaskSet().contains(taskObject.getUniqueId())).
                    collect(Collectors.toList());
            }
            else if (islandData.getIslandBundle() != null &&
                this.taskBundleCache.containsKey(islandData.getIslandBundle()))
            {
                TaskBundleObject bundle = this.taskBundleCache.get(islandData.getIslandBundle());

                return taskObjectList.
                    filter(taskObject -> bundle.getTaskSet().contains(taskObject.getUniqueId())).
                    collect(Collectors.toList());
            }
            else
            {
                return taskObjectList.collect(Collectors.toList());
            }
        }
        else
        {
            return taskObjectList.collect(Collectors.toList());
        }
    }


    /**
     * This method returns available tasks for the given islandData.
     * @param world World where tasks must be searched.
     * @param islandData IslandData that contains some info.
     * @return List of tasks objects that are available for user.
     */
    public List<TaskObject> getAvailableTaskList(World world, @NotNull User user, @Nullable TaskDataObject islandData)
    {
        // Optimization could be done by generating bundles for each situation, but currently I do not
        // think it should be an actual problem here.

        Stream<TaskObject> taskObjectStream =
            this.getAllTasks(world).stream().
                filter(TaskObject::isEnabled).
                filter(TaskObject::isStarted).
                filter(TaskObject::isNotFinished);

        if (islandData != null)
        {
            // Owner bundle has larger priority then island bundle.
            if (islandData.getOwnerBundle() != null &&
                this.taskBundleCache.containsKey(islandData.getOwnerBundle()))
            {
                TaskBundleObject bundle = this.taskBundleCache.get(islandData.getOwnerBundle());

                taskObjectStream =
                    taskObjectStream.filter(taskObject -> bundle.getTaskSet().contains(taskObject.getUniqueId()));
            }
            else if (islandData.getIslandBundle() != null &&
                this.taskBundleCache.containsKey(islandData.getIslandBundle()))
            {
                TaskBundleObject bundle = this.taskBundleCache.get(islandData.getIslandBundle());

                taskObjectStream =
                    taskObjectStream.filter(taskObject -> bundle.getTaskSet().contains(taskObject.getUniqueId()));
            }

            return taskObjectStream.
                filter(taskObject -> this.canActivateTask(taskObject, user, islandData)).
                collect(Collectors.toList());
        }
        else
        {
            return taskObjectStream.collect(Collectors.toList());
        }
    }


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


    /**
     * Gets task by id.
     *
     * @param taskId the task id
     * @return the task by id
     */
    @Nullable
    public TaskObject getTaskById(String taskId)
    {
        return this.taskCache.get(taskId);
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
                Constants.ERRORS + "cannot-start",
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
                                startMessage.getColoredMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case TEAM ->
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.sendMessage(user,
                                startMessage.getColoredMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case STARTER ->
                        this.sendMessage(User.getInstance(player),
                            startMessage.getColoredMessage(),
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
     * This method triggers task stopping without completing. It will erse the progress.
     * @param taskId TaskId that must be stopped.
     * @param player Player who stopped the task.
     * @param islandData Island Data object.
     */
    public void onTaskStop(String taskId, Player player, TaskDataObject islandData)
    {
        TaskObject taskObject = this.taskCache.get(taskId);

        if (taskObject == null)
        {
            // Hmm... weird.
            return;
        }

        // Save island data.
        islandData.stopTask(taskId);
        this.saveTaskData(islandData);

        this.sendMessage(User.getInstance(player),
            Constants.MESSAGES + "task-stopped",
            User.getInstance(player),
            taskObject.getName());
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
                                finishMessage.getColoredMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case TEAM ->
                        island.getMemberSet().stream().map(User::getInstance).forEach(user ->
                            this.sendMessage(user,
                                finishMessage.getColoredMessage(),
                                User.getInstance(player),
                                taskObject.getName()));
                    case FINISHER ->
                        this.sendMessage(User.getInstance(player),
                            finishMessage.getColoredMessage(),
                            User.getInstance(player),
                            taskObject.getName());
                }
            });
    }


    /**
     * This method returns if given task is valid for starting.
     * @param taskObject Task Object that must be checked.
     * @param islandData Island data that contains tasks.
     * @return {@code true} if task is valid for starting, {@code false} otherwise.
     */
    public boolean canActivateTask(TaskObject taskObject, User user, TaskDataObject islandData)
    {
        if (!taskObject.isStarted() || taskObject.isFinished())
        {
            // Task is not started by date or Task is already closed by date.
            return false;
        }

        // Check tasks with data in it.
        if (islandData.getTaskStatus().containsKey(taskObject.getUniqueId()))
        {
            this.processResetTask(taskObject, user, islandData);

            boolean completed = islandData.isTaskCompleted(taskObject.getUniqueId());
            long numberOfCompletion = islandData.getNumberOfCompletions(taskObject.getUniqueId());

            if (completed && !taskObject.isRepeatable() || taskObject.getRepeats() <= numberOfCompletion)
            {
                // Task is not repeatable or repeat count is reached.
                return false;
            }

            long coolDown = taskObject.getCoolDown();

            if (coolDown > 0)
            {
                long lastCompletionTime = islandData.getLastCompletionTime(taskObject.getUniqueId());

                if (new Date().getTime() < lastCompletionTime + coolDown)
                {
                    // Cooldown time is not passed.
                    return false;
                }
            }
        }

        // Check task requirements
        return taskObject.getRequirementList().stream().noneMatch(requirement ->
        {
            switch (requirement.getType())
            {
                case EXPERIENCE -> {
                    // Check user experience
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
            Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "too-many-active-tasks",
                Constants.NUMBER, String.valueOf(islandData.getActiveTaskCount())));
            return false;
        }

        if (islandData.getActiveTasks().contains(taskObject.getUniqueId()))
        {
            Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "task-already-active"));
            // Task is already started
            return false;
        }

        if (!taskObject.isStarted())
        {
            Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "task-not-started-yet",
                Constants.DATE, String.valueOf(taskObject.getStartDate())));

            // Task is not started by date.
            return false;
        }

        if (taskObject.isFinished())
        {
            Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "task-already-finished",
                Constants.DATE, String.valueOf(taskObject.getFinishDate())));

            // Task is already closed by date.
            return false;
        }

        // Check tasks with data in it.
        if (islandData.getTaskStatus().containsKey(taskObject.getUniqueId()))
        {
            this.processResetTask(taskObject, user, islandData);

            boolean completed = islandData.isTaskCompleted(taskObject.getUniqueId());
            long numberOfCompletion = islandData.getNumberOfCompletions(taskObject.getUniqueId());

            if (completed && !taskObject.isRepeatable() || taskObject.getRepeats() <= numberOfCompletion)
            {
                Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "max-completion-count-reached"));

                // Task is not repeatable or repeat count is reached.
                return false;
            }

            long coolDown = taskObject.getCoolDown();

            if (coolDown > 0)
            {
                long lastCompletionTime = islandData.getLastCompletionTime(taskObject.getUniqueId());

                if (new Date().getTime() < lastCompletionTime + coolDown)
                {
                    Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "cooldown",
                        Constants.COOL_DOWN, String.valueOf(coolDown),
                        Constants.NUMBER, String.valueOf(new Date().getTime() - lastCompletionTime - coolDown)));

                    // Cooldown time is not passed.
                    return false;
                }
            }
        }

        // Check task requirements
        return taskObject.getRequirementList().stream().noneMatch(requirement ->
        {
            switch (requirement.getType())
            {
                case EXPERIENCE -> {
                    // Check user experience
                    if (user.getPlayer().getTotalExperience() < 0)
                    {
                        Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "not-enough-experience",
                            Constants.VALUE, String.valueOf(0)));
                        return true;
                    }

                    return false;
                }
                case LEVEL -> {
                    // Check if user island has large enough level.
                    if (this.getIslandLevel(islandData.getUniqueId()) < ((LevelRequirement) requirement).getLevel())
                    {
                        Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "not-enough-level",
                            Constants.VALUE, String.valueOf(((LevelRequirement) requirement).getLevel())));

                        return true;
                    }

                    return false;
                }
                case MONEY -> {
                    // Check if user has enough balance.
                    if (this.getBalance(user) < ((MoneyRequirement) requirement).getMoney())
                    {
                        Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "not-enough-money",
                            Constants.VALUE, String.valueOf(((MoneyRequirement) requirement).getMoney())));

                        return true;
                    }

                    return false;
                }
                case PERMISSION -> {
                    // Check is user has required permission.
                    if (!user.hasPermission(((PermissionRequirement) requirement).getPermission()))
                    {
                        Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "missing-permission",
                            Constants.VALUE, ((PermissionRequirement) requirement).getPermission()));

                        return true;
                    }

                    return false;
                }
                case TASK -> {
                    // Check if task is not completed yet.
                    if (!islandData.isTaskCompleted(((TaskRequirement) requirement).getTaskId()))
                    {
                        Utils.sendMessage(user, user.getTranslation(Constants.MESSAGES + "missing-task",
                            Constants.VALUE, ((TaskRequirement) requirement).getTaskId()));

                        return true;
                    }

                    return false;
                }
                default -> {
                    // Default behaviour
                    return false;
                }
            }
        });
    }


    /**
     * This method process task resetting upon reaching some date.
     * @param taskObject Task that must be checked for reset.
     * @param user User who triggers check.
     * @param islandData Island Data for user.
     */
    private void processResetTask(TaskObject taskObject, User user, TaskDataObject islandData)
    {
        long lastCompletionTime = islandData.getLastCompletionTime(taskObject.getUniqueId());

        if (lastCompletionTime <= 0)
        {
            // Cannot parse task completion here.
            return;
        }

        // Daily Reset
        Optional<Option> optionalReset = taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.DAILY_RESET.equals(option.getType())).
            findFirst();

        if (optionalReset.isPresent())
        {
            DailyResetOption resetOption = (DailyResetOption) optionalReset.get();

            // Get calender instance based on timezone in settings.
            Calendar calendar = Calendar.getInstance(this.addon.getSettings().getTimeZone());
            // Set h for the task.
            calendar.set(Calendar.HOUR_OF_DAY, resetOption.getHour());

            // Check if last completion time was before reset timer.
            if (lastCompletionTime <= calendar.getTimeInMillis())
            {
                // Reset the island data.
                islandData.resetCurrentProgress(taskObject.getUniqueId());

                // Exit, as each task should have only 1 reset option.
                return;
            }
        }

        // Weekly Reset
        optionalReset = taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.WEEKLY_RESET.equals(option.getType())).
            findFirst();

        if (optionalReset.isPresent())
        {
            WeeklyResetOption resetOption = (WeeklyResetOption) optionalReset.get();

            // Get calender instance based on timezone in settings.
            Calendar calendar = Calendar.getInstance(this.addon.getSettings().getTimeZone());
            // Set h for the task.
            calendar.set(Calendar.HOUR_OF_DAY, resetOption.getHour());
            calendar.set(Calendar.DAY_OF_WEEK, resetOption.getDay().getValue());

            // Check if last completion time was before reset timer.
            if (lastCompletionTime <= calendar.getTimeInMillis())
            {
                // Reset the island data.
                islandData.resetCurrentProgress(taskObject.getUniqueId());

                // Exit, as each task should have only 1 reset option.
                return;
            }
        }

        // Monthly Reset
        optionalReset = taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.MONTHLY_RESET.equals(option.getType())).
            findFirst();

        if (optionalReset.isPresent())
        {
            MonthlyResetOption resetOption = (MonthlyResetOption) optionalReset.get();

            // Get calender instance based on timezone in settings.
            Calendar calendar = Calendar.getInstance(this.addon.getSettings().getTimeZone());
            // Set h for the task.
            calendar.set(Calendar.HOUR_OF_DAY, resetOption.getHour());
            calendar.set(Calendar.DAY_OF_MONTH, resetOption.getDay());

            // Check if last completion time was before reset timer.
            if (lastCompletionTime <= calendar.getTimeInMillis())
            {
                // Reset the island data.
                islandData.resetCurrentProgress(taskObject.getUniqueId());

                // Exit, as each task should have only 1 reset option.
                return;
            }
        }

        // Yearly Reset
        optionalReset = taskObject.getOptionList().stream().
            filter(option -> Option.OptionType.YEARLY_RESET.equals(option.getType())).
            findFirst();

        if (optionalReset.isPresent())
        {
            YearlyResetOption resetOption = (YearlyResetOption) optionalReset.get();

            // Get calender instance based on timezone in settings.
            Calendar calendar = Calendar.getInstance(this.addon.getSettings().getTimeZone());
            // Set h for the task.
            calendar.set(Calendar.HOUR_OF_DAY, resetOption.getHour());
            calendar.set(Calendar.DAY_OF_MONTH, resetOption.getDay());
            calendar.set(Calendar.MONTH, resetOption.getMonth().getValue());

            // Check if last completion time was before reset timer.
            if (lastCompletionTime <= calendar.getTimeInMillis())
            {
                // Reset the island data.
                islandData.resetCurrentProgress(taskObject.getUniqueId());

                // Exit, as each task should have only 1 reset option.
                return;
            }
        }
    }


    /**
     * This method returns island level based given island UUID.
     * @param islandUUID island UUID.
     * @return island level.
     */
    public double getIslandLevel(String islandUUID)
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
                Constants.PLAYER, sender.getName(),
                Constants.TASK, taskName));
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
