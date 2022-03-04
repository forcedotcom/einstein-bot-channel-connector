/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import java.util.StringJoiner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ChatbotConfiguration - Class to hold Chatbot configuration defined in Application properties.
 *
 * @author relango
 */
@Configuration
@ConfigurationProperties(prefix = "sfdc.einstein.bots")
public class EinsteinBotConfiguration {

  private String runtimeUrl;
  private String integrationName;
  private Cache cache;
  private OAuth oAuth;

  public String getIntegrationName() {
    return integrationName;
  }

  public String getRuntimeUrl() {
    return runtimeUrl;
  }

  public Cache getCache() {
    return cache;
  }

  public OAuth getoAuth() {
    return oAuth;
  }

  public void setIntegrationName(String integrationName) {
    this.integrationName = integrationName;
  }

  public void setRuntimeUrl(String runtimeUrl) {
    this.runtimeUrl = runtimeUrl;
  }

  public void setCache(Cache cache) {
    this.cache = cache;
  }

  public void setoAuth(OAuth oAuth) {
    this.oAuth = oAuth;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", EinsteinBotConfiguration.class.getSimpleName() + "[", "]")
        .add("runtimeUrl='" + runtimeUrl + "'")
        .add("integrationName='" + integrationName + "'")
        .add("cache=" + cache)
        .add("oAuth=" + oAuth)
        .toString();
  }

  public static class Cache {

    private Long ttlSeconds;
    private String redisUrl;

    public Long getTtlSeconds() {
      return ttlSeconds;
    }

    public void setTtlSeconds(Long ttlSeconds) {
      this.ttlSeconds = ttlSeconds;
    }

    public String getRedisUrl() {
      return redisUrl;
    }

    public void setRedisUrl(String redisUrl) {
      this.redisUrl = redisUrl;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", Cache.class.getSimpleName() + "[", "]")
          .add("ttlSeconds=" + ttlSeconds)
          .add("redisUrl='" + redisUrl + "'")
          .toString();
    }
  }

  public static class OAuth {

    private String privateKeyFile;
    private String loginEndpoint;
    private String connectedAppId;
    private String connectedAppSecret;
    private String userId;

    public String getPrivateKeyFile() {
      return privateKeyFile;
    }

    public void setPrivateKeyFile(String privateKeyFile) {
      this.privateKeyFile = privateKeyFile;
    }

    public String getLoginEndpoint() {
      return loginEndpoint;
    }

    public void setLoginEndpoint(String loginEndpoint) {
      this.loginEndpoint = loginEndpoint;
    }

    public String getConnectedAppId() {
      return connectedAppId;
    }

    public void setConnectedAppId(String connectedAppId) {
      this.connectedAppId = connectedAppId;
    }

    public String getConnectedAppSecret() {
      return connectedAppSecret;
    }

    public void setConnectedAppSecret(String connectedAppSecret) {
      this.connectedAppSecret = connectedAppSecret;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", OAuth.class.getSimpleName() + "[", "]")
          .add("privateKeyFile='" + privateKeyFile + "'")
          .add("loginEndpoint='" + loginEndpoint + "'")
          .add("connectedAppId='" + connectedAppId + "'")
          .add("connectedAppSecret='" + connectedAppSecret + "'")
          .add("userId='" + userId + "'")
          .toString();
    }
  }
}
