package com.casino;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Cash {
    @Id
    @SequenceGenerator(
            name = "cash_id_sequence",
            sequenceName = "cash_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cash_id_sequence"
    )
    private Integer id;
    private Integer userId;
    private Integer cash;
    private LocalDateTime getCashTimer;
    private LocalDateTime gambleTimer;
}
