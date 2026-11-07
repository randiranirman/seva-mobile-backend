package com.seva.seva_mobile_version.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table( name = "service_center"
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCenter {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID)
    @Column( name = "service_center_id")
    private String id  ;


    @OneToOne
    @JoinColumn( name = "owner_id" , nullable = false, unique = true)
    private User owner ;


    @Column( name = "address" , nullable = false)
    private String address ;


    @Column ( name = "store_name" , nullable = false)

    private String storeName ;

    @Column( name ="area", nullable = false)
    private String area ;


    @Column( name = "owner_name" , nullable = false)
    private String ownerName ;


    @Column( name = "owner_contact_number" , nullable = false)
    private String ownerContactNumber ;


    @Column( name = "business_registration_number" , nullable = false, unique = true)
    private String businessRegistrationNumber ;


    @Column( name = "latitude" , nullable = false)
    private Double latitude ;


    @Column( name = "longitude" , nullable = false)
    private Double longitude ;




    @Column( nullable = false , name = "created_at ")
    private LocalDateTime createdAt  ;
    @Column( nullable = false, name ="updated_at" )
    private LocalDateTime updatedAt ;





}
