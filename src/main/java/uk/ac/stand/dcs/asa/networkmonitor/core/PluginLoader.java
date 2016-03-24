/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;


import uk.ac.stand.dcs.asa.eventModel.eventBus.busInterfaces.EventConsumer;
import uk.ac.stand.dcs.asa.networkmonitor.core.events.EventBus;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.Plugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.output.OutputPlugin;
import uk.ac.stand.dcs.asa.networkmonitor.plugins.overlay.OverlayPlugin;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;

/**
 * Singleton responsible for querying and loading NetworkMonitor plugins.
 * 
 * @author gjh1
 */
public class PluginLoader {
	private static PluginLoader uniqueInstance = new PluginLoader();

	/**
     * <code>Map</code> containing the plugins loaded by the system and their associated factory objects.
     */
    private Map registry;

    private PluginLoader() {
        registry = Collections.synchronizedMap(new HashMap());
    }

    /**
     * @return the unique instance of this singleton class
     */
    public static PluginLoader getInstance() {
    		return uniqueInstance;
    }

    /**
     * Adds a factory for a particular plugin to the registry.
     * 
     * @param systemName the name by which the plugin is known in the registry
     * @param factory the factory class for this plugin
     */
    public void addFactoryToRegistry(String systemName, Class pluginClass, PluginFactory factory, boolean hasUserInterface) {
        RegistryEntry entry = new RegistryEntry(pluginClass, factory, hasUserInterface);

        synchronized(registry) {
            registry.put(systemName, entry);
        }

        Diagnostic.traceNoSource(Plugin.LOG_PREFIX + systemName + " added to registry.", Diagnostic.INIT);
        printRegistry();
    }

    /**
     * Add a new plugin instance to the appropriate registry entry.
     * 
     * @param systemName the name by which the plugin is known in the registry
     * @param plugin the plugin instance to be added
     * @throws Exception if the specified name is not in the registry
     */
    public void addPluginToRegistry(String systemName, Plugin plugin) throws Exception {
        RegistryEntry entry = (RegistryEntry) registry.get(systemName);
        if (entry != null) {
            synchronized(registry) {
                entry.getInstances().add(plugin);
            }
            printRegistry();
        }
        else {
            // TODO Change to use a subclass?
            throw new Exception("Name not in registry.");
        }
    }

    /**
     * @return the names of all the plugins detected by the plugin loader
     */
    public Set getAllPluginNames() {
        return registry.keySet();
    }

