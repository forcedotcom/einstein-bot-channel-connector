/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.einsteinbot.twitter.connector.config;

import com.twitter.clientlib.ApiClient;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.TwitterCredentialsOAuth1;
import com.twitter.clientlib.api.TwitterApi;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Sets up required beans
 */
@Configuration
@EnableAsync
public class ApplicationConfig {

  private final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

  @Value("${twitter.oauth.consumerKey}")
  private String twitterConsumerKey;

  @Value("${twitter.oauth.consumerSecret}")
  private String twitterConsumerSecret;

  @Value("${twitter.oauth.accessToken}")
  private String twitterAccessToken;

  @Value("${twitter.oauth.accessTokenSecret}")
  private String twitterAccessTokenSecret;

  @Value("${twitter.bearerToken}")
  private String twitterBearerToken;
  @Bean
  public TwitterApi getTwitterApi() {
    logger.info("Setting up twitter api client");
    OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();
    ApiClient apiClient = new ApiClient(okHttpClient);
    TwitterApi twitterApi = new TwitterApi(apiClient);
    // TODO: generate bearer token using client credentials flow (refer to Twitter API v2 Postman)
    TwitterCredentialsBearer bearerCrendentials = new TwitterCredentialsBearer(this.twitterBearerToken);
    TwitterCredentialsOAuth1 credentials = new TwitterCredentialsOAuth1(this.twitterConsumerKey,
        this.twitterConsumerSecret, this.twitterAccessToken, this.twitterAccessTokenSecret);
    twitterApi.setTwitterCredentials(credentials);
    twitterApi.setTwitterCredentials(bearerCrendentials);
    logger.info("Done setting up twitter api client");
    return twitterApi;
  }

}
