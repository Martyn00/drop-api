package com.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Table(name = "users")
@AllArgsConstructor
@Getter
@Setter
@Entity
@NoArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RootFolderModel> accessibleRootFolders;

}