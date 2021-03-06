package com.tw.todo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.todo.exception.DuplicateTodoException;
import com.tw.todo.exception.TodoNotFoundException;
import com.tw.todo.model.Todo;
import com.tw.todo.service.impl.TodoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoServiceImpl todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo todo;

    @BeforeEach
    public void setup() {
        String todoTitle = "Todo Title";
        boolean isCompleted = false;
        boolean isPriority = false;
        boolean isEdited = false;
        String created_at = "2020-01-01T00:00:00.000Z";
        todo = new Todo(todoTitle, isCompleted, isPriority, isEdited, created_at);
    }

    @Test
    public void shouldBeAbleToSaveNewTodoDetails() throws Exception {
        given(todoService.createTodo(ArgumentMatchers.any(Todo.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(equalTo(todo.getTitle()))))
                .andExpect(jsonPath("$.isCompleted", is(equalTo(todo.getIsCompleted()))))
                .andExpect(jsonPath("$.isPriority", is(equalTo(todo.getIsPriority()))))
                .andExpect(jsonPath("$.isEdited", is(equalTo(todo.getIsEdited()))));
    }


    @Test
    public void shouldNotBeAbleToSaveDuplicateTodo() throws Exception {
        given(todoService.createTodo(ArgumentMatchers.any(Todo.class)))
                .willThrow(new DuplicateTodoException());

        ResultActions response = mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldBeAbleToGetAllTodos() throws Exception {
        String todoTitle = "Another Todo Title";
        boolean isCompleted = false;
        boolean isPriority = false;
        boolean isEdited = false;
        String created_at = "2020-01-01T00:00:00.000Z";
        Todo anotherTodo = new Todo(todoTitle, isCompleted, isPriority, isEdited, created_at);
        List<Todo> todos = List.of(todo, anotherTodo);
        given(todoService.getAllTodos()).willReturn(todos);

        ResultActions response = mockMvc.perform(get("/api/todos"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(equalTo(todos.size()))));
    }

    @Test
    public void shouldBeAbleToGetTodoById() throws Exception {
        given(todoService.getTodoById(ArgumentMatchers.anyLong())).willReturn(todo);

        ResultActions response = mockMvc.perform(get("/api/todos/1"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(equalTo(todo.getTitle()))))
                .andExpect(jsonPath("$.isCompleted", is(equalTo(todo.getIsCompleted()))))
                .andExpect(jsonPath("$.isPriority", is(equalTo(todo.getIsPriority()))))
                .andExpect(jsonPath("$.isEdited", is(equalTo(todo.getIsEdited()))));
    }

    @Test
    public void shouldNotReturnTodoIfIdNotFound() throws Exception {
        given(todoService.getTodoById(ArgumentMatchers.anyLong())).willThrow(new TodoNotFoundException());

        ResultActions response = mockMvc.perform(get("/api/todos/1"));

        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldBeAbleToUpdateTodo() throws Exception {
        given(todoService.updateTodo(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Todo.class)))
                .willAnswer(invocation -> invocation.getArgument(1));

        ResultActions response = mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(equalTo(todo.getTitle()))))
                .andExpect(jsonPath("$.isCompleted", is(equalTo(todo.getIsCompleted()))))
                .andExpect(jsonPath("$.isPriority", is(equalTo(todo.getIsPriority()))))
                .andExpect(jsonPath("$.isEdited", is(equalTo(todo.getIsEdited()))));
    }

    @Test
    public void shouldNotBeAbleToUpdateTodoIfNotFound() throws Exception {
        given(todoService.updateTodo(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Todo.class)))
                .willThrow(new TodoNotFoundException());

        ResultActions response = mockMvc.perform(put("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        response.andExpect(status().isNotFound());
    }

    @Test
    public void shouldBeAbleToDeleteTodo() throws Exception {
        willDoNothing().given(todoService).deleteTodo(ArgumentMatchers.anyLong());

        ResultActions response = mockMvc.perform(delete("/api/todos/1"));

        response.andExpect(status().isOk());
    }

    @Test
    public void shouldNotBeAbleToDeleteTodoIfNotFound() throws Exception {
        willThrow(new TodoNotFoundException()).given(todoService).deleteTodo(ArgumentMatchers.anyLong());

        ResultActions response = mockMvc.perform(delete("/api/todos/1"));

        response.andExpect(status().isNotFound());
    }
}
