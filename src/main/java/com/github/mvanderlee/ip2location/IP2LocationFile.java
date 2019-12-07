package com.github.mvanderlee.ip2location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * RandomAccessFileBuffer
 */
public class IP2LocationFile implements IP2LocationSource {
  RandomAccessFile fileHandle;

  public IP2LocationFile(String file) throws FileNotFoundException {
    this.fileHandle = new RandomAccessFile(file, "r");
  }

  public void seek(long pos) throws IOException {
    fileHandle.seek(pos);
  }

  public int read() throws IOException {
    return fileHandle.read();
  }
}
