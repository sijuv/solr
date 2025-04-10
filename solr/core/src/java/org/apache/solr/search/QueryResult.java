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
package org.apache.solr.search;

/** The result of a search. */
public class QueryResult {

  // Object for back compatibility so that we render true not "true" in json
  private Object partialResults;
  private Object partialResultsDetails;
  private Boolean segmentTerminatedEarly;
  private Boolean terminatedEarly;
  private DocListAndSet docListAndSet;
  private CursorMark nextCursorMark;
  private Boolean maxHitsTerminatedEarly;
  private Long approximateTotalHits;

  public Object groupedResults; // TODO: currently for testing

  public DocList getDocList() {
    return docListAndSet.docList;
  }

  public void setDocList(DocList list) {
    if (docListAndSet == null) {
      docListAndSet = new DocListAndSet();
    }
    docListAndSet.docList = list;
  }

  public DocSet getDocSet() {
    return docListAndSet.docSet;
  }

  public void setDocSet(DocSet set) {
    if (docListAndSet == null) {
      docListAndSet = new DocListAndSet();
    }
    docListAndSet.docSet = set;
  }

  public boolean isPartialResults() {
    // omitted is equivalent to false/empty for java logic
    return Boolean.parseBoolean(String.valueOf(partialResults));
  }

  public boolean isPartialResultOmitted() {
    return "omitted".equals(partialResults);
  }

  public void setPartialResults(Object partialResults) {
    this.partialResults = partialResults;
  }

  public Object getPartialResultsDetails() {
    return partialResultsDetails;
  }

  public void setPartialResultsDetails(Object partialResultsDetails) {
    this.partialResultsDetails = partialResultsDetails;
  }

  public Boolean getSegmentTerminatedEarly() {
    return segmentTerminatedEarly;
  }

  public void setSegmentTerminatedEarly(Boolean segmentTerminatedEarly) {
    this.segmentTerminatedEarly = segmentTerminatedEarly;
  }

  public void setDocListAndSet(DocListAndSet listSet) {
    docListAndSet = listSet;
  }

  public DocListAndSet getDocListAndSet() {
    return docListAndSet;
  }

  public void setNextCursorMark(CursorMark next) {
    this.nextCursorMark = next;
  }

  public CursorMark getNextCursorMark() {
    return nextCursorMark;
  }

  public Boolean getTerminatedEarly() {
    return terminatedEarly;
  }

  public void setTerminatedEarly(boolean terminatedEarly) {
    this.terminatedEarly = terminatedEarly;
  }

  public Boolean getMaxHitsTerminatedEarly() {
    return maxHitsTerminatedEarly;
  }

  public void setMaxHitsTerminatedEarly(Boolean maxHitsTerminatedEarly) {
    this.maxHitsTerminatedEarly = maxHitsTerminatedEarly;
  }

  public Long getApproximateTotalHits() {
    return approximateTotalHits;
  }

  public void setApproximateTotalHits(long approximateTotalHits) {
    this.approximateTotalHits = approximateTotalHits;
  }
}
