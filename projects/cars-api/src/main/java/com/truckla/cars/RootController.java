package com.truckla.cars;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class RootController {
    @GetMapping
    public void doNothing() { }
}
