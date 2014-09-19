/*
 * Copyright (C) Red Hat, Inc.
 * http://www.redhat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.poc.chunker;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class ChunkedInputStream extends FilterInputStream implements Iterable<byte[]> {

  private int chunkSize;

  public ChunkedInputStream(InputStream inputStream, int chunkSize) {
    super(Objects.requireNonNull(inputStream, "InputStream must not be null."));
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("ChunkSize must be larger than 0.");
    }
    this.chunkSize = chunkSize;
  }

  @Override
  public Iterator<byte[]> iterator() {
    return new ChunkedInputStreamIterator(super.in, chunkSize);
  }

  private class ChunkedInputStreamIterator implements Iterator<byte[]> {

    private InputStream inputStream;
    private int chunkSize;

    private boolean hasNext;

    public ChunkedInputStreamIterator(InputStream inputStream, int chunkSize) {
      this.inputStream = inputStream;
      this.chunkSize = chunkSize;

      this.hasNext = true;
    }

    @Override
    public boolean hasNext() {
      return hasNext;
    }

    @Override
    public byte[] next() {
      byte[] chunk = new byte[chunkSize];
      int read = 0;
      try {
        read = inputStream.read(chunk, 0, chunkSize);
      } catch (IOException e) {
        throw new RuntimeException("Error reading stream.", e);
      }
      hasNext = (read == chunkSize);
      return Arrays.copyOf(chunk, read);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("remove");
    }
  }
}
