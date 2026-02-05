package com.fyers.fyerstrading.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fyers.fyerstrading.entity.UnusualOIEvent;

@Repository
public interface UnusualOIEventRepository extends JpaRepository<UnusualOIEvent, Long> {
    // You can add custom methods if needed later
}

