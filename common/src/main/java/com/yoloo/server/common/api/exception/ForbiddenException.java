package com.yoloo.server.common.api.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ServiceException {

  public ForbiddenException(String message, Object... args) {
    super(HttpStatus.FORBIDDEN, message, args);
  }
}
