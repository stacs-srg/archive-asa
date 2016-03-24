/*
 * Created on 18-Jul-2005
 */
package uk.ac.stand.dcs.asa.storage.store.impl.distributed.test;

import uk.ac.stand.dcs.asa.applicationFramework.impl.KeyStringStruct;
import uk.ac.stand.dcs.asa.interfaces.IKey;
import uk.ac.stand.dcs.asa.interfaces.IPID;
import uk.ac.stand.dcs.asa.storage.absfilesystem.impl.general.StringData;
import uk.ac.stand.dcs.asa.storage.persistence.interfaces.IData;
import uk.ac.stand.dcs.asa.storage.store.exceptions.StoreGetException;
import uk.ac.stand.dcs.asa.storage.store.impl.distributed.impl.StoreComponent;
import uk.ac.stand.dcs.asa.util.Error;
import uk.ac.stand.dcs.asa.util.SHA1KeyFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class StoreUserInterface extends JPanel {

    /* SET GUI FONT */
    private int fontSize = 9;

    private Font font = new Font("Verdana", Font.PLAIN, fontSize);

    /* SET GUI COLORS */
    private Color bgColor = new Color(255, 255, 255);

    private Color fgColor = new Color(0, 0, 0);

    private Color highlightColor = new Color(212, 208, 200);

    private JLabel dataLabel = new JLabel("Data to store:");

    private JScrollPane dataTextScroll;

    private JTextArea dataTxt = new JTextArea();

    private JButton storeData = new JButton("Store Data");

    private JButton storeDataSpecKey = new JButton(
            "Store Data with Specified Key");

    private JButton hashData = new JButton("Make Key from Hash of Data");

    private JLabel retrievedLabel = new JLabel("Retrieved data:");

    private JScrollPane retrievedScroll;

    private JTextArea retrievedText = new JTextArea();

    private JLabel keyLabel = new JLabel("Key:");

    private JTextArea keyDisplay = new JTextArea();

    private JLabel keyGenLabel = new JLabel("Key constructed from:");

    private JTextArea keyGenDisplay = new JTextArea();

    private JScrollPane keyGenDisplayScroller = new JScrollPane(keyGenDisplay);

    private JButton lookup = new JButton("Lookup");

    private JLabel keyConstructionLabel = new JLabel(
            "Text for key construction:");

    private JTextArea keyConstructionTxt = new JTextArea();

    private JButton makeKey = new JButton("Make Key from Text");

    private JButton hashTxt = new JButton("Make Key from Hash of Text");

    private JList keyList = new JList(new DefaultListModel());

    private JScrollPane keyListScroller = new JScrollPane(keyList);

    private JList storedKeyList = new JList(new DefaultListModel());

    private JScrollPane storedKeyListScroller = new JScrollPane(storedKeyList);

    private IKey currentKey = null;

    private JLabel consoleLabel = new JLabel("Console:");

    private JTextArea consoleTxt = new JTextArea();

    private JScrollPane consoleScroller = new JScrollPane(consoleTxt);

    private boolean activated = false;

    private StoreComponent storeComponent;

    public StoreUserInterface(StoreComponent storeComponent) {
        this.storeComponent = storeComponent;
        this.setSize(new Dimension(700, 400));
        this.setLayout(new java.awt.GridBagLayout());
        initialise();
    }

    public void enableControls() {
        activated = true;
        if (currentKey != null)
            lookup.setEnabled(true);
        storeData.setEnabled(true);
        // storeDataSpecKey.setEnabled(true);
    }

    private void initialise() {
        dataLabel.setFont(font);
        dataLabel.setForeground(fgColor);

        dataTxt.setBackground(bgColor);
        dataTxt.setFont(font);
        dataTxt.setSelectionColor(highlightColor);

        dataTextScroll = new JScrollPane(dataTxt);
        dataTextScroll.setPreferredSize(new Dimension(500, 200));
        dataTextScroll.setMinimumSize(new Dimension(500, 200));
        dataTextScroll.setMaximumSize(new Dimension(500, 200));

        // keys and stuff
        keyLabel.setFont(font);
        keyLabel.setForeground(fgColor);

        keyDisplay.setPreferredSize(new Dimension(200, 35));
        keyDisplay.setMinimumSize(new Dimension(200, 35));
        keyDisplay.setMaximumSize(new Dimension(200, 35));
        keyDisplay.setFont(font);
        keyDisplay.setEditable(false);
        keyDisplay.setLineWrap(true);
        keyDisplay.setBackground(Color.PINK);

        keyGenLabel.setFont(font);
        keyGenLabel.setForeground(fgColor);

        keyGenDisplayScroller.setPreferredSize(new Dimension(200, 75));
        keyGenDisplayScroller.setMinimumSize(new Dimension(200, 75));
        keyGenDisplayScroller.setMaximumSize(new Dimension(200, 75));

        keyGenDisplay.setFont(font);
        keyGenDisplay.setEditable(false);
        keyGenDisplay.setLineWrap(true);
        keyGenDisplay.setBackground(Color.PINK);

        keyConstructionLabel.setFont(font);
        keyConstructionLabel.setForeground(fgColor);

        keyConstructionTxt.setPreferredSize(new Dimension(200, 35));
        keyConstructionTxt.setMinimumSize(new Dimension(200, 35));
        keyConstructionTxt.setMaximumSize(new Dimension(200, 35));
        keyConstructionTxt.setFont(font);
        keyConstructionTxt.setLineWrap(true);

        consoleLabel.setFont(font);
        consoleLabel.setForeground(fgColor);

        consoleTxt.setFont(font);
        consoleTxt.setEditable(false);
        consoleTxt.setLineWrap(true);

        consoleScroller.setPreferredSize(new Dimension(700, 100));
        consoleScroller.setMinimumSize(new Dimension(700, 100));
        consoleScroller.setMaximumSize(new Dimension(700, 100));

        makeKey.setFont(font);
        makeKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String s = keyConstructionTxt.getText();
                if (s.compareTo("") != 0) {
                    DefaultListModel dlm = (DefaultListModel) keyList
                            .getModel();
                    KeyStringStruct kss = defaultListModel_contains(keyList, s);
                    if (kss == null) {
                        try {
                            IKey k = SHA1KeyFactory.recreateKey(s);
                            kss = new KeyStringStruct(s, k);
                            dlm.add(0, kss);
                            keyList.clearSelection();
                        } catch (NumberFormatException e) {
                            consoleTxt
                                    .setText("\""
                                            + s
                                            + "\" is not valid. Use of the \"Make Key\" button requires that you enter a hexidecimal integer representation in the text box.");
                            return;
                        }
                    }
                    updateCurrentKey(kss);
                }
                keyConstructionTxt.setText("");
                consoleTxt.setText("");
            }
        });

        hashTxt.setFont(font);
        hashTxt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                addHashedStringKey(keyConstructionTxt.getText(), keyList);
                keyConstructionTxt.setText("");
            }
        });

        storeData.setFont(font);
        storeData.setEnabled(false);
        storeData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String s = dataTxt.getText();
                if (s.compareTo("") != 0) {
                    storeString(null, s);
                }
            }
        });

        storeDataSpecKey.setFont(font);
        storeDataSpecKey.setEnabled(false);
        storeDataSpecKey.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String s = dataTxt.getText();
                if (s.compareTo("") != 0) {
                    storeString(currentKey, s);
                }
            }
        });

        hashData.setFont(font);
        // hashData.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent arg0) {
        // addHashedStringKey(dataTxt.getText(),keyList);
        // }
        // });

        // RECEIVED MESSAGE TEXT AREA PROPERTIES
        retrievedLabel.setFont(font);
        retrievedLabel.setForeground(fgColor);

        retrievedText.setBackground(bgColor);
        retrievedText.setFont(font);
        retrievedText.setSelectionColor(highlightColor);

        retrievedText.setEditable(false);
        retrievedScroll = new JScrollPane(retrievedText);
        retrievedScroll.setPreferredSize(new Dimension(500, 200));
        retrievedScroll.setMinimumSize(new Dimension(500, 200));
        retrievedScroll.setMaximumSize(new Dimension(500, 200));

        lookup.setFont(font);
        lookup.setEnabled(false);
        lookup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (currentKey != null)
                    getString(currentKey);
            }
        });

        keyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        keyList.setLayoutOrientation(JList.VERTICAL);

        keyList.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                ListModel lm = keyList.getModel();
                if (e.getClickCount() == 2) {
                    int index = keyList.locationToIndex(e.getPoint());
                    listModelSelection(lm, index);
                }
            }

            public void mouseEntered(MouseEvent arg0) {
                // This method intentionally left blank.
            }

            public void mouseExited(MouseEvent arg0) {
                // This method intentionally left blank.
            }

            public void mousePressed(MouseEvent arg0) {
                // This method intentionally left blank.
            }

            public void mouseReleased(MouseEvent arg0) {
                // This method intentionally left blank.
            }
        });

        keyList.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent arg0) {
                if (!keyList.isSelectionEmpty()) {
                    // System.out.println("DELETE");
                    if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
                        int index = keyList.getSelectedIndex();
                        DefaultListModel dlm = (DefaultListModel) keyList
                                .getModel();
                        if (index >= 0 && index < dlm.getSize()) {
                            dlm.remove(index);
                            keyList.clearSelection();
                        }
                    }
                }
            }

            public void keyReleased(KeyEvent arg0) {
                // This method intentionally left blank.
            }

            public void keyTyped(KeyEvent arg0) {
                // This method intentionally left blank.
            }
        });

        keyList.setFont(font);
        keyListScroller.setPreferredSize(new java.awt.Dimension(200, 80));
        keyListScroller.setMinimumSize(new java.awt.Dimension(200, 80));
        keyListScroller.setMaximumSize(new java.awt.Dimension(200, 80));

        storedKeyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        storedKeyList.setLayoutOrientation(JList.VERTICAL);

        storedKeyList.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                ListModel lm = storedKeyList.getModel();
                if (e.getClickCount() == 2) {
                    int index = storedKeyList.locationToIndex(e.getPoint());
                    listModelSelection(lm, index);
                }
            }

            public void mouseEntered(MouseEvent arg0) {
                // This method intentionally left blank.

            }

            public void mouseExited(MouseEvent arg0) {
                // This method intentionally left blank.

            }

            public void mousePressed(MouseEvent arg0) {
                // This method intentionally left blank.

            }

            public void mouseReleased(MouseEvent arg0) {
                // This method intentionally left blank.

            }
        });

        storedKeyList.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent arg0) {
                if (!storedKeyList.isSelectionEmpty()) {
                    // System.out.println("DELETE");
                    if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
                        int index = storedKeyList.getSelectedIndex();
                        DefaultListModel dlm = (DefaultListModel) storedKeyList
                                .getModel();
                        if (index >= 0 && index < dlm.getSize()) {
                            dlm.remove(index);
                            storedKeyList.clearSelection();
                        }
                    }
                }
            }

            public void keyReleased(KeyEvent arg0) {
                // This method intentionally left blank.

            }

            public void keyTyped(KeyEvent arg0) {
                // This method intentionally left blank.

            }
        });

        storedKeyList.setFont(font);
        storedKeyListScroller.setPreferredSize(new java.awt.Dimension(200, 80));
        storedKeyListScroller.setMinimumSize(new java.awt.Dimension(200, 80));
        storedKeyListScroller.setMaximumSize(new java.awt.Dimension(200, 80));

        this.add(dataLabel, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(dataTextScroll, new GridBagConstraints(0, 1, 1, 4, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(storeData, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(storeDataSpecKey, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(hashData, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(storedKeyListScroller, new GridBagConstraints(1, 4, 1, 1, 1.0,
                0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(retrievedLabel, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(retrievedScroll, new GridBagConstraints(0, 6, 1, 4, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(keyLabel, new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(keyDisplay, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(lookup, new GridBagConstraints(1, 8, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(keyListScroller, new GridBagConstraints(1, 9, 1, 1, 1.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(keyGenLabel, new GridBagConstraints(1, 10, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this
                .add(keyGenDisplayScroller, new GridBagConstraints(1, 11, 1, 3,
                        0.0, 0.0, GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(keyConstructionLabel, new GridBagConstraints(0, 10, 1, 2, 0.0,
                0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));

        this.add(keyConstructionTxt, new GridBagConstraints(0, 11, 1, 1, 0.0,
                0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(makeKey, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(hashTxt, new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(consoleLabel, new GridBagConstraints(0, 14, 2, 1, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 0, 0), 0, 0));
        this.add(consoleScroller, new GridBagConstraints(0, 15, 2, 1, 0.0, 1.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new java.awt.Insets(5, 5, 5, 5), 0, 0));

    }

    protected void storeString(IKey k, String s) {
        // System.out.println("Store: \""+s);
        try {
            IKey storedKey = storeComponent.put(new StringData(s));
            if (storedKey == null) {
                String output = "Failed to store string:\n" + s
                        + "\nwith unspecified key.";
                output2console(output);
            } else {
                String output = "Stored string:\n" + s + "\nwith key: "
                        + storedKey;
                output2console(output);
                if (k == null)
                    addKey(storedKey.toString(), storedKey, storedKeyList);
            }
        } catch (Exception e) {
            String output = "Failed to store string:\n" + s + "\nwith ";
            output += k == null ? "unspecified key." : "specified key " + k;
            output2console(output + "\nError was:\n" + e.getLocalizedMessage());
        }
    }

    protected void getString(IKey k) {
        // System.out.println("Lookup key: "+k);
        // try {
        IData data;
        try {
            data = storeComponent.get((IPID) k);
            if (data != null) {
                String s = new String(data.getState());
                output2recieved(s.toString());
            } else {
                output2recieved(null);
            }
        } catch (StoreGetException e) {
            output2console("Failed to retrieve data with key ("+k+"). This may be a transient error. Please try again.");
        }
        // } catch (P2PApplicationException e) {
        // String output = "Failed to lookup key "+k;
        // output2console(output+"\nError was:\n"+e.getLocalizedMessage());
        // } catch (ClassCastException e1) {
        // output2console("Retrieved object was not a string.");
        // }
    }

    private void output2recieved(String string) {
        timestampFormatOutput(string, retrievedText);
    }

    private void output2console(String string) {
        timestampFormatOutput(string, consoleTxt);
    }

    private void timestampFormatOutput(String string, JTextArea textArea) {
        Date today;
        String output;
        SimpleDateFormat formatter;

        formatter = new SimpleDateFormat("hh:mm:ss:SSS z - dd.MM.yy");
        today = new Date();
        output = formatter.format(today);

        textArea.append("[" + output + "]:\n\"");
        textArea.append(string);
        textArea.append("\"\n");
    }

    private void addHashedStringKey(String s, JList list) {
        IKey k = SHA1KeyFactory.generateKey(s);
        addKey(wrapString(s), k, list);
    }

    private void addKey(String s, IKey k, JList list) {
        if (s.compareTo("") != 0) {
            DefaultListModel dlm = (DefaultListModel) list.getModel();
            KeyStringStruct kss = defaultListModel_contains(list, s);
            if (kss == null) {
                kss = new KeyStringStruct(s, k);
                dlm.add(0, kss);
                list.clearSelection();
            }
            updateCurrentKey(kss);
        }

    }

    private String wrapString(String s) {
        return "hash(\"" + s + "\")";
    }

    private void listModelSelection(ListModel lm, int index) {
        if (index >= 0 && index < lm.getSize()) {
            KeyStringStruct kss = (KeyStringStruct) lm.getElementAt(index);
            storedKeyList.ensureIndexIsVisible(index);
            updateCurrentKey(kss);
            storedKeyList.clearSelection();
            lookup.setEnabled(true);
        }
    }

    private void updateCurrentKey(KeyStringStruct kss) {
        keyGenDisplay.setText(kss.getString());
        keyDisplay.setText(kss.getKey().toString(16));
        currentKey = kss.getKey();
        if (activated) {
            lookup.setEnabled(true);
        }
    }

    private KeyStringStruct defaultListModel_contains(JList list, String s) {
        Enumeration e = ((DefaultListModel) list.getModel()).elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (o instanceof KeyStringStruct) {
                KeyStringStruct kss = (KeyStringStruct) o;
                if (s.compareTo(kss.getString()) == 0) {
                    return kss;
                }
            } else {
                Error
                        .error("DefaultListModel object contained element of wrong type.");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setSize(900, 700);
        f.getContentPane().add(new StoreUserInterface(null));
        f.show();
    }

}
