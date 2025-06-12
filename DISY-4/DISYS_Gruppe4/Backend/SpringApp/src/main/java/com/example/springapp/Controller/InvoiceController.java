package com.example.springapp.Controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/energy")
public class InvoiceController {

    @GetMapping("/current")
    public Map<String, Double> getCurrent() {
        Map<String, Double> result = new HashMap<>();
        result.put("communityDepleted", 100.0);
        result.put("gridPortion", 5.63);
        return result;
    }

    @GetMapping("/historical")
    public List<Map<String, Object>> getHistorical(@RequestParam String start, @RequestParam String end) {
        Map<String, Object> data = new HashMap<>();
        data.put("hour", "2025-01-10T14:00:00");
        data.put("communityDepleted", 100.0);
        data.put("gridPortion", 5.63);
        return List.of(data);
    }
}
// Kommentar von Ali zum Testen

