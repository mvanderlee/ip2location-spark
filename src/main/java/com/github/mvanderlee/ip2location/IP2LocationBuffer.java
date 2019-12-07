package com.github.mvanderlee.ip2location;


/**
 * IP2LocationBuffer
 */
public class IP2LocationBuffer implements IP2LocationSource {
  byte[] buffer;
  int position;

  public IP2LocationBuffer(byte[] array) {
    this.buffer = array;
  }

  public void seek(long pos) {
    this.position = (int)pos;
  }

  public int read() {
    return buffer[position++] & 0xff;
  }
}
