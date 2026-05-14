package com.project.Airbnb.dto;

import com.project.Airbnb.entity.User;
import com.project.Airbnb.entity.enums.Gender;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
