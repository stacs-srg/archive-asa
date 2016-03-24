/*
 * Created on Apr 14, 2005 at 10:21:29 AM.
 */
package uk.ac.stand.dcs.asa.jchord.deploy;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Support for externalised strings.
 *
 * @author graham
 */
public class StringConstants {
    
    private static final String BUNDLE_NAME = "uk.ac.stand.dcs.asa.jchord.deploy.strings";//$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String getString(String key) {

        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