    /**
     * @return the names of all the plugins without a user interface
     */
    public Set getNonVisualPluginNames() {
        Set result = new HashSet();
        synchronized(registry) {
            Iterator i = registry.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry mapEntry = (Entry) i.next();
                RegistryEntry regEntry = (RegistryEntry) mapEntry.getValue();
                if (!regEntry.getHasUserInterface())
                    result.add(mapEntry.getKey());
            }
        }
        return result;
    }

    public Set getOverlayPluginNames() {
        Set result = new HashSet();
        synchronized(registry) {
            Iterator i = registry.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry mapEntry = (Entry) i.next();
                RegistryEntry regEntry = (RegistryEntry) mapEntry.getValue();
                if (OverlayPlugin.class.isAssignableFrom(regEntry.getPluginClass()))
                    result.add(mapEntry.getKey());
            }
        }
        return result;
    }

    /**
     * @return the names of all the plugins with a user interface
     */
    public Set getVisualPluginNames() {
        Set result = new HashSet();
        synchronized(registry) {
            Iterator i = registry.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry mapEntry = (Entry) i.next();
                RegistryEntry regEntry = (RegistryEntry) mapEntry.getValue();
                if (regEntry.getHasUserInterface())
                    result.add(mapEntry.getKey());
            }
        }
        return result;
    }

    /**
     * Load non-visual plugins at application startup.
     * 
     * @param prefs the <code>Preferences</code> object used to determine which plugins to load
     */
    public void loadNonVisualPlugins(Preferences prefs) {
        Iterator i = prefs.getAllPreferenceNames().iterator();
        while (i.hasNext()) {
            String nextName = (String) i.next();
            if (nextName.startsWith(Preferences.LOAD_NON_VISUAL_PLUGINS_PREFIX) && prefs.getPreference(nextName, "false").equals("true")) {
                String pluginSystemName = nextName.substring(nextName.lastIndexOf("-") + 1);
                try {
                    Plugin newPlugin = lookupFactory(pluginSystemName).createNewInstance();
                    newPlugin.load();
                    addPluginToRegistry(newPlugin.getSystemName(), newPlugin);
                    if (newPlugin instanceof OutputPlugin || newPlugin instanceof OverlayPlugin)
                        EventBus.getInstance().registerConsumer((EventConsumer) newPlugin);
                }
                catch (Exception e) {
                    Error.exceptionError("Error loading plugin.", e);
                }
            }
        }
    }

    /**
     * Perform a registry lookup to obtain the factory for a given plugin name.
     * 
     * @param systemName the name by which the plugin is known in the registry
     * @return the factory class for the plugin
     * @throws Exception if the specified name is not in the registry
     */
    public PluginFactory lookupFactory(String systemName) throws Exception {
        RegistryEntry entry = (RegistryEntry) registry.get(systemName);
        if (entry != null) {
            return entry.getFactory();
        }
        else {
            // TODO Change to use a subclass?
            throw new Exception("Name not in registry.");
        }
    }

    /**
     * Determine which plugin a given <code>JPanel</code> corresponds to.
     * 
     * @param pluginPanel the <code>JPanel</code> to search for
     * @return the <code>Plugin</code> it belongs to, or null
     */
    public Plugin lookupPluginFromPanel(JPanel pluginPanel) {
        synchronized(registry) {
            Iterator i = registry.values().iterator();
            while (i.hasNext()) {
                Iterator j = ((RegistryEntry) i.next()).getInstances().iterator();
                while (j.hasNext()) {
                    Plugin pluginInstance = (Plugin) j.next();
                    if (pluginInstance.getPluginPanel() == pluginPanel)
                        return pluginInstance;
                }
            }
            return null;
        }
    }

    /**
     * Remove an instance of a particular plugin from the registry.
     * 
     * @param systemName the name by which the plugin is known in the registry
     * @param plugin the instance of the plugin to remove
     * @throws Exception if the specified name is not in the registry
     */
    public void removePluginFromRegistry(String systemName, Plugin plugin) throws Exception {
        RegistryEntry entry = (RegistryEntry) registry.get(systemName);
        if (entry != null) {
            List instances = entry.getInstances();
            Iterator i = instances.iterator();
            while (i.hasNext()) {
                if (i.next() == plugin) {
                    i.remove();
                }
            }
            printRegistry();
        }
        else {
            // TODO Change to use a subclass?
            throw new Exception("Name not in registry.");
        }
    }

    /**
     * Search the given filesystem path for valid NetworkMonitor plugins. If any are found, add them
     * to the plugin registry.
     * 
     * @param path the filesystem path to scan
     */
    public void scanPathForPlugins(String path) {
        File dir = new File(path);
        File[] entries = dir.listFiles();
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                File current = entries[i];
                // If the current file is a directory, recurse into it
                if (current.isDirectory()) {
                    scanPathForPlugins(current.getPath());
                }
                else if (current.getName().endsWith(".class")) {
                    // The current file is probably a class, so check to see if it is a plugin
                    checkAndRegister(convertPathToClassName(current.getPath()));
                }
            }
        }
    }

    /**
     * Unload all currently loaded plugins.
     */
    public void unloadAllPlugins() {
        Diagnostic.traceNoSource(Plugin.LOG_PREFIX + "Unloading all plugins...", Diagnostic.FINAL);

        synchronized(registry) {
            for (Iterator i = registry.values().iterator(); i.hasNext();) {
                RegistryEntry entry = (RegistryEntry) i.next();
                for (Iterator j = entry.getInstances().iterator(); j.hasNext();) {
                    Plugin plugin = (Plugin) j.next();
                    plugin.unload();
                    j.remove();
                }
                i.remove();
            }
        }

        printRegistry();
    }

    /**
     * Check if the given class is a valid plugin and if so, add it to the registry.
     * 
     * @param className the class to be checked
     */
    private void checkAndRegister(String className) {
        try {
            Class c = Class.forName(className);
            if (Plugin.class.isAssignableFrom(c)) {
                c.newInstance();
            }
        }
        catch (ClassNotFoundException e) {
            Error.exceptionError("The specified class file was not found.", e);
        }
        catch (InstantiationException e) {
            // Ignore failed attempt to instantiate abstract class
        }
        catch (IllegalAccessException e) {
            Error.exceptionError("Creation of this object is not permitted.", e);
        }
    }

    /**
     * Convert the given file path to a fully-qualified classname, suitable for loading.
     * 
     * @param path the path to be converted
     * @return the classname
     */
    private String convertPathToClassName(String path) {
        return path.substring(0, path.lastIndexOf(".")).replaceAll(File.separator, ".");
    }

    /**
     * Print the contents of the plugin registry for diagnostic purposes.
     */
    private void printRegistry() {
        Diagnostic.trace(registry.toString(), Diagnostic.FULL);
    }
}
