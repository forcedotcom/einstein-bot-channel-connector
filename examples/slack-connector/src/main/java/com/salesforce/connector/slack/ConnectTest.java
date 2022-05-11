package com.salesforce.connector.slack;

import com.slack.api.Slack;
import com.slack.api.methods.response.api.ApiTestResponse;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

public class ConnectTest {
  public static void main(String[] args) throws Exception {
    Slack slack = Slack.getInstance();
    ApiTestResponse apiresponse = slack.methods().apiTest(r -> r.foo("bar"));
    System.out.println(apiresponse);
    String token = System.getenv("SLACK_BOT_TOKEN");
    System.out.println(token);
    MethodsClient methods = slack.methods(token);
    // Build a request object
    ChatPostMessageRequest request = ChatPostMessageRequest.builder()
        .channel("#einstein-slack-bot-testing")
        .text(":wave: Hi from a bot written in Java!")
        .build();

    // Get a response as a Java object
    ChatPostMessageResponse response = methods.chatPostMessage(request);

  }
}