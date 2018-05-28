package com.yoloo.server.search.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Map;

public class Message {
  private Map<String, String> attributes;
  private String data;

  @JsonProperty("message_id")
  private String messageId;

  private Message() {}

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public String getData() {
    return data;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getJsonData() {
    byte[] bytes = Base64.getDecoder().decode(data);
    return new String(bytes, Charset.forName("UTF-8"));
  }
}
