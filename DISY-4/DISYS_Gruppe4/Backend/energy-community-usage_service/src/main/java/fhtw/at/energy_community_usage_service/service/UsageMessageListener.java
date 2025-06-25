package fhtw.at.energy_community_usage_service.service;

import fhtw.at.energy_community_usage_service.entity.EnergyMessage;
import fhtw.at.energy_community_usage_service.entity.UsageHour;
import fhtw.at.energy_community_usage_service.repo.UsageHourRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsageMessageListener {

    private final UsageHourRepository usageHourRepository;

    public UsageMessageListener(UsageHourRepository usageHourRepository) {
        this.usageHourRepository = usageHourRepository;
    }

    @RabbitListener(queues = "energy.queue")
    public void handleMessage(EnergyMessage message) {
        LocalDateTime hour = message.getDatetime().withMinute(0).withSecond(0).withNano(0);

        UsageHour usage = usageHourRepository.findById(hour).orElseGet(() -> {
            UsageHour u = new UsageHour();
            u.setHour(hour);
            u.setCommunityProduced(0);
            u.setCommunityUsed(0);
            u.setGridUsed(0);
            return u;
        });

        if ("PRODUCER".equals(message.getType())) {
            usage.setCommunityProduced(usage.getCommunityProduced() + message.getKwh());
        } else if ("USER".equals(message.getType())) {
            double remaining = usage.getCommunityProduced() - usage.getCommunityUsed();
            if (remaining >= message.getKwh()) {
                usage.setCommunityUsed(usage.getCommunityUsed() + message.getKwh());
            } else {
                double usedFromCommunity = Math.max(remaining, 0);
                double usedFromGrid = message.getKwh() - usedFromCommunity;

                usage.setCommunityUsed(usage.getCommunityUsed() + usedFromCommunity);
                usage.setGridUsed(usage.getGridUsed() + usedFromGrid);
            }
        }

        usageHourRepository.save(usage);
    }
}
