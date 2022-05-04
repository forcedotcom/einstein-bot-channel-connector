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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.salesforce.einsteinbot.sdk.auth.AuthMechanism;
import com.salesforce.einsteinbot.sdk.auth.JwtBearerOAuth;
import com.salesforce.einsteinbot.sdk.cache.Cache;
import com.salesforce.einsteinbot.sdk.cache.RedisCache;
import com.salesforce.einsteinbot.sdk.client.BasicChatbotClient;
import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
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
        "sfdc.einstein.bots.integration-name=" + TEST_INTEGRATION_NAME,
        "sfdc.einstein.bots.oauth.private-key-file=" + TEST_PRIVATE_KEY,
        "sfdc.einstein.bots.oauth.login-endpoint=" + TEST_LOGIN_ENDPOINT,
        "sfdc.einstein.bots.oauth.connected-app-id=" + TEST_CONNECTED_APP_ID,
        "sfdc.einstein.bots.oauth.connected-app-secret=" + TEST_CONNECTED_APP_SECRET,
        "sfdc.einstein.bots.oauth.user-id=" + TEST_USER_ID
    }
)
public class EinsteinBotAutoConfigurationTest {

  @Autowired
  private BasicChatbotClient basicChatbotClient;

  @Autowired
  private SessionManagedChatbotClient sessionManagedChatbotClient;

  @Autowired
  private Cache cache;

  @Autowired
  private AuthMechanism auth;

  @Test
  public void testChatbotClientAutowiring() {
    assertNotNull(basicChatbotClient);
    assertNotNull(sessionManagedChatbotClient);
    assertTrue(cache instanceof RedisCache);
    assertTrue(auth instanceof JwtBearerOAuth);
  }
}
