package iot.model;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "humidity")
public class Humidity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long humidityId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "device_id", referencedColumnName = "deviceId")
    private Device device;

    Double value;
    private String unit; // e.g. Percentage, Relative Humidity
    private String status;

    @CreationTimestamp
    private String createdDate;
    @CreationTimestamp
    private String updatedDate;
}
