package ru.hogwarts.school.StudentControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/student";

        ResponseEntity<List<Student>> allStudents = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        if (allStudents.getBody() != null) {
            for (Student student : allStudents.getBody()) {
                restTemplate.delete(baseUrl + "/" + student.getId());
            }
        }

        testStudent = new Student();
        testStudent.setName("Тестовый Студент");
        testStudent.setAge(20);
    }

    @Test
    void testCreateStudent() {
        ResponseEntity<Student> response = restTemplate.postForEntity(
                baseUrl,
                testStudent,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Студент");
        assertThat(response.getBody().getAge()).isEqualTo(20);
    }

    @Test
    void testGetStudentById() {
        Student createdStudent = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/" + createdStudent.getId(),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdStudent.getId());
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Студент");
    }

    @Test
    void testGetStudentByIdNotFound() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                baseUrl + "/9999",
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateStudent() {
        Student createdStudent = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        createdStudent.setName("Обновленное Имя");
        createdStudent.setAge(21);

        HttpEntity<Student> requestEntity = new HttpEntity<>(createdStudent);
        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/" + createdStudent.getId(),
                HttpMethod.PUT,
                requestEntity,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Обновленное Имя");
        assertThat(response.getBody().getAge()).isEqualTo(21);
    }

    @Test
    void testUpdateStudentNotFound() {
        testStudent.setId(9999L);
        HttpEntity<Student> requestEntity = new HttpEntity<>(testStudent);

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.PUT,
                requestEntity,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteStudent() {
        Student createdStudent = restTemplate.postForObject(baseUrl, testStudent, Student.class);

        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/" + createdStudent.getId(),
                HttpMethod.DELETE,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdStudent.getId());

        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdStudent.getId(),
                Student.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteStudentNotFound() {
        ResponseEntity<Student> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.DELETE,
                null,
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllStudents() {
        Student student1 = new Student(null, "Студент 1", 18);
        Student student2 = new Student(null, "Студент 2", 19);

        restTemplate.postForObject(baseUrl, student1, Student.class);
        restTemplate.postForObject(baseUrl, student2, Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetStudentsByAge() {
        Student student18 = new Student(null, "Студент 18 лет", 18);
        Student student18_2 = new Student(null, "Студент 18 лет второй", 18);

        restTemplate.postForObject(baseUrl, student18, Student.class);
        restTemplate.postForObject(baseUrl, student18_2, Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/age/18",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(Student::getAge)
                .containsOnly(18);
    }

    @Test
    void testGetStudentsByAgeBetween() {
        Student student17 = new Student(null, "Студент 17 лет", 17);
        Student student18 = new Student(null, "Студент 18 лет", 18);
        Student student19 = new Student(null, "Студент 19 лет", 19);
        Student student20 = new Student(null, "Студент 20 лет", 20);

        restTemplate.postForObject(baseUrl, student17, Student.class);
        restTemplate.postForObject(baseUrl, student18, Student.class);
        restTemplate.postForObject(baseUrl, student19, Student.class);
        restTemplate.postForObject(baseUrl, student20, Student.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/age/between?minAge=18&maxAge=19",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(Student::getAge)
                .containsExactlyInAnyOrder(18, 19);
    }
}