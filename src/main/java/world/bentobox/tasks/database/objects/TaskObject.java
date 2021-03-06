//
// Created by BONNe
// Copyright - 2021
//


package world.bentobox.tasks.database.objects;


import com.google.gson.annotations.Expose;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.stream.Collectors;

import world.bentobox.bentobox.database.objects.DataObject;
import world.bentobox.bentobox.database.objects.Table;
import world.bentobox.tasks.TasksAddon;
import world.bentobox.tasks.database.objects.options.*;
import world.bentobox.tasks.database.objects.requirements.*;
import world.bentobox.tasks.database.objects.rewards.Reward;
import world.bentobox.tasks.listeners.tasks.Task;


/**
 * This class contains data about Tasks Object.
 */
@Table(name = "Task")
public class TaskObject implements DataObject
{
    /**
     * Instantiates a new Tasks object.
     */
    public TaskObject()
    {
        // Empty constructor
    }


// ---------------------------------------------------------------------
// Section: Processing methods
// ---------------------------------------------------------------------


    /**
     * Add option to the TaskObject.
     * @param option Option that must be added.
     */
    public void addOption(Option option)
    {
        if (this.optionList == null)
        {
            this.optionList = new ArrayList<>();
        }

        this.optionList.add(option);
    }


    /**
     * Remove option.
     *
     * @param option the option
     */
    public void removeOption(Option option)
    {
        this.optionList.remove(option);
    }


    /**
     * Add requirement to the TaskObject.
     * @param requirement Requirement that must be added.
     */
    public void addRequirement(Requirement requirement)
    {
        if (this.requirementList == null)
        {
            this.requirementList = new ArrayList<>();
        }

        this.requirementList.add(requirement);
    }


    /**
     * Remove requirement.
     *
     * @param requirements the requirements
     */
    public void removeRequirement(Requirement requirements)
    {
        this.requirementList.remove(requirements);
    }


    /**
     * Add reward to the TaskObject.
     * @param reward Reward that must be added.
     */
    public void addReward(Reward reward)
    {
        if (this.rewardList == null)
        {
            this.rewardList = new ArrayList<>();
        }

        this.rewardList.add(reward);
    }


    /**
     * Remove reward.
     *
     * @param reward the reward
     */
    public void removeReward(Reward reward)
    {
        this.rewardList.remove(reward);
    }


// ---------------------------------------------------------------------
// Section: Access methods
// ---------------------------------------------------------------------


    /**
     * This method returns if given task is started.
     * @return {@code true} if task has start date option and it is smaller than current date, {@code false} otherwise.
     */
    public boolean isStarted()
    {
        Optional<StartDateOption> startDate = this.getOptionList().stream().
            filter(option -> Option.OptionType.START_DATE.equals(option.getType())).
            map(option -> (StartDateOption) option).
            findFirst();

        // Task is not started by date.
        return startDate.isEmpty() || startDate.get().getStartDate().before(
            Calendar.getInstance(this.addon.getSettings().getTimeZone()).getTime());
    }


    /**
     * This method returns if given task is finished.
     * @return {@code true} if task has end date option and it is smaller than current date, {@code false} otherwise.
     */
    public boolean isFinished()
    {
        Optional<EndDateOption> endDate = this.getOptionList().stream().
            filter(option -> Option.OptionType.END_DATE.equals(option.getType())).
            map(option -> (EndDateOption) option).
            findFirst();

        // Task is already closed by date.
        return endDate.isPresent() && endDate.get().getEndDate().before(
            Calendar.getInstance(this.addon.getSettings().getTimeZone()).getTime());
    }


    /**
     * This method returns if task is not finished yet.
     * @return {@code true} if task is not finished, {@code false} otherwise.
     */
    public boolean isNotFinished()
    {
        return !this.isFinished();
    }


    /**
     * @return Long of date when task starts.
     */
    public long getStartDate()
    {
        Optional<StartDateOption> startDate = this.getOptionList().stream().
            filter(option -> Option.OptionType.START_DATE.equals(option.getType())).
            map(option -> (StartDateOption) option).
            findFirst();

        // Task is not started by date.
        return startDate.isEmpty() ? 0 : startDate.get().getStartDate().getTime();
    }


    /**
     * @return Long of date when task ends.
     */
    public long getFinishDate()
    {
        Optional<EndDateOption> endDate = this.getOptionList().stream().
            filter(option -> Option.OptionType.END_DATE.equals(option.getType())).
            map(option -> (EndDateOption) option).
            findFirst();

        // Task is not started by date.
        return endDate.isEmpty() ? 0 : endDate.get().getEndDate().getTime();
    }


    /**
     * @return Long for cool down between repeats.
     */
    public long getCoolDown()
    {
        Optional<CoolDownOption> coolDown = this.getOptionList().stream().
            filter(option -> Option.OptionType.COOL_DOWN.equals(option.getType())).
            map(option -> (CoolDownOption) option).
            findFirst();

        return coolDown.isEmpty() ? 0 : coolDown.get().getCoolDown();
    }


    /**
     * @return Long for number of repeats.
     */
    public long getRepeats()
    {
        Optional<RepeatableOption> repeatable = this.getOptionList().stream().
            filter(option -> Option.OptionType.REPEATABLE.equals(option.getType())).
            map(option -> (RepeatableOption) option).
            findFirst();

        return repeatable.isEmpty() ? 1 : repeatable.get().getNumberOfRepeats();
    }


