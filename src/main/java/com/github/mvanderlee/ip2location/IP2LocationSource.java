package com.github.mvanderlee.ip2location;

import java.io.IOException;
import java.io.Serializable;

/**
 * IP2LocationSource
 */
public interface IP2LocationSource extends Serializable {
  public void seek(long pos) throws IOException;
  public int read() throws IOException;
}
