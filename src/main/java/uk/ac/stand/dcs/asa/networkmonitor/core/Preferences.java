/*
 * Created on Jul 27, 2005
 */
package uk.ac.stand.dcs.asa.networkmonitor.core;


import uk.ac.stand.dcs.asa.networkmonitor.core.gui.NetworkMonitor;
import uk.ac.stand.dcs.asa.util.Diagnostic;
import uk.ac.stand.dcs.asa.util.Error;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * A generic utility class for querying, updating, loading and saving application preferences.
 * 
 * @author gjh1
 */
public class Preferences {
    public static final String LOAD_NON_VISUAL_PLUGINS_PREFIX = "LoadNonVisual-";
    private static final String PREFS_FILE = NetworkMonitor.NAME + ".properties";
    private Properties prefs;
    private File prefsFile;

    /**
     * @return the names of all currently defined preferences
     */
    public List getAllPreferenceNames() {
        List preferenceNames = new ArrayList();
        Enumeration e = prefs.propertyNames();
        while (e.hasMoreElements())
            preferenceNames.add(e.nextElement());
        return preferenceNames;
    }

    /**
     * Obtain the value of a preference item.
     * 
     * @param key the item name
     * @param defaultValue the value to return if the item does not currently have one or does not exist
     * @return the value of the item, or the given default
     */
    public String getPreference(String key, String defaultValue) {
        return prefs.getProperty(key, defaultValue);
    }

    /**
     * Load the preferences from permanent storage.
     */
    public void loadPreferences() {
        Diagnostic.trace("Reading preferences...", Diagnostic.FULL);
        // Open the file
        try {
            prefsFile = new File(PREFS_FILE);
            prefsFile.createNewFile();
            FileInputStream fis = new FileInputStream(PREFS_FILE);
            prefs = new Properties();
            prefs.load(fis);
            Diagnostic.trace(prefs.toString(), Diagnostic.FULL);
        }
        catch (FileNotFoundException e) {
            // Ignore this since the file will be created if it does not exist
        }
        catch (IOException e) {
            Error.hardExceptionError("Could not load preferences.", e);
        }
    }

    /**
     * Save the preferences to permanent storage.
     */
    public void savePreferences() {
        Diagnostic.trace("Writing preferences...", Diagnostic.FULL);
        // Open the file
        try {
            FileOutputStream fos = new FileOutputStream(prefsFile);
            prefs.store(fos, NetworkMonitor.NAME + " Preferences");
        }
        catch (FileNotFoundException e) {
            // Ignore this since the file will be created if it does not exist
        }
        catch (IOException e) {
            Error.hardExceptionError("Could not save preferences.", e);
        }
    }

    /**
     * Assign a value to a preference item.
     * 
     * @param key the item name
     * @param value the value to assign
     * @return the current value, or null if none
     */
    public Object setPreference(String key, String value) {
        return prefs.setProperty(key, value);
    }
}
