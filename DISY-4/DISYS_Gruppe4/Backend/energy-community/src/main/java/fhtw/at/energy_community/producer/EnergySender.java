package fhtw.at.energy_community.producer;

import fhtw.at.energy_community.model.EnergyMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class EnergySender {

    @Value("${energy.queue}")
    private String queueName;

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    public EnergySender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void sendEnergyMessage() {
        double kwh = 0.002 + (0.005 * random.nextDouble());

        EnergyMessage message = new EnergyMessage();
        message.setType("PRODUCER");
        message.setAssociation("COMMUNITY");
        message.setKwh(kwh);
        message.setDatetime(LocalDateTime.now());


        rabbitTemplate.convertAndSend(queueName, message);
        System.out.println("Produced: " + kwh + " kWh");
    }
}
