package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class EnergyMessage {
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("association")
    private String association;
    
    @JsonProperty("kwh")
    private double kwh;
    
    @JsonProperty("datetime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;

    public EnergyMessage(double kwh, LocalDateTime datetime) {
        this.type = "PRODUCER";
        this.association = "COMMUNITY";
        this.kwh = kwh;
        this.datetime = datetime;
    }

    // Leerer Konstruktor für Jackson
    public EnergyMessage() {
    }

    // Getter und Setter
    public String getType() { return type; }
    public String getAssociation() { return association; }
    public double getKwh() { return kwh; }
    public LocalDateTime getDatetime() { return datetime; }
}