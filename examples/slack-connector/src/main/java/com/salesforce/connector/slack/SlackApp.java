/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.connector.slack;

import com.salesforce.connector.chatbot.service.SessionIdProvider;
import com.salesforce.connector.chatbot.util.Constants;
import com.salesforce.connector.slack.service.SlackService;
import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.ActionContext;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;
import java.io.IOException;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.slack.api.bolt.App;


@Configuration
public class SlackApp {

  private static final Logger logger = LoggerFactory.getLogger(SlackApp.class);

  @Autowired
  private SessionIdProvider sessionIdProvider;

  @Autowired
  private SlackService slackService;

  /*
   * This function handles all selection events.
   * In slack, that means either a menu item is selected or a button is pressed.
   */
  private void handleSelection(App app, BlockActionRequest req,
      ActionContext ctx, boolean isButton) throws SlackApiException, IOException {
    String user = req.getPayload().getUser().getId();
    String requestText;
    String channel = req.getPayload().getChannel().getId();
    String threadTs = req.getPayload().getMessage().getThreadTs();

    if(req.getPayload().getActions().isEmpty()){
      slackService.postSlackErrorMessage(ctx, channel, threadTs);
    }else {
      if (isButton){
        requestText = req.getPayload().getActions().get(0).getValue();
      }else {
        requestText = req.getPayload().getActions().get(0).getSelectedOption().getValue();
      }

      if(slackService.isReplyInThreadChannel(channel) && threadTs==null){
        threadTs = req.getPayload().getMessage().getTs();
      }

      logger.debug("Full selection payload {}", req.getPayload().getActions().get(0).toString());
      logger.debug("Request Text {}", requestText);
      slackService.process(app, requestText, this.sessionIdProvider, ctx, user, channel, threadTs);
    }

  }


  /*
   * This function handles all at mention events for the bot.
   * This will be triggered whenever the bot is at mentioned in a channel.
   */
  private void handleAtMention(App app, EventsApiPayload<AppMentionEvent> req, EventContext ctx){
    app.executorService().execute(() -> {
      String requestText = slackService.cleanTextRequest(req.getEvent().getText(),true);
      String user = req.getEvent().getUser();
      String channel = req.getEvent().getChannel();
      String threadTs = req.getEvent().getThreadTs();
      if(slackService.isReplyInThreadChannel(channel) && threadTs==null){
        threadTs = req.getEvent().getTs();
      }
      slackService.process(app, requestText, this.sessionIdProvider, ctx, user, channel, threadTs);
    });
  }


  /*
   * This function handles all generic messages in slack.
   * Every message in any channel / group chat / DM will trigger this function.
   * This function performs 2 tasks:
   * 1. Perform a specific action for a certain keyword.
   *    a. If the keywords "reset" and "password" are present, provide some text.
   *    b. If the message is a ":wave:", reset the externalSessionId for that user.
   * 2. Forward the request to einstein bot if the message is a DM and provide a reply.
   */
  private void handleGenericMessage(App app, EventsApiPayload<MessageEvent> payload, EventContext ctx)
      throws SlackApiException, IOException {
    String requestText = payload.getEvent().getText();
    String user = payload.getEvent().getUser();
    String channel = payload.getEvent().getChannel();
    String channelType = payload.getEvent().getChannelType();
    String threadTs = payload.getEvent().getThreadTs();
    if(slackService.isReplyInThreadChannel(channel) && threadTs==null){
      threadTs = payload.getEvent().getTs();
    }

    if (requestText.contains(Constants.SLACK_WAVE)){
      this.sessionIdProvider.newSessionId(user);
      ctx.say("Hi <@" + user + ">, your session has been reset");
    } else if (channelType.equals(Constants.IM) ){
      slackService.process(app, requestText, this.sessionIdProvider, ctx, user, channel, threadTs);
    }
  }

  /*
   * This init function creates the Slack app (bot) that is provided to the bolt framework.
   * All working mechanisms for the bot are defined in this function and added to the app object
   * which is returned by the function.
   */
  @Bean
  public App initSlackApp() {

    App app = new App();

    /*
     * Handle slash command.
     * This slash command adds the current channel to list of channels to in which
     * replies are to be threaded.
     */
    app.command("/reply-in-thread-toggle-chatbot", (req, ctx) -> {
      this.slackService.addThreadToReplyInThread(ctx.getChannelId());
      return ctx.ack("I toggled reply in thread for this channel");
    });

    // Handle at mention event
    app.event(AppMentionEvent.class, (req, ctx) -> {
      handleAtMention(app, req, ctx);
      return ctx.ack();
    });

    // Handle generic message
    Pattern allMessages = Pattern.compile(".*");
    app.message(allMessages, (payload, ctx) -> {
      handleGenericMessage(app, payload, ctx);
      return ctx.ack();
    });

    // Handle button press
    Pattern buttonPattern = Pattern.compile("bot-buttons.*");
    app.blockAction(buttonPattern, (req, ctx) -> {
      this.handleSelection(app, req, ctx, true);
      return ctx.ack();
    });

    // Handle menu selection
    app.blockAction("bot-menu", (req, ctx) -> {
      this.handleSelection(app, req, ctx, false);
      return ctx.ack();
    });

    return app;
  }
}