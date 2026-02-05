package com.fyers.fyerstrading.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fyers.fyerstrading.entity.OptionInstrument;

public interface OptionInstrumentRepository extends JpaRepository<OptionInstrument, String> {

	@Query(" SELECT DISTINCT o.expiry FROM OptionInstrument o WHERE o.underlying = :symbol AND o.expiry >= :today ORDER BY o.expiry ASC ")
	Optional<LocalDate> findNearestExpiry(@Param("symbol") String symbol, @Param("today") LocalDate today);

	Optional<OptionInstrument> findByUnderlyingAndExpiryAndStrikeAndOptionType(String underlying, LocalDate expiry,
			int strike, String optionType);
}
