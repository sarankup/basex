package org.basex.query.xquery.util;

import static org.basex.query.xquery.XQText.*;
import static org.basex.query.xquery.XQTokens.*;
import static org.basex.util.Token.*;
import org.basex.query.xquery.XQException;
import org.basex.query.xquery.item.QNm;
import org.basex.query.xquery.item.Uri;
import org.basex.util.Atts;

/**
 * Local Namespaces.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
public final class NSLocal {
  /** Namespaces. */
  public Atts atts = new Atts();
  /** Number of default namespaces. */
  private int def;

  /**
   * Adds the specified namespace.
   * @param name namespace
   * @throws XQException evaluation exception
   */
  public void add(final QNm name) throws XQException {
    final byte[] ln = name.ln();
    if(eq(ln, XML) || eq(ln, XMLNS)) Err.or(NSDEF, name);
    final byte[] uri = name.uri.str();
    if(eq(XMLURI, uri)) Err.or(NOXMLNS, name);
    atts.add(ln, uri);
  }

  /**
   * Deletes the specified namespace.
   * @param name namespace
   */
  public void delete(final QNm name) {
    final byte[] ln = name.ln();
    for(int s = atts.size - 1; s >= 0; s--) {
      if(eq(ln, atts.key[s])) atts.delete(s);
    }
  }
  
  /**
   * Assigns a uri to the specified QName.
   * @param qname qname
   */
  public void uri(final QNm qname) {
    final byte[] pre = qname.pre();
    if(pre.length == 0) return;
    final byte[] uri = find(pre);
    qname.uri = Uri.uri(uri != null ? uri : NSGlobal.uri(pre));
  }

  /**
   * Finds the uri for the specified prefix in the local and global namespaces.
   * @param pre prefix of the namespace
   * @return uri
   * @throws XQException evaluation exception
   */
  public byte[] uri(final byte[] pre) throws XQException {
    byte[] uri = find(pre);
    if(uri == null) uri = NSGlobal.uri(pre);
    if(uri.length == 0 && pre.length != 0) Err.or(PREUNKNOWN, pre);
    return uri;
  }

  /**
   * Finds the URI for the specified prefix.
   * @param pre prefix of the namespace
   * @return uri or null value
   */
  public byte[] find(final byte[] pre) {
    for(int s = atts.size - 1; s >= 0; s--) {
      if(eq(atts.key[s], pre)) return atts.val[s];
    }
    return null;
  }

  /**
   * Finds the specified URI and returns the prefix.
   * @param uri URI
   * @return prefix
   */
  public byte[] prefix(final byte[] uri) {
    for(int s = atts.size - 1; s >= 0; s--) {
      if(eq(atts.val[s], uri)) return atts.key[s];
    }
    return NSGlobal.prefix(uri);
  }
  
  /**
   * Finishes the creation of default namespaces.
   * @param elem default element namespace
   */
  public void finish(final byte[] elem) {
    if(elem.length != 0) atts.add(EMPTY, elem);
    def = atts.size;
  }
  
  /**
   * Creates a copy with the default namespaces.
   * @return copy
   */
  public NSLocal copy() {
    final NSLocal local = new NSLocal();
    for(int i = 0; i < def; i++) local.atts.add(atts.key[i], atts.val[i]);
    return local;
  }

  /**
   * Returns the number of namespaces.
   * @return namespaces
   */
  public int size() {
    return atts.size;
  }

  /**
   * Sets the number of namespaces.
   * @param s namespaces
   */
  public void size(final int s) {
    atts.size = s;
  }
}
