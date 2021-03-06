package org.basex.query.func;

import static org.basex.query.QueryError.*;
import static org.basex.query.QueryText.*;
import static org.basex.util.Token.*;

import org.basex.core.*;
import org.basex.io.serial.*;
import org.basex.query.*;
import org.basex.query.expr.path.*;
import org.basex.query.value.item.*;
import org.basex.query.value.map.*;
import org.basex.query.value.node.*;
import org.basex.query.value.type.*;
import org.basex.util.*;
import org.basex.util.options.*;

/**
 * This class parses options specified in function arguments.
 *
 * @author BaseX Team 2005-15, BSD License
 * @author Christian Gruen
 */
public final class FuncOptions {
  /** QName. */
  public static final QNm Q_SPARAM = QNm.get("serialization-parameters", OUTPUT_URI);
  /** Value. */
  private static final String VALUE = "value";

  /** Root element. */
  private final QNm root;
  /** Root node test. */
  private final NodeTest test;
  /** Input info. */
  private final InputInfo info;

  /**
   * Constructor.
   */
  public FuncOptions() {
    this(null, null);
  }

  /**
   * Constructor.
   * @param root name of root node (can be {@code null})
   * @param info input info
   */
  public FuncOptions(final QNm root, final InputInfo info) {
    test = root == null ? null : new NodeTest(root);
    this.root = root;
    this.info = info;
  }

  /**
   * Extracts options from the specified item.
   * @param it item to be converted
   * @param options options
   * @param <T> option type
   * @return specified options
   * @throws QueryException query exception
   */
  public <T extends Options> T parse(final Item it, final T options) throws QueryException {
    parse(it, options, INVALIDOPT_X);
    return options;
  }

  /**
   * Extracts options from the specified item.
   * @param item item to be parsed
   * @param options options
   * @param error raise error if parameter is unknown
   * @throws QueryException query exception
   */
  private void parse(final Item item, final Options options, final QueryError error)
      throws QueryException {

    if(item == null) return;

    final TokenBuilder tb = new TokenBuilder();
    try {
      if(item instanceof Map) {
        options.parse((Map) item);
      } else {
        if(test == null) {
          throw MAP_X_X.get(info, item.type, item);
        } else if(!test.eq(item)) {
          throw ELMMAP_X_X_X.get(info, root.prefixId(XML), item.type, item);
        }
        options.parse(tb.add(optString((ANode) item)).toString());
      }
    } catch(final BaseXException ex) {
      throw error.get(info, ex);
    }
  }

  /**
   * Builds a string representation of the specified node.
   * @param node node
   * @return string
   * @throws QueryException query exception
   */
  private String optString(final ANode node) throws QueryException {
    final TokenBuilder tb = new TokenBuilder();
    // interpret options
    for(final ANode child : node.children()) {
      if(child.type != NodeType.ELM) continue;
      // ignore elements in other namespace
      final QNm qn = child.qname();
      if(!eq(qn.uri(), root.uri())) continue;
      // retrieve key from element name and value from "value" attribute or text node
      byte[] v;
      if(hasElements(child)) {
        v = token(optString(child));
      } else {
        v = child.attribute(VALUE);
        if(v == null) v = child.string();
      }
      tb.add(string(qn.local())).add('=').add(string(v).replace(",", ",,")).add(',');
    }
    return tb.toString();
  }

  /**
   * Checks if the specified node has elements as children.
   * @param node node
   * @return result of check
   */
  private static boolean hasElements(final ANode node) {
    for(final ANode n : node.children()) {
      if(n.type == NodeType.ELM) return true;
    }
    return false;
  }

  /**
   * Converts the specified output parameter item to serialization parameters.
   * @param it input item
   * @param info input info
   * @return serialization parameters
   * @throws QueryException query exception
   */
  public static SerializerOptions serializer(final Item it, final InputInfo info)
      throws QueryException {
    final SerializerOptions so = new SerializerOptions();
    so.set(SerializerOptions.METHOD, SerialMethod.XML);
    return serializer(it, so, info);
  }

  /**
   * Converts the specified output parameter item to serialization parameters.
   * @param it input item
   * @param sopts serialization parameters
   * @param info input info
   * @return serialization parameters
   * @throws QueryException query exception
   */
  public static SerializerOptions serializer(final Item it, final SerializerOptions sopts,
      final InputInfo info) throws QueryException {
    new FuncOptions(Q_SPARAM, info).parse(it, sopts, SEROPT_X);
    return sopts;
  }
}
