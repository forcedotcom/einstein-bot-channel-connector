/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import com.salesforce.einsteinbot.sdk.auth.AuthMechanism;
import com.salesforce.einsteinbot.sdk.auth.JwtBearerOAuth;
import com.salesforce.einsteinbot.sdk.cache.Cache;
import com.salesforce.einsteinbot.sdk.cache.InMemoryCache;
import com.salesforce.einsteinbot.sdk.cache.RedisCache;
import com.salesforce.einsteinbot.sdk.client.BasicChatbotClient;
import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(EinsteinBotConfiguration.class)
public class EinsteinBotAutoConfiguration {

  @Autowired
  private EinsteinBotConfiguration einsteinBotConfiguration;

  @Bean
  @ConditionalOnMissingBean
  public Cache getCache() {
    EinsteinBotConfiguration.Cache cache = einsteinBotConfiguration.getCache();
    if (cache.getRedisUrl() != null) {
      return new RedisCache(cache.getTtlSeconds(), cache.getRedisUrl());
    } else {
      return new InMemoryCache(cache.getTtlSeconds());
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public AuthMechanism getOAuth(Cache cache) {
    EinsteinBotConfiguration.OAuth oauthConfig = einsteinBotConfiguration.getoAuth();
    return JwtBearerOAuth.with()
        .privateKeyFilePath(oauthConfig.getPrivateKeyFile())
        .loginEndpoint(oauthConfig.getLoginEndpoint())
        .connectedAppId(oauthConfig.getConnectedAppId())
        .connectedAppSecret(oauthConfig.getConnectedAppSecret())
        .userId(oauthConfig.getUserId())
        .cache(cache)
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public ChatbotClient getChatbotClient(AuthMechanism auth, Cache cache,
      WebClient.Builder wcBuilder) {
    return SessionManagedChatbotClient.builder().basicClient(BasicChatbotClient.builder()
        .basePath(einsteinBotConfiguration.getRuntimeUrl())
        .authMechanism(auth)
        .webClientBuilder(wcBuilder)
        .build())
        .cache(cache)
        .integrationName(einsteinBotConfiguration.getIntegrationName())
        .build();
  }
}
