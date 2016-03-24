/*
 * Created on Jun 16, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;


import uk.ac.stand.dcs.asa.networkmonitor.plugins.PluginFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a reference to a particular plugin factory
 * and also to all instances of that plugin type.
 * 
 * @author gjh1
 */
class RegistryEntry {
    /**
     * The factory object for this particular type of plugin.
     */
    private PluginFactory factory;

    /**
     * Whether this type of plugin has a user interface.
     */
    private boolean hasUserInterface;

    /**
     * All currently loaded instances of this plugin class.
     */
    private List instances;

    /**
     * The <code>Class</code> of the plugins managed by this entry.
     */
    private Class pluginClass;

    public RegistryEntry(Class pluginClass, PluginFactory factory, boolean hasUserInterface) {
        this.factory = factory;
        this.hasUserInterface = hasUserInterface;
        this.instances = new ArrayList();
        this.pluginClass = pluginClass;
    }

    public PluginFactory getFactory() {
        return factory;
    }

    public boolean getHasUserInterface() {
        return hasUserInterface;
    }

    public List getInstances() {
        return instances;
    }

    public Class getPluginClass() {
        return pluginClass;
    }

    public String toString() {
        return String.valueOf(this.instances.size());
    }
}
