/*
 * Created on Jul 26, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core.gui;

import uk.ac.stand.dcs.asa.networkmonitor.core.Preferences;

/**
 * Defines the methods that the preferences window expects individual
 * preference tabs to provide.
 * 
 * @author gjh1
 */
public interface PreferenceTab {
    /**
     * Instruct the preference tab to load the preferences it uses from the
     * given <code>Preferences</code> object.
     * 
     * @param prefs the <code>Preferences</code> instance to use
     */
    public void loadPreferences(Preferences prefs);

    /**
     * Instruct the preference tab to save the preferences it uses to the
     * given <code>Preferences</code> object.
     * 
     * @param prefs the <code>Preferences</code> instance to use
     */
    public void savePreferences(Preferences prefs);
}
