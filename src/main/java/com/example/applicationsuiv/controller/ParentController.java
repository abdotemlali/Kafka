package com.example.applicationsuiv.controller;


import com.example.applicationsuiv.dto.BusEvent;
import com.example.applicationsuiv.model.Parent;
import com.example.applicationsuiv.service.BusStateService;
import com.example.applicationsuiv.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/view")
public class ParentController {

    @Autowired
    private BusStateService busStateService;

    @Autowired
    private ParentService parentService;

    // Remplace la lecture de bus.json
    @GetMapping("/bus")
    public BusEvent getBusLocation() {
        return busStateService.getDernierePosition();
    }

    // Remplace la lecture de parents.json
    @GetMapping("/parents")
    public List<Parent> getParentsLocations() {
        return parentService.getTousLesParents();
    }
}