package org.basex.gui.dialog;

import static org.basex.Text.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import org.basex.core.Prop;
import org.basex.core.proc.List;
import org.basex.gui.GUIConstants;
import org.basex.gui.GUIProp;
import org.basex.gui.layout.BaseXBack;
import org.basex.gui.layout.BaseXButton;
import org.basex.gui.layout.BaseXCheckBox;
import org.basex.gui.layout.BaseXFileChooser;
import org.basex.gui.layout.BaseXLabel;
import org.basex.gui.layout.BaseXLayout;
import org.basex.gui.layout.BaseXTextField;
import org.basex.gui.layout.TableLayout;
import org.basex.io.IO;
import org.basex.util.StringList;
import org.basex.util.Token;

/**
 * Dialog window for specifying options for creating a new database.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class DialogCreate extends Dialog {
  /** Database Input. */
  private final BaseXTextField input;
  /** Database Input. */
  private final BaseXTextField filter;
  /** Input info. */
  private final BaseXLabel info1;
  /** Database name. */
  private final BaseXTextField dbname;
  /** Database info. */
  private final BaseXLabel info2;
  
  /** Internal XML parsing. */
  private final BaseXCheckBox intparse;
  /** Whitespace chopping. */
  private final BaseXCheckBox chop;
  /** Entities mode. */
  private final BaseXCheckBox entities;
  /** DTD mode. */
  private final BaseXCheckBox dtd;
  /** Indexing mode. */
  private final BaseXCheckBox txtindex;
  /** Indexing mode. */
  private final BaseXCheckBox atvindex;
  /** Word Indexing mode. */
  private final BaseXCheckBox ftxindex;
  /** Fulltext indexing. */
  private final BaseXCheckBox[] ft = new BaseXCheckBox[4];
  /** Buttons. */
  private final BaseXBack buttons;
  /** Available databases. */
  private final StringList db = List.list();

  /**
   * Default Constructor.
   * @param parent parent frame
   */
  public DialogCreate(final JFrame parent) {
    super(parent, CREATEADVTITLE);

    // create checkboxes
    final BaseXBack p1 = new BaseXBack();
    p1.setLayout(new TableLayout(7, 1));
    p1.setBorder(8, 8, 8, 8);
 
    final BaseXBack p = new BaseXBack();
    p.setLayout(new TableLayout(2, 3, 6, 0));

    input = new BaseXTextField(GUIProp.createpath, null, this);
    input.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(final KeyEvent e) { action(null); }
    });
    BaseXLayout.setWidth(input, 240);

    filter = new BaseXTextField(Prop.createfilter, null, this);
    input.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(final KeyEvent e) { action(null); }
    });
    BaseXLayout.setWidth(filter, 54);

    final BaseXButton button = new BaseXButton(BUTTONBROWSE, HELPBROWSE, this);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        choose(parent);
      }
    });

    p.add(new BaseXLabel(CREATETITLE + ":"));
    p.add(new BaseXLabel(CREATEFILTER + ":"));
    p.add(new BaseXLabel(""));
    p.add(input);
    p.add(filter);
    p.add(button);
    p1.add(p);
    
    info1 = new BaseXLabel(" ", true);
    info1.setForeground(GUIConstants.COLORERROR);
    p1.add(info1);

    final BaseXLabel l = new BaseXLabel(CREATENAME);
    l.setBorder(0, 0, 0, 0);
    p1.add(l);
    dbname = new BaseXTextField(null, this);
    dbname.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(final KeyEvent e) {
        action(null);
      }
    });
    BaseXLayout.setWidth(dbname, 300);
    p1.add(dbname);

    info2 = new BaseXLabel(" ", true);
    info2.setForeground(GUIConstants.COLORERROR);
    p1.add(info2);

    // create checkboxes
    final BaseXBack p2 = new BaseXBack();
    p2.setLayout(new TableLayout(9, 1));
    p2.setBorder(8, 8, 8, 8);

    intparse = new BaseXCheckBox(CREATEINTPARSE + COL,
        Token.token(INTPARSEINFO), Prop.intparse, 0, this);
    p2.add(intparse);
    p2.add(new BaseXLabel(INTPARSEINFO, 8));

    entities = new BaseXCheckBox(CREATEENTITIES,
        Token.token(ENTITIESINFO), Prop.entity, 0, this);
    p2.add(entities);

    dtd = new BaseXCheckBox(CREATEDTD,
        Token.token(DTDINFO), Prop.entity, 12, this);
    p2.add(dtd);

    chop = new BaseXCheckBox(CREATECHOP,
        Token.token(CHOPPINGINFO), Prop.chop, 0, this);
    p2.add(chop);
    p2.add(new BaseXLabel(CHOPPINGINFO, 16));

    // create checkboxes
    final BaseXBack p3 = new BaseXBack();
    p3.setLayout(new TableLayout(10, 1, 0, 0));
    p3.setBorder(8, 8, 8, 8);

    txtindex = new BaseXCheckBox(INFOTEXTINDEX,
        Token.token(TXTINDEXINFO), Prop.textindex, 0, this);
    p3.add(txtindex);
    p3.add(new BaseXLabel(TXTINDEXINFO, 8));

    atvindex = new BaseXCheckBox(INFOATTRINDEX,
        Token.token(ATTINDEXINFO), Prop.attrindex, 0, this);
    p3.add(atvindex);
    p3.add(new BaseXLabel(ATTINDEXINFO, 8));

    // create checkboxes
    final BaseXBack p4 = new BaseXBack();
    p4.setLayout(new TableLayout(10, 1, 0, 0));
    p4.setBorder(8, 8, 8, 8);

    ftxindex = new BaseXCheckBox(INFOFTINDEX + COL,
        Token.token(FTINDEXINFO), Prop.ftindex, 0, this);
    p4.add(ftxindex);
    p4.add(new BaseXLabel(FTINDEXINFO, 8));

    final String[] cb = { CREATEFZ, CREATESTEM, CREATEDC, CREATECS };
    final String[] desc = { FZINDEXINFO, FTSTEMINFO, FTDCINFO, FTCSINFO };
    final boolean[] val = { Prop.ftfuzzy, Prop.ftstem, Prop.ftdc, Prop.ftcs };
    for(int f = 0; f < ft.length; f++) {
      ft[f] = new BaseXCheckBox(cb[f], Token.token(desc[f]), val[f], 0, this);
      p4.add(ft[f]);
    }

    final JTabbedPane tabs = new JTabbedPane();
    BaseXLayout.addDefaultKeys(tabs, this);
    tabs.addTab(GENERALINFO, p1);
    tabs.addTab(PARSEINFO, p2);
    tabs.addTab(INDEXINFO, p3);
    tabs.addTab(FTINFO, p4);

    set(tabs, BorderLayout.CENTER);

    // create buttons
    buttons = BaseXLayout.okCancel(this);
    set(buttons, BorderLayout.SOUTH);
    action(null);

    finish(parent);
  }

  /**
   * Choose an XML document or directory.
   * @param parent parent reference
   */
  public void choose(final JFrame parent) {
    final BaseXFileChooser fc = new BaseXFileChooser(CREATETITLE,
        GUIProp.createpath, parent);
    fc.addFilter(IO.GZSUFFIX, CREATEGZDESC);
    fc.addFilter(IO.ZIPSUFFIX, CREATEZIPDESC);
    fc.addFilter(IO.XMLSUFFIX, CREATEXMLDESC);

    if(fc.select(BaseXFileChooser.MODE.OPENDIR)) {
      final IO file = fc.getFile();
      input.setText(file.path());
      dbname.setText(file.dbname());
      //final int c = count(file);
      //count.setText(CREATETITLE +
      //    " (" + (c >= MAX ? "> " + MAX : c) + " files):");
    }
    GUIProp.createpath = fc.getDir();
  }

  /**
   * Returns the chosen XML file or directory.
   * @return file or directory
   */
  public String input() {
    return input.getText().trim();
  }

  /**
   * Returns the database name.
   * @return file or directory
   */
  public String dbname() {
    return dbname.getText().trim();
  }

  @Override
  public void action(final String cmd) {
    final boolean ftx = ftxindex.isSelected();
    for(int f = 0; f < ft.length; f++) ft[f].setEnabled(ftx);

    entities.setEnabled(intparse.isSelected());
    dtd.setEnabled(intparse.isSelected());
    
    final String path = input();
    final IO file = IO.get(path);
    final boolean exists = path.length() != 0 && file.exists();
    if(exists) GUIProp.createpath = file.getDir();
    filter.setEnabled(exists && file.isDir());

    final String nm = dbname();
    dbname.setEnabled(exists);
    ok = exists && nm.length() != 0;
    
    final String inf1 = !exists ? PATHWHICH : " ";
    String inf2 = "";
    if(ok) {
      ok = IO.valid(nm);
      if(!ok) inf2 = RENAMEINVALID;
      else if(db.contains(nm)) inf2 = RENAMEOVER;
    }
    info1.setText(inf1);
    info2.setText(inf2);
    BaseXLayout.enableOK(buttons, ok);
  }

  @Override
  public void close() {
    if(!ok) return;
    super.close();
    Prop.chop  = chop.isSelected();
    Prop.createfilter = filter.getText();
    Prop.entity   = entities.isSelected();
    Prop.dtd = dtd.isSelected();
    Prop.textindex = txtindex.isSelected();
    Prop.attrindex = atvindex.isSelected();
    Prop.ftindex = ftxindex.isSelected();
    Prop.intparse = intparse.isSelected();
    Prop.ftfuzzy = ft[0].isSelected();
    Prop.ftstem = ft[1].isSelected();
    Prop.ftdc = ft[2].isSelected();
    Prop.ftcs = ft[3].isSelected();
  }
}
