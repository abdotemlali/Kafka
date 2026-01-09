package com.example.applicationsuiv.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusEvent {
    private String id;
    private double lat;
    private double lon;
    private String status;
    private String target;
    private int remaining;
    private int counter;
    private boolean penalty;
    private boolean near;
}