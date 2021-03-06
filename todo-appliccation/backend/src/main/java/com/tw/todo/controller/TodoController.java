package com.tw.todo.controller;

import com.tw.todo.exception.DuplicateTodoException;
import com.tw.todo.exception.TodoNotFoundException;
import com.tw.todo.model.Todo;
import com.tw.todo.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(@RequestBody Todo todo) throws DuplicateTodoException {
        return todoService.createTodo(todo);
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}")
    public Todo getTodoById(@PathVariable long id) throws TodoNotFoundException {
        return todoService.getTodoById(id);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable long id, @RequestBody Todo todo) throws TodoNotFoundException {
        return todoService.updateTodo(id, todo);
    }

    @DeleteMapping("/{id}")
    public String deleteTodo(@PathVariable long id) throws TodoNotFoundException {
        todoService.deleteTodo(id);
        return "Todo Deleted Successfully!";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(DuplicateTodoException exception) {
        return "Duplicate Todo Title";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(TodoNotFoundException exception) {
        return "Todo Not Found";
    }

}