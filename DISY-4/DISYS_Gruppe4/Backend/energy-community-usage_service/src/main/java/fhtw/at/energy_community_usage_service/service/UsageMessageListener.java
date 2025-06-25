package fhtw.at.energy_community_usage_service.service;

import fhtw.at.energy_community_usage_service.entity.EnergyMessage;
import fhtw.at.energy_community_usage_service.entity.UsageHour;
import fhtw.at.energy_community_usage_service.repo.UsageHourRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Service-Klasse zur Verarbeitung von Nachrichten aus der RabbitMQ-Warteschlange
@Service
public class UsageMessageListener {

    // Repository für den Zugriff auf die UsageHour-Entitäten
    private final UsageHourRepository usageHourRepository;

    // Konstruktor zur Initialisierung des Repositories
    public UsageMessageListener(UsageHourRepository usageHourRepository) {
        this.usageHourRepository = usageHourRepository;
    }

    // Listener für Nachrichten aus der Warteschlange "energy.queue"
    @RabbitListener(queues = "energy.queue")
    public void handleMessage(EnergyMessage message) {
        // Rundet den Zeitstempel der Nachricht auf die volle Stunde ab
        LocalDateTime hour = message.getDatetime().withMinute(0).withSecond(0).withNano(0);

        // Holt oder erstellt eine UsageHour-Entität für die abgerundete Stunde
        UsageHour usage = usageHourRepository.findById(hour).orElseGet(() -> {
            UsageHour u = new UsageHour();
            u.setHour(hour);
            u.setCommunityProduced(0);
            u.setCommunityUsed(0);
            u.setGridUsed(0);
            return u;
        });

        // Verarbeitung der Nachrichten basierend auf dem Typ (PRODUCER oder USER)
        if ("PRODUCER".equals(message.getType())) {
            // Fügt die produzierte Energie hinzu
            usage.setCommunityProduced(usage.getCommunityProduced() + message.getKwh());
        } else if ("USER".equals(message.getType())) {
            // Berechnet den verbleibenden Energiebedarf der Gemeinschaft
            double remaining = usage.getCommunityProduced() - usage.getCommunityUsed();
            if (remaining >= message.getKwh()) {
                // Energie direkt aus der Gemeinschaft decken
                usage.setCommunityUsed(usage.getCommunityUsed() + message.getKwh());
            } else {
                // Deckt Energie teilweise aus der Gemeinschaft und teilweise aus dem Netz
                double usedFromCommunity = Math.max(remaining, 0);
                double usedFromGrid = message.getKwh() - usedFromCommunity;

                usage.setCommunityUsed(usage.getCommunityUsed() + usedFromCommunity);
                usage.setGridUsed(usage.getGridUsed() + usedFromGrid);
            }
        }

        // Speichert die aktualisierte UsageHour-Entität in der Datenbank
        usageHourRepository.save(usage);
    }
}
