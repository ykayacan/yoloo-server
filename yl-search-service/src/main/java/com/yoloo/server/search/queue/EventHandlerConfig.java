package com.yoloo.server.search.queue;

import com.yoloo.server.search.post.PostRepository;
import com.yoloo.server.search.queue.handler.*;
import com.yoloo.server.search.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class EventHandlerConfig {

  // REFACTOR: refactor this mess into a builder like pattern
  @Lazy
  @Bean
  public EventHandler eventHandlerChain(
      PostRepository postRepository, UserRepository userRepository) {
    return new NewPostEventHandler(postRepository)
        .setNext(
            new UpdatePostEventHandler(postRepository)
                .setNext(
                    new DeletePostEventHandler(postRepository)
                        .setNext(
                            new NewUserEventHandler(userRepository)
                                .setNext(
                                    new UpdateUserEventHandler(userRepository)
                                        .setNext(new DeleteUserEventHandler(userRepository))))));
  }
}
