/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.solr.response;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.apache.solr.common.util.ContentStreamBase;
import org.apache.solr.common.util.FastWriter;
import org.apache.solr.request.SolrQueryRequest;

/** Static utility methods relating to {@link QueryResponseWriter}s */
public final class QueryResponseWriterUtil {
  private QueryResponseWriterUtil() {
    /* static helpers only */
  }

  /**
   * Writes the response writer's result to the given output stream. This method inspects the
   * specified writer to determine if it is a {@link BinaryQueryResponseWriter} or not to delegate
   * to the appropriate method.
   *
   * @see BinaryQueryResponseWriter#write(OutputStream,SolrQueryRequest,SolrQueryResponse)
   * @see BinaryQueryResponseWriter#write(Writer,SolrQueryRequest,SolrQueryResponse)
   */
  public static void writeQueryResponse(
      OutputStream outputStream,
      QueryResponseWriter responseWriter,
      SolrQueryRequest solrRequest,
      SolrQueryResponse solrResponse,
      String contentType)
      throws IOException {

    if (responseWriter instanceof JacksonJsonWriter binWriter) {
      BufferedOutputStream bos = new BufferedOutputStream(new NonFlushingStream(outputStream));
      binWriter.write(bos, solrRequest, solrResponse);
      bos.flush();
    } else if (responseWriter instanceof BinaryQueryResponseWriter binWriter) {
      binWriter.write(outputStream, solrRequest, solrResponse);
    } else {
      OutputStream out = new NonFlushingStream(outputStream);
      Writer writer = buildWriter(out, ContentStreamBase.getCharsetFromContentType(contentType));
      responseWriter.write(writer, solrRequest, solrResponse);
      writer.flush();
    }
  }

  private static Writer buildWriter(OutputStream outputStream, String charset)
      throws UnsupportedEncodingException {
    Writer writer =
        (charset == null)
            ? new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)
            : new OutputStreamWriter(outputStream, charset);

    return new FastWriter(writer);
  }

  /**
   * Delegates write methods to an underlying {@link OutputStream}, but does not delegate {@link
   * OutputStream#flush()}, (nor {@link OutputStream#close()}). This allows code writing to this
   * stream to flush internal buffers without flushing the response. If we were to flush the
   * response early, that would trigger chunked encoding.
   *
   * <p>See SOLR-8669.
   */
  private static class NonFlushingStream extends OutputStream {
    private final OutputStream outputStream;

    public NonFlushingStream(OutputStream outputStream) {
      this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
      outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
      outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      outputStream.write(b, off, len);
    }
  }
}
