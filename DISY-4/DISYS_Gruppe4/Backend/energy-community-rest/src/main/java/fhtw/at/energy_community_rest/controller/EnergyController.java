package fhtw.at.energy_community_rest.controller;

import fhtw.at.energy_community_rest.model.CurrentPercentage;
import fhtw.at.energy_community_rest.model.UsageHour;
import fhtw.at.energy_community_rest.repo.CurrentPercentageRepository;
import fhtw.at.energy_community_rest.repo.UsageHourRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final CurrentPercentageRepository percentageRepository;
    private final UsageHourRepository usageHourRepository;

    public EnergyController(CurrentPercentageRepository percentageRepository, UsageHourRepository usageHourRepository) {
        this.percentageRepository = percentageRepository;
        this.usageHourRepository = usageHourRepository;
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentPercentage> getCurrentPercentage() {
        LocalDateTime hour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        return percentageRepository.findById(hour)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/historical")
    public List<UsageHour> getHistoricalUsage(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return usageHourRepository.findAllByHourBetween(start, end);
    }
}


