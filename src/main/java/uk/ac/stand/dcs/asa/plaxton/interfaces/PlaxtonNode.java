/*
 * Created on Jan 10, 2005 at 5:02:24 PM.
 */
package uk.ac.stand.dcs.asa.plaxton.interfaces;

/**
 * @author al
 *
 * Insert comment explaining purpose of class here.
 */
public interface PlaxtonNode extends PlaxtonRemote {
    public abstract void join(PlaxtonRemote knownNode);
    public abstract void setFailed(boolean failed);
}
