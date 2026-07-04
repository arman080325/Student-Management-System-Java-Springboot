package online.armanportfolio.sms;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.armanportfolio.sms.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired StudentRepository repository;
    @Autowired ObjectMapper mapper;

    @BeforeEach
    void reset() {
        repository.deleteAll();
    }

    private String body(Map<String, Object> m) throws Exception {
        return mapper.writeValueAsString(m);
    }

    private Map<String, Object> validStudent() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", "Test Student");
        m.put("fatherName", "Test Father");
        m.put("course", "B.Tech");
        m.put("branch", "Computer Science & Engineering (CSE)");
        m.put("phone", "9876543210");
        m.put("email", "test@example.com");
        m.put("classXiiPercent", 85.0);
        return m;
    }

    private Long createStudent(Map<String, Object> payload) throws Exception {
        String created = mvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON).content(body(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) mapper.readValue(created, Map.class).get("id")).longValue();
    }

    @Test
    void createStudent_returns201_withGeneratedRollNo() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(body(validStudent())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rollNo", matchesPattern("14\\d{6}")))
                .andExpect(jsonPath("$.name", is("Test Student")));
    }

    @Test
    void createStudent_missingName_returns400() throws Exception {
        Map<String, Object> bad = validStudent();
        bad.remove("name");
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(body(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name", notNullValue()));
    }

    @Test
    void createStudent_invalidPhone_returns400() throws Exception {
        Map<String, Object> bad = validStudent();
        bad.put("phone", "123");
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(body(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.phone", notNullValue()));
    }

    @Test
    void listStudents_returnsPagedEnvelope() throws Exception {
        createStudent(validStudent());
        mvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.page", is(0)));
    }

    @Test
    void listStudents_respectsPageSize() throws Exception {
        for (int i = 0; i < 5; i++) {
            Map<String, Object> s = validStudent();
            s.put("name", "Student " + i);
            createStudent(s);
        }
        mvc.perform(get("/api/students").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)));
    }

    @Test
    void listStudents_sortsByRequestedField() throws Exception {
        Map<String, Object> a = validStudent(); a.put("name", "Zed"); createStudent(a);
        Map<String, Object> b = validStudent(); b.put("name", "Amy"); createStudent(b);

        mvc.perform(get("/api/students").param("sortBy", "name").param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("Amy")))
                .andExpect(jsonPath("$.content[1].name", is("Zed")));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mvc.perform(get("/api/students/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStudent_removesIt() throws Exception {
        Long id = createStudent(validStudent());
        mvc.perform(delete("/api/students/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students")).andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void bulkDelete_removesOnlyGivenIds() throws Exception {
        Long keep = createStudent(validStudent());
        Long a = createStudent(validStudent());
        Long b = createStudent(validStudent());

        mvc.perform(delete("/api/students/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(List.of(a, b))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(2)));

        mvc.perform(get("/api/students/" + keep)).andExpect(status().isOk());
        mvc.perform(get("/api/students/" + a)).andExpect(status().isNotFound());
    }

    @Test
    void stats_returnsAggregateFigures() throws Exception {
        Map<String, Object> a = validStudent(); a.put("classXiiPercent", 90.0); createStudent(a);
        Map<String, Object> b = validStudent(); b.put("classXiiPercent", 80.0); createStudent(b);

        mvc.perform(get("/api/students/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents", is(2)))
                .andExpect(jsonPath("$.averageClassXiiPercent", is(85.0)))
                .andExpect(jsonPath("$.topPerformer.classXiiPercent", is(90.0)));
    }

    @Test
    void export_returnsCsvWithHeaderRow() throws Exception {
        createStudent(validStudent());
        mvc.perform(get("/api/students/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("students.csv")))
                .andExpect(content().string(startsWith("Roll No,Name,Father's Name")));
    }
}
