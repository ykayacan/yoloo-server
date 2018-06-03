package com.yoloo.server.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoloo.server.common.vo.PubSubResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public final class PubSubHelper {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static PubSubResponse convertToPubSubResponse(HttpServletRequest request)
      throws IOException {
    return MAPPER.readValue(request.getInputStream(), PubSubResponse.class);
  }
}
