package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {
    private final Map<Long, Faculty> faculties = new HashMap<>();
    private long counter = 0;

    public Faculty createFaculty(Faculty faculty) {
        faculty.setId(++counter);
        faculties.put(faculty.getId(), faculty);
        return faculty;
    }

    public Faculty getFacultyById(Long id) {
        return faculties.get(id);
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        if (!faculties.containsKey(id)) {
            return null;
        }
        faculty.setId(id);
        faculties.put(id, faculty);
        return faculty;
    }

    public Faculty deleteFaculty(Long id) {
        return faculties.remove(id);
    }

    public List<Faculty> getAllFaculties() {
        return new ArrayList<>(faculties.values());
    }

    public List<Faculty> getFacultiesByColor(String color) {
        return faculties.values().stream()
                .filter(faculty -> faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }
}
