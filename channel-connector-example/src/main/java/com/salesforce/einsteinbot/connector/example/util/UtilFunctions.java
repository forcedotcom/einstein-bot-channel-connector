/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.salesforce.einsteinbot.connector.example.model.ErrorResult;
import com.salesforce.einsteinbot.sdk.client.ClientBuilderUtil;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * UtilFunctions - Contains static utility functions.
 */
public class UtilFunctions {

  private static final ObjectMapper mapper = ClientBuilderUtil.getMapper();
  private static final DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();


  static {
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
  }

  public static ErrorResult convertStackTraceToErrorResponse(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    String[] stackTrace = sw.toString().replaceAll("\t", "").split("\n");
    String errorMessage = e.getMessage();
    return new ErrorResult(errorMessage, stackTrace);
  }

  public static Object convertObjectToJson(Object data) {
    try {
      return mapper.writer(prettyPrinter).writeValueAsString(data);
    } catch (JsonProcessingException e) {
      return convertStackTraceToErrorResponse(e);
    }
  }
}
