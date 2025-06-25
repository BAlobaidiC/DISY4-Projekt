package fhtw.at.energy_community_user.user;

import fhtw.at.energy_community_user.model.EnergyMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class EnergyUsageSender {

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    @Value("${energy.queue}")
    private String queueName;

    public EnergyUsageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void sendUsageMessage() {
        int hour = LocalDateTime.now().getHour();

        // Simulate peak usage in morning (7-9) and evening (17-20)
        double base = (hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 20) ? 0.004 : 0.002;
        double kwh = base + (random.nextDouble() * 0.002);

        EnergyMessage message = new EnergyMessage();
        message.setType("USER");
        message.setAssociation("COMMUNITY");
        message.setKwh(kwh);
        message.setDatetime(LocalDateTime.now());

        rabbitTemplate.convertAndSend(queueName, message);

        System.out.println("Used: " + kwh + " kWh");
    }
}
