package ru.hogwarts.school.FacultyControllerTest;

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
class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Faculty testFaculty;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/faculty";

        restTemplate.delete(baseUrl + "/clean-all");

        testFaculty = new Faculty();
        testFaculty.setName("Тестовый Факультет");
        testFaculty.setColor("Синий");

        restTemplate.delete(baseUrl + "/test-cleanup");
    }

    @Test
    void testCreateFaculty() {
        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl,
                testFaculty,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Факультет");
        assertThat(response.getBody().getColor()).isEqualTo("Синий");
    }

    @Test
    void testGetFacultyById() {
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdFaculty.getId());
        assertThat(response.getBody().getName()).isEqualTo("Тестовый Факультет");
    }

    @Test
    void testGetFacultyByIdNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                baseUrl + "/9999",
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateFaculty() {
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        createdFaculty.setName("Обновленное Название");
        createdFaculty.setColor("Красный");

        HttpEntity<Faculty> requestEntity = new HttpEntity<>(createdFaculty);
        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/" + createdFaculty.getId(),
                HttpMethod.PUT,
                requestEntity,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Обновленное Название");
        assertThat(response.getBody().getColor()).isEqualTo("Красный");
    }

    @Test
    void testUpdateFacultyNotFound() {
        testFaculty.setId(9999L);
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(testFaculty);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.PUT,
                requestEntity,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteFaculty() {
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, testFaculty, Faculty.class);

        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/" + createdFaculty.getId(),
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(createdFaculty.getId());

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdFaculty.getId(),
                Faculty.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteFacultyNotFound() {
        ResponseEntity<Faculty> response = restTemplate.exchange(
                baseUrl + "/9999",
                HttpMethod.DELETE,
                null,
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllFaculties() {
        Faculty faculty1 = new Faculty(null, "Гриффиндор", "Красный");
        Faculty faculty2 = new Faculty(null, "Слизерин", "Зеленый");

        restTemplate.postForObject(baseUrl, faculty1, Faculty.class);
        restTemplate.postForObject(baseUrl, faculty2, Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGetFacultiesByColor() {
        Faculty redFaculty1 = new Faculty(null, "Red Faculty 1", "Red");
        Faculty greenFaculty = new Faculty(null, "Green Faculty", "Green");
        Faculty redFaculty2 = new Faculty(null, "Red Faculty 2", "Red");

        restTemplate.postForObject(baseUrl, redFaculty1, Faculty.class);
        restTemplate.postForObject(baseUrl, greenFaculty, Faculty.class);
        restTemplate.postForObject(baseUrl, redFaculty2, Faculty.class);

        ResponseEntity<List<Faculty>> response = restTemplate.exchange(
                baseUrl + "/color/Red",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(Faculty::getColor)
                .containsOnly("Red");
    }

    @Test
    void testSearchFacultiesByNameOrColor() {
        Faculty gryffindor = new Faculty(null, "Гриффиндор", "Красный");
        Faculty slytherin = new Faculty(null, "Слизерин", "Зелёный");
        Faculty ravenclaw = new Faculty(null, "Когтевран", "Голубой");

        restTemplate.postForObject(baseUrl, gryffindor, Faculty.class);
        restTemplate.postForObject(baseUrl, slytherin, Faculty.class);
        restTemplate.postForObject(baseUrl, ravenclaw, Faculty.class);

        ResponseEntity<List<Faculty>> nameSearchResponse = restTemplate.exchange(
                baseUrl + "/search?query=Гриффиндор",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(nameSearchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nameSearchResponse.getBody()).isNotNull();
        assertThat(nameSearchResponse.getBody()).hasSize(1);
        assertThat(nameSearchResponse.getBody().get(0).getName()).isEqualTo("Гриффиндор");

        ResponseEntity<List<Faculty>> colorSearchResponse = restTemplate.exchange(
                baseUrl + "/search?query=Красный",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Faculty>>() {}
        );

        assertThat(colorSearchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(colorSearchResponse.getBody()).isNotNull();
        assertThat(colorSearchResponse.getBody()).hasSize(1);
        assertThat(colorSearchResponse.getBody().get(0).getColor()).isEqualTo("Красный");
    }

    @Test
    void testGetFacultyStudents() {
        Faculty faculty = new Faculty(null, "Test Faculty", "Blue");
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, faculty, Faculty.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/" + createdFaculty.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testGetFacultyStudentsEmpty() {
        Faculty emptyFaculty = new Faculty(null, "Empty Faculty", "Black");
        Faculty createdFaculty = restTemplate.postForObject(baseUrl, emptyFaculty, Faculty.class);

        ResponseEntity<List<Student>> response = restTemplate.exchange(
                baseUrl + "/" + createdFaculty.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }
}
