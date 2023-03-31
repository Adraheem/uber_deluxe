package africa.semicolon.uberdeluxe.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Passenger passenger;

    @OneToOne(cascade = CascadeType.ALL)
    private Location origin;

    @OneToOne(cascade = CascadeType.ALL)
    private Location destination;

    private String eta;

    private BigDecimal fare;

    private final LocalDateTime createdAt = LocalDateTime.now();

}
