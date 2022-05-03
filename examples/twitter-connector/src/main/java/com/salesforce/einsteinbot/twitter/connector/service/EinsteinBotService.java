/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.einsteinbot.twitter.connector.service;

import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import com.salesforce.einsteinbot.sdk.client.model.BotResponse;
import com.salesforce.einsteinbot.sdk.client.model.BotSendMessageRequest;
import com.salesforce.einsteinbot.sdk.client.model.ExternalSessionId;
import com.salesforce.einsteinbot.sdk.client.model.RequestConfig;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import com.salesforce.einsteinbot.twitter.connector.listener.TweetCreateResponseApiCallback;
import com.salesforce.einsteinbot.twitter.connector.util.MessageTransformer;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.CreateTweetRequest;
import com.twitter.clientlib.model.Tweet;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This class serves as the service layer to talk to EinsteinBots.
 */
@Service
public class EinsteinBotService {

  private static final Logger logger = LoggerFactory.getLogger(EinsteinBotService.class);

  @Autowired
  private SessionManagedChatbotClient chatbotClient;

  @Autowired
  private TwitterApi twitterApi;

  @Value("${twitter.user.name}")
  private String twitterUserName;

  @Value("${sfdc.einstein.bots.orgId}")
  private String orgId;

  @Value("${sfdc.einstein.bots.botId}")
  private String botId;

  @Value("${sfdc.einstein.bots.force-config-endpoint}")
  private String forceConfigEndpoint;

  private RequestConfig requestConfig;

  private TweetCreateResponseApiCallback tweetCreateResponseApiCallback;

  @PostConstruct
  public void setup() {
    this.requestConfig = RequestConfig.with()
        .botId(botId)
        .orgId(orgId)
        .forceConfigEndpoint(forceConfigEndpoint)
        .build();
    this.tweetCreateResponseApiCallback = new TweetCreateResponseApiCallback();
    logger.info("Setup EinsteinBotService");
  }

  /**
   * Receives tweet and sends it to einstein bots for processing. Once it receives response from
   * Einstein bots it replies back to that tweet. Currently it only consumes text from tweet and
   * replies back with a text as well.
   * <p>
   *
   * @param tweet
   */
  @Async
  public void sendMessage(final Tweet tweet) {
    logger.debug("Received tweet with text: {}", tweet.getText());
    // 1. Convert tweet to einstein bot request envelope
    ExternalSessionId externalSessionId = new ExternalSessionId(tweet.getConversationId());
    AnyRequestMessage message = MessageTransformer.buildChatbotMessage(tweet, this.twitterUserName);
    BotSendMessageRequest sendMessageRequest = MessageTransformer
        .buildBotSendMessageRequest(message);
    //TODO logger.debug("Converted tweet into Einstein bot request envelope: {}", requestEnvelope);
    BotResponse botResponse = null;
    try {
      // 2. Send message to einstein bot
      botResponse = this.chatbotClient
          .sendMessage(requestConfig, externalSessionId, sendMessageRequest);
      logger.debug("Received response from Einstein bot: {}", botResponse);
    } catch (Exception e) {
      logger.error("Error when calling einstein bots", e);
      // If no response from bots then don't respond to tweet
      return;
    }
    // 3. Convert einstein bot response to tweet
    CreateTweetRequest tweetRequest = MessageTransformer
        .buildTweetRequest(botResponse.getResponseEnvelope(), tweet);
    logger.debug("Converted Einstein response to tweet request: {}", tweetRequest);
    // 4. Reply to tweet
    try {
      this.twitterApi.tweets().createTweetAsync(tweetRequest, this.tweetCreateResponseApiCallback);
    } catch (ApiException e) {
      logger.error("Error in replying to tweet. Status: {}, response: {}", e.getCode(),
          e.getResponseBody());
    }
  }
}
