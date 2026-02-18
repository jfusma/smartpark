package com.usm.park.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parking_spots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpot {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "has_charger", nullable = false)
    private boolean hasCharger;

    @Column(name = "occupied", nullable = false)
    private boolean occupied;
}
