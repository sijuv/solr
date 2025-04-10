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

package org.apache.solr.schema;

import static org.hamcrest.Matchers.containsStringIgnoringCase;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;
import org.apache.solr.cloud.SolrCloudTestCase;
import org.apache.solr.common.cloud.DocCollection;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchemaApiFailureTest extends SolrCloudTestCase {

  private static final String COLLECTION = "schema-api-failure";

  @BeforeClass
  public static void setupCluster() throws Exception {
    configureCluster(1).configure();
    CollectionAdminRequest.createCollection(COLLECTION, 2, 1) // _default configset
        .process(cluster.getSolrClient());
    cluster
        .getZkStateReader()
        .waitForState(
            COLLECTION,
            DEFAULT_TIMEOUT,
            TimeUnit.SECONDS,
            (n, c) -> DocCollection.isFullyActive(n, c, 2, 1));
  }

  @Test
  public void testAddTheSameFieldTwice() throws Exception {
    CloudSolrClient client = cluster.getSolrClient();
    SchemaRequest.Update fieldAddition =
        new SchemaRequest.AddField(Map.of("name", "myfield", "type", "string"));
    SchemaResponse.UpdateResponse updateResponse = fieldAddition.process(client, COLLECTION);

    final var thrown =
        expectThrows(
            SolrClient.RemoteSolrException.class,
            () -> {
              fieldAddition.process(client, COLLECTION);
            });

    assertThat(thrown.getMessage(), containsStringIgnoringCase("Field 'myfield' already exists."));
  }
}
