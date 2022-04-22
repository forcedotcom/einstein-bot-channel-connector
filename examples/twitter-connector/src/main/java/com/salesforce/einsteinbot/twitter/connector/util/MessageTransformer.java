/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.einsteinbot.twitter.connector.util;

import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import com.salesforce.einsteinbot.sdk.model.AnyResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ChoicesResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ChoicesResponseMessageChoices;
import com.salesforce.einsteinbot.sdk.model.EscalateResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ForceConfig;
import com.salesforce.einsteinbot.sdk.model.RequestEnvelope;
import com.salesforce.einsteinbot.sdk.model.ResponseEnvelope;
import com.salesforce.einsteinbot.sdk.model.SessionEndedResponseMessage;
import com.salesforce.einsteinbot.sdk.model.TextMessage;
import com.salesforce.einsteinbot.sdk.model.TextResponseMessage;
import com.twitter.clientlib.model.CreateTweetRequest;
import com.twitter.clientlib.model.CreateTweetRequestReply;
import com.twitter.clientlib.model.Tweet;
import java.util.List;

/**
 * RequestMessageFactory - Provides factory methods to build Chatbot Request Messages
 */
public class MessageTransformer {

  public static final String NEW_LINE = "\n";

  public static RequestEnvelope buildRequestEnvelope(String externalSessionId,
      String botId,
      String forceConfigEndPoint,
      List<AnyRequestMessage> messages) {
    return new RequestEnvelope()
        .externalSessionKey(externalSessionId)
        .botId(botId)
        .forceConfig(new ForceConfig().endpoint(forceConfigEndPoint))
        .messages(messages);
  }

  public static AnyRequestMessage buildChatbotMessage(final Tweet tweet,
      final String twitterUserNameToRemove) {
    // TODO: Update this to return different type of chatbot message depending on tweet data
    String tweetText = tweet.getText();
    String text = tweetText.replaceAll(twitterUserNameToRemove, "");
    return new TextMessage()
        .text(text)
        .type(TextMessage.TypeEnum.TEXT)
        .sequenceId(System.currentTimeMillis());
  }

  public static CreateTweetRequest buildTweetRequest(final ResponseEnvelope responseEnvelope,
      final Tweet incomingTweet) {
    CreateTweetRequest tweetRequest = new CreateTweetRequest();
    StringBuilder builder = new StringBuilder();
    for (AnyResponseMessage message : responseEnvelope.getMessages()) {
      if (message instanceof TextResponseMessage) {
        TextResponseMessage textResponseMessage = (TextResponseMessage) message;
        builder.append(textResponseMessage.getText()).append(NEW_LINE);
      }
      if (message instanceof ChoicesResponseMessage) {
        // TODO: Explore how to send choices in a tweet other than just text
        ChoicesResponseMessage choicesResponseMessage = (ChoicesResponseMessage) message;
        for (ChoicesResponseMessageChoices choice : choicesResponseMessage.getChoices()) {
          builder.append(choice.getAlias()).append(": ").append(choice.getLabel()).append(NEW_LINE);
        }
      }
      if (message instanceof SessionEndedResponseMessage) {
        builder.append("Goodbye!");
      }
      if (message instanceof EscalateResponseMessage) {
        builder.append("Sorry, I cannot help you but I will ask someone to get back to you.");
      }
    }
    tweetRequest.text(builder.toString());
    tweetRequest.setReply(new CreateTweetRequestReply().inReplyToTweetId(incomingTweet.getId()));
    return tweetRequest;
  }

}
