package com.tw.todo.service;

import com.tw.todo.exception.DuplicateTodoException;
import com.tw.todo.exception.TodoNotFoundException;
import com.tw.todo.model.Todo;

import java.util.List;

public interface TodoService {

    Todo createTodo(Todo todo) throws DuplicateTodoException;

    List<Todo> getAllTodos();

    Todo getTodoById(long todoId) throws TodoNotFoundException;

    Todo updateTodo(long todoId, Todo todo) throws TodoNotFoundException;

    void deleteTodo(long todoId) throws TodoNotFoundException;
}