package com.seva.seva_mobile_version.entity;


import com.seva.seva_mobile_version.enums.SlotStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "service_center_slot" )
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ServiceCenterSlot {


    @Id
    @GeneratedValue ( strategy = GenerationType.UUID)
    @Column( name = "slot_id")

    private String id ;

    @Column( name = "starting_at" , nullable = false)
    private LocalDateTime starting_at ;
    @Column( name = "duration" , nullable = false)
    private LocalDateTime duration;



    @Enumerated( EnumType.STRING)
    @Column( name = "status" , nullable = false)

    private SlotStatus status= SlotStatus.OPEN;

}
