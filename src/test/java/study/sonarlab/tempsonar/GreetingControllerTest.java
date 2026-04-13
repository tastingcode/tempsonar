package study.sonarlab.tempsonar;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GreetingController.class)
@Import(Calculator.class)
class GreetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloReturnsMessage() throws Exception {
        mockMvc.perform(get("/hello").param("name", "jenkins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("hello jenkins"));
    }

    @Test
    void addReturnsCalculation() throws Exception {
        mockMvc.perform(get("/add").param("left", "4").param("right", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(10));
    }
}
