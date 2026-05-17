package com.project.Airbnb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "hotel"
)
//@ToString is very dangerous
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hotelName;

    private String city;

    @Column(columnDefinition = "TEXT[]")// it stores url of images. actual images are not stored in this database
    private String[] photos;

    @Column(columnDefinition = "TEXT[]") //wifi ,swimming pool, the columnsdefintion tells jpa what this column will store
    private String[] amenities;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Embedded
    private HotelContactInfo contactInfo;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "hotel",fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Room> rooms;

    @ManyToOne
    private User owner;


 }
//@Embedded will bring new colums to this table without creating new table for hotelconteactinfo
//like contactInfo_phonenumber,...