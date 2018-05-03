package com.yoloo.server.common.api.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

  @JsonIgnore
  @JsonProperty("status")
  private int status;

  @JsonProperty("error")
  private String error;

  @JsonProperty("message")
  private String message;

  @JsonProperty("field")
  private String field;

  @JsonProperty("rejectedValue")
  private Object rejectedValue;

  private Error(Builder builder) {
    this.status = builder.status;
    this.error = builder.error;
    this.message = builder.message;
    this.field = builder.field;
    this.rejectedValue = builder.rejectedValue;
  }

  public static Builder builder() {
    return new Builder();
  }

  public int getStatus() {
    return status;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getField() {
    return field;
  }

  public Object getRejectedValue() {
    return rejectedValue;
  }

  public static class Builder {
    private int status;
    private String error;
    private String message;
    private String field;
    private Object rejectedValue;

    Builder() {}

    public Builder status(int status) {
      this.status = status;
      return this;
    }

    public Builder error(String error) {
      this.error = error;
      return this;
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder field(String field) {
      this.field = field;
      return this;
    }

    public Builder rejectedValue(Object rejectedValue) {
      this.rejectedValue = rejectedValue;
      return this;
    }

    public Error build() {
      return new Error(this);
    }
  }
}
