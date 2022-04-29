package com.example.entity;

import lombok.*;

import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Table(name = "account")
public class Account {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
}
