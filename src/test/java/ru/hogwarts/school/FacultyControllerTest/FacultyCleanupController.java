package ru.hogwarts.school.FacultyControllerTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;

@RestController
@RequestMapping("/faculty")
public class FacultyCleanupController {

    @Autowired
    private FacultyRepository facultyRepository;
    @Autowired
    private StudentRepository studentRepository;

    @DeleteMapping("/clean-all")
    public void cleanAll() {
        facultyRepository.deleteAll();
        studentRepository.deleteAll();
    }
}