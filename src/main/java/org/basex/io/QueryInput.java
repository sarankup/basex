package org.basex.io;

import org.basex.data.*;

/**
 * This class references input passed on in a query.
 *
 * @author BaseX Team 2005-12, BSD License
 * @author Christian Gruen
 */
public final class QueryInput {
  /** Original input string. */
  public final String original;
  /** Input reference. */
  public final IO input;
  /** Optional database path. */
  public String path = "";
  /** Optional database name. */
  public String db;

  /**
   * Constructor.
   * @param in input path
   */
  public QueryInput(final String in) {
    original = in;
    input = IO.get(in);

    // checks if the specified input reference is a valid database name
    if(MetaData.validName(in, false)) {
      db = in;
    } else {
      final int s = in.indexOf('/');
      if(s > 0 && in.indexOf(':') == -1) {
        final String n = in.substring(0, s);
        if(MetaData.validName(n, false)) {
          path = in.substring(s + 1);
          db = n;
        }
      }
    }
  }

  @Override
  public String toString() {
    return original;
  }
}
