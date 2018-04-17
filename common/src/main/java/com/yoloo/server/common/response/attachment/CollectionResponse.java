package com.yoloo.server.common.response.attachment;

import java.util.Collection;

public class CollectionResponse<T> {

  private String nextPageToken;
  private String prevPageToken;
  private Collection<T> data;

  private CollectionResponse() {}

  public CollectionResponse(Builder<T> builder) {
    this.nextPageToken = builder.nextPageToken;
    this.prevPageToken = builder.prevPageToken;
    this.data = builder.data;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {
    private String nextPageToken;
    private String prevPageToken;
    private Collection<T> data;

    private Builder() {}

    public Builder<T> nextPageToken(String nextPageToken) {
      this.nextPageToken = nextPageToken;
      return this;
    }

    public Builder<T> prevPageToken(String prevPageToken) {
      this.prevPageToken = prevPageToken;
      return this;
    }

    public Builder<T> data(Collection<T> data) {
      this.data = data;
      return this;
    }

    public CollectionResponse<T> build() {
      return new CollectionResponse<>(this);
    }
  }
}
