package ie.todolist.api.service;

import ie.todolist.api.auth.User;
import ie.todolist.api.auth.UserRepository;
import ie.todolist.api.auth.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemoData {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;
  private final TodolistRepository todolistRepository;

  @EventListener(ApplicationReadyEvent.class)
  public void initDemoData() {
    var user = User.builder()
      .first("Bob")
      .last("Smith")
      .email("bob@todolist.ie")
      .password(passwordEncoder.encode("password"))
      .locked(false)
      .verified(true)
      .role(UserRole.USER)
      .build();
    userRepository.save(user);
    user = userRepository.findByEmail("bob@todolist.ie")
      .orElseThrow();
    todoRepository.save(new Todo(null,user.getId(),1,"First Todo","Finish Openapi.yml"));
    todoRepository.save(new Todo(null,user.getId(),2,"Second Todo","Frontend"));
    var todos = todoRepository.findByUserId(user.getId())
      .orElseThrow();
    var todoIds = todos.stream().map(Todo::getId).toList();
    var todolist = Todolist.builder()
      .userId(user.getId())
      .status(Todolist.TodoStatus.TODO)
      .todoIds(todoIds)
      .build();
    todolistRepository.save(todolist);
    System.out.println("Initialized Demo Data");
  }

}
