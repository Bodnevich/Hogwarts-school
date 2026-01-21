package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@RestController
@RequestMapping("/faculty")
@Tag(name = "Faculty Controller", description = "Управление факультетами")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    @Operation(
            summary = "Создать новый факультет",
            description = "Создает новый факультет в системе"
    )
    @ApiResponse(responseCode = "201", description = "Факультет успешно создан")
    public ResponseEntity<Faculty> createFaculty(@RequestBody Faculty faculty) {
        Faculty createdFaculty = facultyService.createFaculty(faculty);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFaculty);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить факультет по ID",
            description = "Возвращает факультет по указанному идентификатору"
    )
    @ApiResponse(responseCode = "200", description = "Факультет найден")
    @ApiResponse(responseCode = "404", description = "Факультет не найден")
    public ResponseEntity<Faculty> getFaculty(@Parameter(description = "ID факультета") @PathVariable Long id) {
        Faculty faculty = facultyService.getFacultyById(id);
        if (faculty == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить данные факультета",
            description = "Обновляет информацию о существующем факультете"
    )
    @ApiResponse(responseCode = "200", description = "Данные факультета обновлены")
    @ApiResponse(responseCode = "404", description = "Факультет не найден")
    public ResponseEntity<Faculty> updateFaculty(@Parameter(description = "ID факультета") @PathVariable Long id, @RequestBody Faculty faculty) {
        Faculty updatedFaculty = facultyService.updateFaculty(id, faculty);
        if (updatedFaculty == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedFaculty);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить факультет",
            description = "Удаляет факультет по указанному ID"
    )
    @ApiResponse(responseCode = "200", description = "Факультет успешно удален")
    @ApiResponse(responseCode = "404", description = "Факультет не найден")
    public ResponseEntity<Faculty> deleteFaculty(@Parameter(description = "ID факультета") @PathVariable Long id) {
        Faculty deletedFaculty = facultyService.deleteFaculty(id);
        if (deletedFaculty == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(deletedFaculty);
    }

    @GetMapping
    @Operation(
            summary = "Получить все факультеты",
            description = "Возвращает список всех факультетов"
    )
    public ResponseEntity<List<Faculty>> getAllFaculties() {
        List<Faculty> faculties = facultyService.getAllFaculties();
        return ResponseEntity.ok(faculties);
    }

    @GetMapping("/color/{color}")
    @Operation(
            summary = "Фильтрация факультетов по цвету",
            description = "Возвращает список факультетов указанного цвета"
    )
    public ResponseEntity<List<Faculty>> getFacultiesByColor(@Parameter(description = "Цвет факультета") @PathVariable String color) {
        List<Faculty> faculties = facultyService.getFacultiesByColor(color);
        return ResponseEntity.ok(faculties);
    }
}
