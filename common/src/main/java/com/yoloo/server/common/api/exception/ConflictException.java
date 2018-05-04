package com.yoloo.server.common.api.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ServiceException {

  public ConflictException(String message, Object... args) {
    super(HttpStatus.CONFLICT, message, args);
  }
}
