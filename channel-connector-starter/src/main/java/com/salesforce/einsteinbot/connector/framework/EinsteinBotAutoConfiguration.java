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
import com.salesforce.einsteinbot.sdk.client.ChatbotClients;
import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import java.util.Optional;
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
    return createCacheFromCacheConfig(einsteinBotConfiguration.getCache());
  }

  private Optional<Cache> getCacheForOAuth(EinsteinBotConfiguration.Cache cacheConfig) {
    if (cacheConfig != null && cacheConfig.getTtlSeconds() != null){
      return Optional.of(createCacheFromCacheConfig(cacheConfig));
    }else{
      return Optional.empty();
    }
  }

  private Cache createCacheFromCacheConfig(EinsteinBotConfiguration.Cache cacheConfig) {
    if (cacheConfig.getRedisUrl() != null) {
      return new RedisCache(cacheConfig.getTtlSeconds(), cacheConfig.getRedisUrl());
    } else {
      return new InMemoryCache(cacheConfig.getTtlSeconds());
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public AuthMechanism getOAuth() {
    EinsteinBotConfiguration.OAuth oauthConfig = einsteinBotConfiguration.getoAuth();
    return JwtBearerOAuth.with()
        .privateKeyFilePath(oauthConfig.getPrivateKeyFile())
        .loginEndpoint(oauthConfig.getLoginEndpoint())
        .connectedAppId(oauthConfig.getConnectedAppId())
        .connectedAppSecret(oauthConfig.getConnectedAppSecret())
        .userId(oauthConfig.getUserId())
        .cache(getCacheForOAuth(oauthConfig.getCache()))
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public BasicChatbotClient getChatbotClient(AuthMechanism auth,
      WebClient.Builder wcBuilder) {

    return ChatbotClients.basic()
        .basePath(einsteinBotConfiguration.getRuntimeUrl())
        .authMechanism(auth)
        .webClientBuilder(wcBuilder).build();

  }

  @Bean
  @ConditionalOnMissingBean
  public SessionManagedChatbotClient getSessionManagedChatbotClient(BasicChatbotClient basicChatbotClient, Cache cache) {

    return ChatbotClients.sessionManaged()
        .basicClient(basicChatbotClient)
        .cache(cache)
        .integrationName(einsteinBotConfiguration.getIntegrationName())
        .build();
  }
}