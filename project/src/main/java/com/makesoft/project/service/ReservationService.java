package com.makesoft.project.service;
import org.springframework.stereotype.Service;

import com.makesoft.project.repository.ReservationRepository;

@Service
public class ReservationService {
    
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
}
