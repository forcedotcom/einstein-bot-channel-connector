/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.einsteinbot.twitter.connector.service;

import com.google.common.reflect.TypeToken;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.AddOrDeleteRulesRequest;
import com.twitter.clientlib.model.AddOrDeleteRulesResponse;
import com.twitter.clientlib.model.AddRulesRequest;
import com.twitter.clientlib.model.FilteredStreamingTweetOneOf;
import com.twitter.clientlib.model.GetRulesResponse;
import com.twitter.clientlib.model.RuleNoId;
import com.twitter.clientlib.model.Tweet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This class setsup the rules for filtered stream and then starts the twitter stream.
 */
@Service
public class TwitterStreamService {

  private static final Logger logger = LoggerFactory.getLogger(TwitterStreamService.class);

  @Value("${twitter.user.name}")
  private String userName;

  @Value("${twitter.user.id}")
  private String userId;

  @Value("#{'${twitter.rule.ids}'.split(',')}")
  private List<String> ruleIds;

  @Autowired
  private TwitterApi twitterApi;

  @Autowired
  private EinsteinBotService einsteinBotService;

  @Async
  public void startStreaming() {
    logger.info("Starting filtered stream from twitter");
    try {
      this.upsertRule();
    } catch (Exception e) {
      logger.error("Error in reading or creating rules for stream", e);
    }
    // Run forever trying to get tweet stream and then handling the tweets
    while (true) {
      try {
        this.connectAndReadStream();
      } catch (Exception e) {
        logger.error("Error in getting or reading stream", e);
        logger.info("Sleep and try to get stream again");
        try {
          Thread.sleep(2000);
        } catch (InterruptedException ex) {
          logger.error("Error when waiting to connect again", ex);
          return;
        }
      }
    }
  }

  private void connectAndReadStream() throws Exception {
    logger.info("Connecting to filtered stream for tweets");
    // Set<String> | A comma separated list of Tweet fields to display.
    Set<String> tweetFields = new HashSet<>(
        Arrays.asList("id", "text", "author_id", "conversation_id", "geo"));
    Set<String> emptySet = new HashSet<>(Arrays.asList());
    InputStream result;
    try {
      result = this.twitterApi.tweets()
          .searchStream(emptySet, tweetFields, emptySet, emptySet, emptySet, emptySet, null);
    } catch (ApiException e) {
      logger.error("Error when setting up stream. Status code: {}, response body: {}", e.getCode(),
          e.getResponseBody());
      throw e;
    }
    BufferedReader reader = null;
    try {
      logger.info("Got the stream!");
      JSON json = new JSON();
      Type localVarReturnType = new TypeToken<FilteredStreamingTweetOneOf>() {
      }.getType();
      reader = new BufferedReader(new InputStreamReader(result));
      String line = reader.readLine();
      while (line != null) {
        logger.debug("STREAM INPUT: {}", line);
        if (!line.isEmpty()) {
          FilteredStreamingTweetOneOf filteredTweet = json.getGson()
              .fromJson(line, localVarReturnType);
          logger.debug("STREAM INPUT: {}", filteredTweet.toString());
          Tweet tweet = filteredTweet.getData();
          if (!tweet.getAuthorId().equals(this.userId)) {
            this.einsteinBotService.sendMessage(tweet);
          } else {
            logger.debug("Ignoring own messages");
          }
        }
        line = reader.readLine();
      }
    } finally {
      if (reader != null) {
        reader.close();
      }
      result.close();
    }
  }

  private void upsertRule() throws ApiException {
    logger.debug("Checking if the rule exists");
    // TODO: If config var is not set and upon its first boot this app creates a rule then we shouldn't
    //  create rule again on next startup.
    if (!isRuleSetup()) {
      logger.info("No rules configured, hence setting up rule");
      AddRulesRequest addRulesRequest = new AddRulesRequest();
      RuleNoId rule = new RuleNoId();
      rule.setValue("has:mentions " + this.userName);
      rule.setTag(this.userName + " mentions");
      addRulesRequest.addAddItem(rule);
      AddOrDeleteRulesRequest rulesRequest = new AddOrDeleteRulesRequest(addRulesRequest);
      AddOrDeleteRulesResponse rulesResponse = this.twitterApi.tweets()
          .addOrDeleteRules(rulesRequest, true);
      logger.info("Add rule response: {}", rulesResponse);
    } else {
      logger.info("Fetching rules for ids: {}", this.ruleIds);
      GetRulesResponse rules = this.twitterApi.tweets()
          .getRules(this.ruleIds, this.ruleIds.size(), null);
      logger.debug("Rules: {}", rules);
    }
  }

  private boolean isRuleSetup() {
    boolean ruleSetup = false;
    if (this.ruleIds != null) {
      ruleSetup = this.ruleIds.stream().anyMatch(s -> !s.isEmpty());
    }
    return ruleSetup;
  }
}
