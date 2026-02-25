package com.bank_app.bank_app.bank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bankCode;

    @Column(nullable = false)
    private String bankName;

    // Simulated account name for name enquiry
    @Column(nullable = false)
    private String simulatedAccountName;

    @Column(nullable = false)
    private boolean active = true;
}