    /**
     * @return Boolean if task is repeatable.
     */
    public boolean isRepeatable()
    {
        Optional<RepeatableOption> repeatable = this.getOptionList().stream().
            filter(option -> Option.OptionType.REPEATABLE.equals(option.getType())).
            map(option -> (RepeatableOption) option).
            findFirst();

        return repeatable.isPresent() && repeatable.get().isRepeatable();
    }


    /**
     * @return Icon of the task object or paper.
     */
    public ItemStack getIcon()
    {
        return this.getOptionList().stream().
            filter(option -> Option.OptionType.ICON.equals(option.getType())).
            map(option -> (IconOption) option).
            findFirst().
            map(IconOption::getIcon).
            orElseGet(() -> new ItemStack(Material.PAPER));
    }


    /**
     * @return Description of the task object or empty list.
     */
    public List<String> getDescription()
    {
        return this.getOptionList().stream().
            filter(option -> Option.OptionType.DESCRIPTION.equals(option.getType())).
            map(option -> (DescriptionOption) option).
            findFirst().
            map(DescriptionOption::getDescription).
            orElseGet(Collections::emptyList);
    }


    /**
     * Gets required permissions.
     *
     * @return the required permissions
     */
    public List<String> getRequiredPermissions()
    {
        return this.getRequirementList().stream().
            filter(requirement -> Requirement.RequirementType.PERMISSION.equals(requirement.getType())).
            map(option -> (PermissionRequirement) option).
            map(PermissionRequirement::getPermission).
            collect(Collectors.toList());
    }


    /**
     * Gets required tasks.
     *
     * @return the required tasks
     */
    public List<String> getRequiredTasks()
    {
        return this.getRequirementList().stream().
            filter(requirement -> Requirement.RequirementType.TASK.equals(requirement.getType())).
            map(option -> (TaskRequirement) option).
            map(TaskRequirement::getTaskId).
            collect(Collectors.toList());
    }


    /**
     * Gets required level.
     *
     * @return the required level
     */
    public double getRequiredLevel()
    {
        return this.getRequirementList().stream().
            filter(requirement -> Requirement.RequirementType.LEVEL.equals(requirement.getType())).
            map(option -> (LevelRequirement) option).
            map(LevelRequirement::getLevel).
            findFirst().
            orElse(0.0);
    }


    /**
     * Gets required money.
     *
     * @return the required money
     */
    public double getRequiredMoney()
    {
        return this.getRequirementList().stream().
            filter(requirement -> Requirement.RequirementType.MONEY.equals(requirement.getType())).
            map(option -> (MoneyRequirement) option).
            map(MoneyRequirement::getMoney).
            findFirst().
            orElse(0.0);
    }


// ---------------------------------------------------------------------
// Section: Public Setters and Getters
// ---------------------------------------------------------------------


    /**
     * @return the uniqueId
     */
    @Override
    public String getUniqueId()
    {
        return this.uniqueId;
    }


    /**
     * @param uniqueId - unique ID the uniqueId to set
     */
    @Override
    public void setUniqueId(String uniqueId)
    {
        this.uniqueId = uniqueId;
    }


    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Gets task.
     *
     * @return the task
     */
    public <T extends Task> T getTask()
    {
        return (T) task;
    }


    /**
     * Sets task.
     *
     * @param task the task
     */
    public void setTask(Task task)
    {
        this.task = task;
    }


    /**
     * Gets option list.
     *
     * @return the option list
     */
    public List<Option> getOptionList()
    {
        return Objects.requireNonNullElse(this.optionList, Collections.emptyList());
    }


    /**
     * Sets option list.
     *
     * @param optionList the option list
     */
    public void setOptionList(List<Option> optionList)
    {
        this.optionList = optionList;
    }


    /**
     * Gets requirements.
     *
     * @return the requirements
     */
    public List<Requirement> getRequirementList()
    {
        return Objects.requireNonNullElse(this.requirementList, Collections.emptyList());
    }


    /**
     * Sets requirements.
     *
     * @param requirementList the requirements
     */
    public void setRequirementList(List<Requirement> requirementList)
    {
        this.requirementList = requirementList;
    }


    /**
     * Gets rewards.
     *
     * @return the rewards
     */
    public List<Reward> getRewardList()
    {
        return Objects.requireNonNullElse(this.rewardList, Collections.emptyList());
    }


    /**
     * Sets rewards.
     *
     * @param rewardList the rewards
     */
    public void setRewardList(List<Reward> rewardList)
    {
        this.rewardList = rewardList;
    }


    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    public boolean isEnabled()
    {
        return enabled;
    }


    /**
     * Sets enabled.
     *
     * @param enabled the enabled
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }


// ---------------------------------------------------------------------
// Section: Variables
// ---------------------------------------------------------------------


    /**
     * Task addon instance.
     */
    private final TasksAddon addon = TasksAddon.getInstance();

    /**
     * UniqueId for the object.
     */
    @Expose
    private String uniqueId;

    /**
     * Name of the task.
     */
    @Expose
    private String name;

    /**
     * Tasks class that will be processed.
     */
    @Expose
    private Task task;

    /**
     * Task options for customization.
     */
    @Expose
    private List<Option> optionList;

    /**
     * Task requirements that must be fulfilled
     */
    @Expose
    private List<Requirement> requirementList;

    /**
     * Task rewards that must be given out.
     */
    @Expose
    private List<Reward> rewardList;

    /**
     * Option to disable/enable task for users.
     */
    @Expose
    private boolean enabled;
}