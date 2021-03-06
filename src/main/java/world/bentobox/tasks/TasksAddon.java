package world.bentobox.tasks;


import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.hooks.VaultHook;
import world.bentobox.level.Level;
import world.bentobox.tasks.commands.admin.AdminCommand;
import world.bentobox.tasks.commands.player.PlayerCommand;
import world.bentobox.tasks.configs.Settings;
import world.bentobox.tasks.managers.ImportManager;
import world.bentobox.tasks.managers.TasksManager;


/**
 * This is main Addon class. It allows to load it into BentoBox hierarchy.
 */
public class TasksAddon extends Addon
{

	// ---------------------------------------------------------------------
	// Section: Methods
	// ---------------------------------------------------------------------


	/**
	 * Executes code when loading the addon. This is called before {@link #onEnable()}.
	 * This <b>must</b> be used to setup configuration, worlds and commands.
	 */
	@Override
	public void onLoad()
	{
		super.onLoad();
		this.saveDefaultConfig();

		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			this.logError("TasksAddon settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}

		// Set current instance as TasksAddon.
		TasksAddon.instance = this;
	}


	/**
	 * Executes code when enabling the addon. This is called after {@link #onLoad()}.
	 * <br/> Note that commands and worlds registration <b>must</b> be done in {@link
	 * #onLoad()}, if need be. Failure to do so <b>will</b> result in issues such as
	 * tab-completion not working for commands.
	 */
	@Override
	public void onEnable()
	{
		// Check if it is enabled - it might be loaded, but not enabled.

		if (this.getPlugin() == null || !this.getPlugin().isEnabled())
		{
			Bukkit.getLogger().severe("BentoBox is not available or disabled!");
			this.setState(State.DISABLED);
			return;
		}

		// Check if addon is not disabled before.

		if (this.getState().equals(State.DISABLED))
		{
			Bukkit.getLogger().severe("TasksAddon is not available or disabled!");
			return;
		}

		this.manager = new TasksManager(this);
		this.manager.load();

		this.importManager = new ImportManager(this);

		this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
			if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName()))
			{
				gameModeAddon.getPlayerCommand().ifPresent(
					playerCommand -> new PlayerCommand(this, playerCommand));
				gameModeAddon.getAdminCommand().ifPresent(
					playerCommand -> new AdminCommand(this, playerCommand));

				// Add all worlds into map. It would speed up dropping events that comes from different worlds.
				this.manager.addWorld(gameModeAddon.getOverWorld());

				if (gameModeAddon.getWorldSettings().isNetherGenerate())
				{
					this.manager.addWorld(gameModeAddon.getNetherWorld());
				}

				if (gameModeAddon.getWorldSettings().isEndGenerate())
				{
					this.manager.addWorld(gameModeAddon.getEndWorld());
				}
			}
		});

		// BentoBox does not manage money, but it provides VaultHook that does it.
		// I suggest to do the same trick as with Level addon. Create local variable and
		// store if Vault is present there.

		this.vaultHook = this.getPlugin().getVault();

		// Even if Vault is installed, it does not mean that economy can be used. It is
		// necessary to check it via VaultHook#hook() method.

		if (!this.vaultHook.isPresent() || !this.vaultHook.get().hook())
		{
			this.logWarning("Economy plugin not found by Tasks Addon at the startup!");
		}

		this.levelHook = this.getPlugin().getAddonsManager().getAddonByName("Level");

		// Register Request Handlers
		//this.registerRequestHandler(EXAMPLE_REQUEST_HANDLER);
	}


	/**
	 * Executes code when reloading the addon.
	 */
	@Override
	public void onReload()
	{
		super.onReload();

		this.settings = new Config<>(this, Settings.class).loadConfigObject();

		if (this.settings == null)
		{
			this.logError("Tasks Addon settings could not load! Addon disabled.");
			this.setState(State.DISABLED);
		}
	}


	/**
	 * Executes code when disabling the addon.
	 */
	@Override
	public void onDisable()
	{
		// Save Task Manager.
		this.manager.save(true);
	}


	// ---------------------------------------------------------------------
	// Section: Getters
	// ---------------------------------------------------------------------


	/**
	 * This method returns Tasks Manager instance.
	 * @return TasksManager instance.
	 */
	public TasksManager getAddonManager()
	{
		return this.manager;
	}


	/**
	 * This method returns Tasks Import Manager instance.
	 * @return ImportManager instance.
	 */
	public ImportManager getImportManager()
	{
		return this.importManager;
	}


	/**
	 * This method returns Settings instance.
	 * @return Settings instance.
	 */
	public Settings getSettings()
	{
		return this.settings;
	}


	/**
	 * This getter will allow to access to VaultHook. It is written so that it could
	 * return null, if Vault is not present.
	 * @return {@code VaultHook} if it is present, {@code null} otherwise.
	 */
	public VaultHook getEconomyProvider()
	{
		return this.vaultHook.orElse(null);
	}


	/**
	 * Returns if VaultHook is present and hooked.
	 * @return Return boolean if vault hook is present and enabled.
	 */
	public boolean isEconomyProvided()
	{
		return this.vaultHook.isPresent() && this.vaultHook.get().hook();
	}


	/**
	 * This method returns if Level addon is installed.
	 * @return {@code true} if level addon is installed, {@code false} otherwise.
	 */
	public boolean hasLevelHook()
	{
		return levelHook.isPresent();
	}


	/**
	 * @return Level addon class or null.
	 */
	public @Nullable Level getLevelHook()
	{
		return this.levelHook.orElse(null);
	}


	/**
	 * Returns the instance of TaskAddon.
	 * @return TaskAddon instance.
	 */
	public static TasksAddon getInstance()
	{
		return instance;
	}


	// ---------------------------------------------------------------------
	// Section: Variables
	// ---------------------------------------------------------------------

	/**
	 * Settings object contains
	 */
	private Settings settings;

	/**
	 * Tasks Manager class.
	 */
	private TasksManager manager;

	/**
	 * Tasks Import Manager class.
	 */
	private ImportManager importManager;

	/**
	 * Local variable that stores if vaultHook is present.
	 */
	private Optional<VaultHook> vaultHook;

	/**
	 * Local variable that stores if Level is present.
	 */
	private Optional<Level> levelHook;

	/**
	 * Instance of the TaskAddon.
	 */
	private static TasksAddon instance;
}
