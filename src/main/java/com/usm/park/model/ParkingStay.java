package com.usm.park.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_stays")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_plate", referencedColumnName = "plate", nullable = false)
    private Vehicle vehicle;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "liquidated", nullable = false)
    private boolean liquidated;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @OneToMany(mappedBy = "parkingStay", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StayMovement> movements = new ArrayList<>();

    public void addMovement(StayMovement movement) {
        movements.add(movement);
        movement.setParkingStay(this);
    }
}
