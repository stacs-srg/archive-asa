/*
 * Created on 23-Nov-2004 at 16:58:37.
 */
package uk.ac.stand.dcs.asa.jchord.impl;

import uk.ac.stand.dcs.asa.util.Error;

import java.util.Iterator;

/**
 * @author al
 */
public class JChordNodeIterator implements Iterator {

    public JChordNodeIterator() {
        super();
    }

    public void remove() {
        Error.hardError("unimplemented method");
    }

    public boolean hasNext() {
        Error.hardError("unimplemented method");
        return false;
    }

    public Object next() {
        Error.hardError("unimplemented method");
        return null;
    }
}
