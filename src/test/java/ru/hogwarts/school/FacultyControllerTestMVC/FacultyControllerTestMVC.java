package ru.hogwarts.school.FacultyControllerTestMVC;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTestMVC {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    @Test
    void testCreateFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Гриффиндор")))
                .andExpect(jsonPath("$.color", is("Красный")));
    }

    @Test
    void testGetFacultyById() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        when(facultyService.getFacultyById(1L)).thenReturn(faculty);
        when(facultyService.getFacultyById(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Гриффиндор")))
                .andExpect(jsonPath("$.color", is("Красный")));

        mockMvc.perform(get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");
        Faculty updatedFaculty = new Faculty(1L, "Гриффиндор Обновленный", "Бордовый");

        when(facultyService.updateFaculty(eq(1L), any(Faculty.class))).thenReturn(updatedFaculty);
        when(facultyService.updateFaculty(eq(999L), any(Faculty.class))).thenReturn(null);

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Гриффиндор Обновленный")))
                .andExpect(jsonPath("$.color", is("Бордовый")));

        mockMvc.perform(put("/faculty/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(faculty)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteFaculty() throws Exception {
        Faculty faculty = new Faculty(1L, "Гриффиндор", "Красный");

        when(facultyService.deleteFaculty(1L)).thenReturn(faculty);
        when(facultyService.deleteFaculty(999L)).thenReturn(null);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Гриффиндор")));

        mockMvc.perform(delete("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllFaculties() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Гриффиндор", "Красный"),
                new Faculty(2L, "Слизерин", "Зеленый")
        );

        when(facultyService.getAllFaculties()).thenReturn(faculties);

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Гриффиндор")))
                .andExpect(jsonPath("$[1].name", is("Слизерин")));
    }

    @Test
    void testGetFacultiesByColor() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Гриффиндор", "Красный"),
                new Faculty(3L, "Хаффлпафф", "Желтый")
        );

        when(facultyService.getFacultiesByColor("Красный")).thenReturn(
                Collections.singletonList(new Faculty(1L, "Гриффиндор", "Красный"))
        );

        mockMvc.perform(get("/faculty/color/Красный"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].color", is("Красный")));
    }

    @Test
    void testSearchFacultiesByNameOrColor() throws Exception {
        List<Faculty> faculties = Arrays.asList(
                new Faculty(1L, "Гриффиндор", "Красный"),
                new Faculty(3L, "Когтевран", "Синий")
        );

        when(facultyService.searchFacultiesByNameOrColor("Гриффиндор")).thenReturn(
                Collections.singletonList(new Faculty(1L, "Гриффиндор", "Красный"))
        );

        mockMvc.perform(get("/faculty/search")
                        .param("query", "Гриффиндор"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Гриффиндор")));
    }

    @Test
    void testGetFacultyStudents() throws Exception {
        List<Student> students = Arrays.asList(
                new Student(1L, "Гарри Поттер", 18),
                new Student(2L, "Гермиона Грейнджер", 18)
        );

        when(facultyService.getFacultyStudents(1L)).thenReturn(students);

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Гарри Поттер")))
                .andExpect(jsonPath("$[1].name", is("Гермиона Грейнджер")));
    }
}
