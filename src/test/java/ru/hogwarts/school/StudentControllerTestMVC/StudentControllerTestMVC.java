package ru.hogwarts.school.StudentControllerTestMVC;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTestMVC {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void testCreateStudent() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 18);

        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Гарри Поттер")))
                .andExpect(jsonPath("$.age", is(18)));
    }

    @Test
    void testGetStudentById() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 18);

        when(studentService.getStudentById(1L)).thenReturn(student);
        when(studentService.getStudentById(999L)).thenReturn(null);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Гарри Поттер")))
                .andExpect(jsonPath("$.age", is(18)));

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateStudent() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 19);
        Student updatedStudent = new Student(1L, "Гарри Поттер Обновленный", 20);

        when(studentService.updateStudent(eq(1L), any(Student.class))).thenReturn(updatedStudent);
        when(studentService.updateStudent(eq(999L), any(Student.class))).thenReturn(null);

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Гарри Поттер Обновленный")))
                .andExpect(jsonPath("$.age", is(20)));

        mockMvc.perform(put("/student/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteStudent() throws Exception {
        Student student = new Student(1L, "Гарри Поттер", 18);

        when(studentService.deleteStudent(1L)).thenReturn(student);
        when(studentService.deleteStudent(999L)).thenReturn(null);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Гарри Поттер")));

        mockMvc.perform(delete("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllStudents() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 18),
                new Student(2L, "Гермиона Грейнджер", 18)
        );

        when(studentService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Гарри Поттер")))
                .andExpect(jsonPath("$[1].name", is("Гермиона Грейнджер")));
    }

    @Test
    void testGetStudentsByAge() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 18),
                new Student(2L, "Гермиона Грейнджер", 18)
        );

        when(studentService.getStudentsByAge(18)).thenReturn(students);

        mockMvc.perform(get("/student/age/18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].age", is(18)))
                .andExpect(jsonPath("$[1].age", is(18)));
    }

    @Test
    void testGetStudentsByAgeBetween() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 18),
                new Student(2L, "Гермиона Грейнджер", 19)
        );

        when(studentService.getStudentsByAgeBetween(18, 19)).thenReturn(students);

        mockMvc.perform(get("/student/age/between")
                        .param("minAge", "18")
                        .param("maxAge", "19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].age", is(18)))
                .andExpect(jsonPath("$[1].age", is(19)));
    }

    @Test
    void testGetStudentFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        Student student = new Student(1L, "Гарри Поттер", 18);
        student.setFaculty(faculty);

        when(studentService.getStudentFaculty(1L)).thenReturn(faculty);
        when(studentService.getStudentFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Гриффиндор")))
                .andExpect(jsonPath("$.color", is("Красный")));

        mockMvc.perform(get("/student/999/faculty"))
                .andExpect(status().isNotFound());
    }
}
