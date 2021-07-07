package world.bentobox.tasks;


import org.bukkit.Bukkit;

import java.util.Optional;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.bentobox.hooks.VaultHook;
import world.bentobox.tasks.configs.Settings;
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

		this.getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
			if (!this.settings.getDisabledGameModes().contains(gameModeAddon.getDescription().getName()))
			{
//				gameModeAddon.getPlayerCommand().ifPresent(
//					playerCommand -> new PlayerCommand(this, playerCommand));
//				gameModeAddon.getPlayerCommand().ifPresent(
//					playerCommand -> new AdminCommand(this, playerCommand));
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
			this.logWarning("Economy plugin not found by Tasks Addon!");
		}

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
		if (this.settings != null)
		{
			new Config<>(this, Settings.class).saveConfigObject(this.settings);
		}
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
	public VaultHook getVaulHook()
	{
		return this.vaultHook.orElse(null);
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
	 * Local variable that stores if vaultHook is present.
	 */
	private Optional<VaultHook> vaultHook;
}
