package com.example.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class Account {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
}
