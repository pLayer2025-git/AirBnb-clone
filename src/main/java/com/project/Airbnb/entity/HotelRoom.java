package com.project.Airbnb.entity;

import jakarta.persistence.*;

@Entity
public class HotelRoom {

    @Id
    private Long id;

    private String roomNumber;

    private Integer floor;

    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Room category;
}
