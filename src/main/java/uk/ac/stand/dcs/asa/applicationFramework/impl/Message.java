/*
 * Created on 10-May-2005
 */
package uk.ac.stand.dcs.asa.applicationFramework.impl;

import java.net.InetSocketAddress;

/**
 * @author stuart
 */
public class Message {

    public InetSocketAddress from;
    public String contents;
    
    public Message(InetSocketAddress from, String contents) {
        this.from = from;
        this.contents = contents;
    }

    public InetSocketAddress getFrom() {
        return from;
    }

    public String getContents() {
        return contents;
    }
}
