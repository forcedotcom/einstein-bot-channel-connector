package com.salesforce.connector.slack.service;

import com.salesforce.connector.chatbot.service.ChatbotService;
import com.salesforce.connector.chatbot.util.Constants;
import com.salesforce.connector.chatbot.service.SessionIdProvider;
import com.salesforce.einsteinbot.sdk.client.model.BotResponse;
import com.salesforce.einsteinbot.sdk.model.AnyResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ChoicesResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ResponseEnvelope;
import com.salesforce.einsteinbot.sdk.model.SessionEndedResponseMessage;
import com.salesforce.einsteinbot.sdk.model.TextResponseMessage;
import com.google.common.base.Preconditions;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.block.composition.OptionObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.bolt.App;
import com.slack.api.bolt.context.Context;
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.ButtonElement;
import com.slack.api.model.block.element.StaticSelectElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class SlackService {

  private static final Logger logger = LoggerFactory.getLogger(SlackService.class);

  @Autowired
  private ChatbotService chatbotService;

  private String replyInThreadChannels;

  @PostConstruct
  public void initialize(){

    if(System.getenv(Constants.REPLY_IN_THREAD_CHANNELS)!=null){
      this.replyInThreadChannels = System.getenv(Constants.REPLY_IN_THREAD_CHANNELS);
    } else {
      this.replyInThreadChannels = "";
    }
  }

  public void addThreadToReplyInThread(String channelId){
    if(this.replyInThreadChannels.contains(channelId)){
      this.replyInThreadChannels = this.replyInThreadChannels.replace(channelId,
          Constants.EMPTY_STRING);
    }else {
      this.replyInThreadChannels += "," + channelId;
    }
  }

  public boolean isFlag(String key){
    return (System.getenv(key)!=null && System.getenv(key).equalsIgnoreCase("true"));
  }

  public boolean isReplyInThreadChannel(String channel){
    return (this.replyInThreadChannels!=null && this.replyInThreadChannels.contains(channel));
  }

  public String cleanTextResponse(String responseText, String user){
    return responseText.replace(Constants.BOT_RESPONSE_CLEANUP_MATCH,"<@"+ user + ">");
  }

  public String cleanTextRequest(String text, boolean isAppMention){
    String response = text;
    if (isAppMention) {
      response = response.substring(14);
    }
    return response.trim();
  }

  private void processTextResponse(AnyResponseMessage message, Context ctx, String user,
      String channel, String threadTs ) throws SlackApiException, IOException {
    logger.debug("MESSAGE: {}", ((TextResponseMessage) message).getText());
    String response = cleanTextResponse(((TextResponseMessage) message).getText(),
        user);
    ctx.client().chatPostMessage(r -> r
        .channel(channel)
        .text(response)
        .threadTs(threadTs)
        .parse("full")
        .unfurlLinks(true)
        .unfurlMedia(true));

  }

  private void processChoiceResponse(AnyResponseMessage message, Context ctx, String channel,
      String threadTs) throws SlackApiException, IOException {
    logger.debug("MESSAGE: **choice message**");
    java.util.List<com.slack.api.model.block.LayoutBlock> messageBlocks;
    if (isFlag(Constants.USE_BUTTONS)) {
      messageBlocks = createSlackButtonMessage((ChoicesResponseMessage) message);
    } else {
      messageBlocks = createSlackStaticSelectMessage((ChoicesResponseMessage) message);
    }
    logger.debug(messageBlocks.toString());
    ctx.client().chatPostMessage(r -> r
        .channel(channel)
        .blocks(messageBlocks)
        .threadTs(threadTs));

  }

  public void postSlackErrorMessage(Context ctx, String channel, String threadTs)
      throws SlackApiException, IOException {
    ctx.client().chatPostMessage(r -> r
        .channel(channel)
        .text(Constants.ERROR_MESSAGE_SLACK)
        .threadTs(threadTs));
  }

  private void postBotErrorMessage(Context ctx, String channel, String threadTs)
      throws SlackApiException, IOException {
    ctx.client().chatPostMessage(r -> r
        .channel(channel)
        .text(Constants.ERROR_MESSAGE_BOT)
        .threadTs(threadTs));
  }

  private void sendToSlack(BotResponse botResponse, Context ctx, String user, String channel,
                           String threadTs, SessionIdProvider sessionIdProvider) {
    logger.debug("USER {} " , user);
    logger.debug("CHANNEL {} " , channel);
    logger.debug("THREADTS {}" , threadTs);
    logger.debug("SESSIONID {}" , sessionIdProvider.getSessionId(user));
    /*
     * Iterate over every message in response from einstein bots and convert each piece
     * to an appropriate Slack message.
     */
    try{
      if(botResponse != null) {
        ResponseEnvelope resp = botResponse.getResponseEnvelope();
        for (AnyResponseMessage message : resp.getMessages()) {
          if (message instanceof TextResponseMessage) {
            processTextResponse(message, ctx, user, channel, threadTs);
          } else if (message instanceof ChoicesResponseMessage) {
            processChoiceResponse(message, ctx, channel, threadTs);
          } else if (message instanceof SessionEndedResponseMessage) {
            sessionIdProvider.newSessionId(user);
          }
        }
      } else {
        postBotErrorMessage(ctx, channel, threadTs);
      }
    } catch (Exception exception) {
      logger.error("Encountered Exception sending request to slack", exception);
    }
  }

  public void process(App app, String requestText, SessionIdProvider sessionIdProvider, Context ctx, String user,
                      String channel, String threadTs){
    app.executorService().execute(() -> {
       BotResponse botsResponse = this.chatbotService.sendToBots(requestText,
           sessionIdProvider.getSessionId(user));
       this.sendToSlack(botsResponse, ctx, user, channel, threadTs, sessionIdProvider);
    });
  }

  private List<LayoutBlock> createSlackButtonMessage(ChoicesResponseMessage botButtonMessage){
    Preconditions.checkArgument(botButtonMessage != null && botButtonMessage.getChoices().size() >0,
        "The bot button message can not be null.");
    List<LayoutBlock> blocks = new ArrayList<>();
    List<BlockElement> blockElements = new ArrayList<>();
    for(int i = 0; i < botButtonMessage.getChoices().size() ; i ++){
      ButtonElement currentButton = new ButtonElement();
      currentButton.setActionId("bot-buttons"+i);
      String currentValue = botButtonMessage.getChoices().get(i).getLabel();
      currentButton.setValue(currentValue);
      String currentLabel = currentValue.substring(0, Math.min(currentValue.length(), 60));
      currentButton.setText(getBlockKitTextObject(currentLabel));
      blockElements.add(currentButton);
    }
    blocks.add(ActionsBlock
        .builder()
        .elements(blockElements)
        .build());
    return  blocks;
  }

  public List<LayoutBlock> createSlackStaticSelectMessage(ChoicesResponseMessage botMenuMessage){
    Preconditions.checkArgument(botMenuMessage != null && botMenuMessage.getChoices().size() > 0,
        "The bot menu message can not be null.");
    List<LayoutBlock> blocks = new ArrayList<>();
    List<BlockElement> blockElements = new ArrayList<>();
    StaticSelectElement menuObject = new StaticSelectElement();
    menuObject.setActionId("bot-menu");
    List<OptionObject> menuOptions = new ArrayList<>();
    for(int i = 0; i < botMenuMessage.getChoices().size() ; i ++){
      OptionObject currentOption = new OptionObject();
      String currentValue = botMenuMessage.getChoices().get(i).getLabel();
      currentOption.setValue(currentValue);
      String currentText = currentValue.substring(0, Math.min(currentValue.length(), 60));
      currentOption.setText(getBlockKitTextObject(currentText));
      menuOptions.add(currentOption);
    }
    menuObject.setOptions(menuOptions);
    blockElements.add(menuObject);
    blocks.add(ActionsBlock
        .builder()
        .elements(blockElements)
        .build());
    return blocks;
  }

  private PlainTextObject getBlockKitTextObject(String text){
    PlainTextObject currentText = new PlainTextObject();
    currentText.setText(text);
    return currentText;
  }

}
