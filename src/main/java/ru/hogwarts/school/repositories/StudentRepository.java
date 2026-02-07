package ru.hogwarts.school.repositories;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);
    List<Student> findByAgeBetween(int minAge, int maxAge);

    @EntityGraph(attributePaths = {"faculty"})
    Optional<Student> findWithFacultyById(Long id);

    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    Integer countAllStudents();

    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    Double findAverageAge();

    @Query(value = "SELECT * FROM student order by id DESC limit :count", nativeQuery = true)
    List<Student> findLastStudents(int count);
}