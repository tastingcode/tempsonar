package study.sonarlab.tempsonar;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final Calculator calculator;

    public GreetingController(Calculator calculator) {
        this.calculator = calculator;
    }

    @GetMapping("/hello")
    public GreetingResponse hello(@RequestParam(defaultValue = "sonarlab") String name) {
        return new GreetingResponse("hello " + name);
    }

    @GetMapping("/add")
    public CalculationResponse add(@RequestParam int left, @RequestParam int right) {
        return new CalculationResponse(left, right, calculator.add(left, right));
    }
}
