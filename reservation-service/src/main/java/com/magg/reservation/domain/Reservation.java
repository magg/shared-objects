package com.magg.reservation.domain;

import com.magg.common.model.BaseFields;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation extends BaseFields<Reservation>
{
    @Column(name="start_time")
    private Instant startTime;
    @Column(name="end_time")
    private Instant endTime;
    private String title;
    private String email;
    private String availabilityId;
}
