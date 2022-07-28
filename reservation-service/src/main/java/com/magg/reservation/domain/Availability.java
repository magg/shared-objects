package com.magg.reservation.domain;

import com.magg.common.model.BaseFields;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "availabilities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Availability extends BaseFields<Availability>
{

    @Column(name="start_time")
    private Instant startTime;
    @Column(name="end_time")
    private Instant endTime;
    private boolean booked;
}
