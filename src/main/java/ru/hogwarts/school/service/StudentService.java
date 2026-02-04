package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.FacultyRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public StudentService(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    public List<Student> getStudentsByAgeBetween(int minAge, int maxAge) {
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty getStudentFaculty(Long studentId) {
        Student student = getStudentById(studentId);
        return student != null ? student.getFaculty() : null;
    }

    public Student createStudent(Student student) {
        Faculty faculty = student.getFaculty();
        if (faculty != null && faculty.getId() != null) {
            Faculty existingFaculty = facultyRepository.findById(faculty.getId()).orElse(null);
            student.setFaculty(existingFaculty);
        } else if (faculty != null && faculty.getId() == null) {
            faculty = facultyRepository.save(faculty);
            student.setFaculty(faculty);
        }

        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Long id, Student student) {
        Optional<Student> existingStudentOpt = studentRepository.findById(id);
        if (existingStudentOpt.isEmpty()) {
            return null;
        }

        Student existingStudent = existingStudentOpt.get();

        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());

        if (student.getFaculty() != null) {
            Faculty faculty = student.getFaculty();
            if (faculty.getId() != null) {
                Faculty existingFaculty = facultyRepository.findById(faculty.getId()).orElse(null);
                existingStudent.setFaculty(existingFaculty);
            } else {
                Faculty savedFaculty = facultyRepository.save(faculty);
                existingStudent.setFaculty(savedFaculty);
            }
        } else {
            existingStudent.setFaculty(existingStudent.getFaculty());
        }

        student.setId(id);
        return studentRepository.save(existingStudent);
    }

    public Student deleteStudent(Long id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isEmpty()) {
            return null;
        }
        studentRepository.deleteById(id);
        return student.get();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public  List<Student> getStudentsByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Student getStudentWithFaculty(Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null && student.getFaculty() != null) {
            return student;
        }
        throw new NotFoundException("Студент не имеет назначеного факультета.");
    }
}
