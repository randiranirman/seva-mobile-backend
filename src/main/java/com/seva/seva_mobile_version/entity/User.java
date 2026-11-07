package com.seva.seva_mobile_version.entity;


import com.seva.seva_mobile_version.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table( name = "users")


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column( name = "id" , nullable = false, unique = true)

    private  String id ;


    @Column( name = "email" , nullable = false, unique = true)
    private String email;


    @Enumerated(EnumType.STRING)
    @Column( name = "role" , nullable = false)
    private Role role;


    @Column( name = "mobile_number" , unique = true, nullable = false)
    private String mobileNumber;


    @Column( name = "username" , unique = true)
    private String   username ;


    @Column( name =  "passwowrd" , nullable = false)
    private String password  ;


    @Column( name = "first_name" , nullable = true)
    private String firstName ;

    @Column( name = "last_name" , nullable = true)
    private String lastName  ;


    @Column( name = "email_verified" , nullable = false)
    private boolean emailVerified;


    @Column( name = "verification_code" )
    private String verificationCode;


    @Column( name = "verification_code_expires_at" )
    private LocalDateTime verificationCodeExpiresAt;


    @Column( name = "created_at" , nullable = false)
    private LocalDateTime createdAt ;


    @Column( name = "updated_at" , nullable = false)
    private LocalDateTime  updatedAt ;





}
