package com.project.Airbnb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id",nullable = false)

    private Hotel hotel;

    @Column(nullable = false)
    private String hotelType;

    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal basePrice;

    @Column(columnDefinition = "TEXT[]")// it stores url of images. actual images are not stored in this database
    private String[] photos;

    @Column(columnDefinition = "TEXT[]") //wifi ,swimming pool, the columnsdefintion tells jpa what this column will store
    private String[] amenities;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer capacity;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
