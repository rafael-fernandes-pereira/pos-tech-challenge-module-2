package com.github.rafaelfernandes.parquimetro.generate.data.controller;

public record ParkingData(
        Long start_hours_minus,
        Long end_hours_plus
) {
}
