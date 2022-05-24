/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.connector.slack;

public class Constants {
  public static String REPLY_IN_THREAD_CHANNELS = "REPLY_IN_THREAD_CHANNELS";
  public static String SLACK_WAVE = ":wave:";
  public static String IM = "im";
  public static String EMPTY_STRING = "";
  public static String USE_BUTTONS = "USE_BUTTONS";
  public static String BOT_RESPONSE_CLEANUP_MATCH = "{!$Context.Channel_Name}";
  public static String ERROR_MESSAGE_BOT = "An error was encountered talking to bots. :(";
  public static String ERROR_MESSAGE_SLACK = "An error was encountered talking to slack :(";
}
