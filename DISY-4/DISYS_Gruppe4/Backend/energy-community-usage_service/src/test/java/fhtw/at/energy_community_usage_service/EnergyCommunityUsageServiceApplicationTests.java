
package fhtw.at.energy_community_usage_service;

import fhtw.at.energy_community_usage_service.entity.EnergyMessage;
import fhtw.at.energy_community_usage_service.entity.UsageHour;
import fhtw.at.energy_community_usage_service.repo.UsageHourRepository;
import fhtw.at.energy_community_usage_service.service.UsageMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyCommunityUsageServiceApplicationTests {

	@Mock
	private UsageHourRepository usageHourRepository;

	private UsageMessageListener messageListener;

	@BeforeEach
	void setup() {
		messageListener = new UsageMessageListener(usageHourRepository);
	}

	@Test
	void testeProduzentenNachricht() {
		// Arrange
		LocalDateTime jetzt = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
		EnergyMessage nachricht = new EnergyMessage();
		nachricht.setType("PRODUCER");
		nachricht.setDatetime(jetzt);
		nachricht.setKwh(10.0);

		// Act
		messageListener.handleMessage(nachricht);

		// Assert
		verify(usageHourRepository).save(argThat(usageHour ->
				usageHour.getHour().equals(jetzt) &&
						usageHour.getCommunityProduced() == 10.0 &&
						usageHour.getCommunityUsed() == 0.0 &&
						usageHour.getGridUsed() == 0.0
		));
	}

	@Test
	void testeVerbraucherNachrichtMitVerfügbarerEnergie() {
		// Arrange
		LocalDateTime jetzt = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
		UsageHour bestehendeStunde = new UsageHour();
		bestehendeStunde.setHour(jetzt);
		bestehendeStunde.setCommunityProduced(20.0);
		bestehendeStunde.setCommunityUsed(5.0);
		when(usageHourRepository.findById(jetzt)).thenReturn(Optional.of(bestehendeStunde));

		EnergyMessage nachricht = new EnergyMessage();
		nachricht.setType("USER");
		nachricht.setDatetime(jetzt);
		nachricht.setKwh(10.0);

		// Act
		messageListener.handleMessage(nachricht);

		// Assert
		verify(usageHourRepository).save(argThat(usageHour ->
				usageHour.getHour().equals(jetzt) &&
						usageHour.getCommunityProduced() == 20.0 &&
						usageHour.getCommunityUsed() == 15.0 &&
						usageHour.getGridUsed() == 0.0
		));
	}

	@Test
	void testeVerbraucherNachrichtOhneVerfügbareEnergie() {
		// Arrange
		LocalDateTime jetzt = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
		UsageHour bestehendeStunde = new UsageHour();
		bestehendeStunde.setHour(jetzt);
		bestehendeStunde.setCommunityProduced(20.0);
		bestehendeStunde.setCommunityUsed(20.0);
		when(usageHourRepository.findById(jetzt)).thenReturn(Optional.of(bestehendeStunde));

		EnergyMessage nachricht = new EnergyMessage();
		nachricht.setType("USER");
		nachricht.setDatetime(jetzt);
		nachricht.setKwh(10.0);

		// Act
		messageListener.handleMessage(nachricht);

		// Assert
		verify(usageHourRepository).save(argThat(usageHour ->
				usageHour.getHour().equals(jetzt) &&
						usageHour.getCommunityProduced() == 20.0 &&
						usageHour.getCommunityUsed() == 20.0 &&
						usageHour.getGridUsed() == 10.0
		));
	}

	@Test
	void testeVerbraucherNachrichtWennKeineStundeVorhanden() {
		// Arrange
		LocalDateTime jetzt = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
		when(usageHourRepository.findById(jetzt)).thenReturn(Optional.empty());

		EnergyMessage nachricht = new EnergyMessage();
		nachricht.setType("USER");
		nachricht.setDatetime(jetzt);
		nachricht.setKwh(10.0);

		// Act
		messageListener.handleMessage(nachricht);

		// Assert
		verify(usageHourRepository).save(argThat(usageHour ->
				usageHour.getHour().equals(jetzt) &&
						usageHour.getCommunityProduced() == 0.0 &&
						usageHour.getCommunityUsed() == 0.0 &&
						usageHour.getGridUsed() == 10.0
		));
	}
}