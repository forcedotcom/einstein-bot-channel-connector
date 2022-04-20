/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_CONNECTED_APP_ID;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_CONNECTED_APP_SECRET;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_INTEGRATION_NAME;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_LOGIN_ENDPOINT;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_PRIVATE_KEY;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_REDIS_URL;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_RUNTIME_URL;
import static com.salesforce.einsteinbot.connector.framework.TestConfigProperties.TEST_USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;

@AutoConfigureWebClient
@SpringBootTest(
    classes = EinsteinBotAutoConfiguration.class,
    properties = {
        "sfdc.einstein.bots.cache.redis-url=" + TEST_REDIS_URL,
        "sfdc.einstein.bots.runtime-url=" + TEST_RUNTIME_URL,
        "sfdc.einstein.bots.oauth.private-key-file=" + TEST_PRIVATE_KEY,
        "sfdc.einstein.bots.oauth.login-endpoint=" + TEST_LOGIN_ENDPOINT,
        "sfdc.einstein.bots.oauth.connected-app-id=" + TEST_CONNECTED_APP_ID,
        "sfdc.einstein.bots.oauth.connected-app-secret=" + TEST_CONNECTED_APP_SECRET,
        "sfdc.einstein.bots.oauth.user-id=" + TEST_USER_ID,
        "sfdc.einstein.bots.oauth.cache.redis-url=" + TEST_REDIS_URL
    }
)
public class EinsteinBotConfigurationTest {

  static {
    System.setProperty("sfdc.einstein.bots.integration-name", TEST_INTEGRATION_NAME);
  }

  @Autowired
  private EinsteinBotConfiguration einsteinBotConfiguration;

  @Test
  public void testChatbotConfigurationValues() {

    assertEquals(TEST_RUNTIME_URL, einsteinBotConfiguration.getRuntimeUrl());
    assertEquals(TEST_INTEGRATION_NAME, einsteinBotConfiguration
        .getIntegrationName()); //Verify property can be overridden by system properties

    EinsteinBotConfiguration.Cache cache = einsteinBotConfiguration.getCache();
    assertNotNull(cache);
    assertEquals(TEST_REDIS_URL, cache.getRedisUrl());
    assertEquals(259140, cache.getTtlSeconds()); //Verify default value is used

    EinsteinBotConfiguration.OAuth oAuth = einsteinBotConfiguration.getoAuth();
    assertNotNull(oAuth);
    assertEquals(TEST_LOGIN_ENDPOINT, oAuth.getLoginEndpoint());
    assertEquals(TEST_CONNECTED_APP_ID, oAuth.getConnectedAppId());
    assertEquals(TEST_CONNECTED_APP_SECRET, oAuth.getConnectedAppSecret());
    assertEquals(TEST_PRIVATE_KEY, oAuth.getPrivateKeyFile());
    assertEquals(TEST_USER_ID, oAuth.getUserId());

    EinsteinBotConfiguration.Cache oAuthCache = einsteinBotConfiguration.getoAuth().getCache();
    assertNotNull(oAuthCache);
    assertEquals(TEST_REDIS_URL, oAuthCache.getRedisUrl());
    assertNull(oAuthCache.getTtlSeconds());

    System.out.println("RAJA -> " + oAuth.getCache());

  }

}
