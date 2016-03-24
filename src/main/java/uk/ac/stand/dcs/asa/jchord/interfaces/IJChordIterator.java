
package uk.ac.stand.dcs.asa.jchord.interfaces;

import uk.ac.stand.dcs.asa.interfaces.IKey;

import java.util.Iterator;

public interface IJChordIterator {

	Iterator iterator( IKey k, IApplicationGUID aid );
}
