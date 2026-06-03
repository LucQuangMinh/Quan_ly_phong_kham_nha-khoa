package com.example.demo.dto;

import lombok.Data;

@Data
public class ServiceDTO {
    private Long id;
    private String code;
    private String name;
    private String unit;
    private String category;
    private String description;
    private String status;
    private Double price;
}
