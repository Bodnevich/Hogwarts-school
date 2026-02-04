package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/student")
@Tag(name = "Student Controller", description = "Управление студентами")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/age/between")
    @Operation(
            summary = "Получить студентов по возрасту",
            description = "Возвращает список студентов, возраст между min и max")
    public ResponseEntity<List<Student>> getStudentsByAgeBetween(@Parameter(description = "Минимальный возраст") @RequestParam int minAge,
                                                                 @Parameter(description = "Максимальный возраст") @RequestParam int maxAge)
    {
        List<Student> students = studentService.getStudentsByAgeBetween(minAge, maxAge);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getStudentFaculty(@Parameter(description = "ID студента") @PathVariable Long id) {
        Faculty faculty = studentService.getStudentFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PostMapping
    @Operation(
            summary = "Создать нового студента",
            description = "Создает нового студента в системе"
    )
    @ApiResponse(responseCode = "201", description = "Студент успешно создан")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получить студента по ID",
            description = "Возвращает студента по указанному идентификатору"
    )
    @ApiResponse(responseCode = "200", description = "Студент найден")
    @ApiResponse(responseCode = "404", description = "Студент не найден")
    public ResponseEntity<Student> getStudent(@Parameter(description = "ID студента") @PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновить данные студента",
            description = "Обновляет информацию о существующем студенте"
    )
    @ApiResponse(responseCode = "200", description = "Данные студента обновлены")
    @ApiResponse(responseCode = "404", description = "Студент не найден")
    public ResponseEntity<Student> updateStudent(@Parameter(description = "ID студента") @PathVariable Long id, @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        if (updatedStudent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удалить студента",
            description = "Удаляет студента по указанному ID"
    )
    @ApiResponse(responseCode = "200", description = "Студент успешно удален")
    @ApiResponse(responseCode = "404", description = "Студент не найден")
    public ResponseEntity<Student> deleteStudent(@Parameter(description = "ID студента") @PathVariable Long id) {
        Student deletedStudent = studentService.deleteStudent(id);
        if (deletedStudent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(deletedStudent);
    }

    @GetMapping
    @Operation(
            summary = "Получить всех студентов",
            description = "Возвращает список всех студентов"
    )
    public ResponseEntity<List<Student>> getAllStudent() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/age/{age}")
    @Operation(
            summary = "Фильтрация студентов по возрасту",
            description = "Возвращает список студентов указанного возраста"
    )
    public ResponseEntity<List<Student>> getStudentsByAge(@Parameter(description = "Возраст студентов") @PathVariable int age) {
        List<Student> students = studentService.getStudentsByAge(age);
        return ResponseEntity.ok(students);
    }
}
