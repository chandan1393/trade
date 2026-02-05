package com.fyers.fyerstrading.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fyers.fyerstrading.entity.IndexCategory;

public interface IndexCategoryRepository extends JpaRepository<IndexCategory, Long> {
    boolean existsBySymbol(String symbol);

	Optional<IndexCategory> findBySymbol(String string);
}
