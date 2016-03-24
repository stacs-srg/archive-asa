/*
 * Created on Jun 15, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.plugins;

/**
 * A factory class for producing <code>Plugin</code> instances.
 * 
 * @author gjh1
 */
public abstract class PluginFactory {
    /**
     * Creates and returns a new instance of the class this factory is for.
     * 
     * @return a new instance of the class
     */
    public abstract Plugin createNewInstance();
}
