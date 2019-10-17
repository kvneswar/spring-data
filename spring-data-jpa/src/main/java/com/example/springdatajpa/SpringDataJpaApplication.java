package com.example.springdatajpa;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class SpringDataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaApplication.class, args);
    }

}

@RestController
@RequiredArgsConstructor
class Controller{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @PostMapping("/user")
    public void saveUser(@RequestBody User user){
        userRepository.save(user);
    }

    @PostMapping("/role")
    public void saveRole(@RequestBody Role role){

        role.setUserList(role.getUserList()
                .stream()
                .map(user -> {
                    User user1 = userRepository.getOne(user.getId());

                    user1.getRoleList().add(role);

                    return user1;
                }
        ).collect(Collectors.toList()));

//        if(role.getUserList().get(0).getId() != null){
//            User user1 = userRepository.getOne(id);
//
//            user1.getRoleList().add(role);
//
//            role.setUserList(Arrays.asList(user1));
//
//            //userRepository.save(user1);
//
//        }

        roleRepository.save(role);
    }



}

@Repository
interface UserRepository extends JpaRepository<User, Long>{

}

@Repository
interface RoleRepository extends JpaRepository<Role, Long>{

}


@Entity
@Data
class User{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roleList;
}

@Entity
@Data
class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "roleList", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<User> userList;
}