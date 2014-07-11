/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.logger;

import java.util.logging.Level;

public class WTFLevel extends Level {

  private static final long serialVersionUID = 1L;

  public static final Level STEP = new WTFLevel("STEP", 143143);

  protected WTFLevel(String name, int level) {
    super(name, level);
  }
}
