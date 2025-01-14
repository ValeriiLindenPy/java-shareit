package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND " +
            "(b.start < ?2 AND b.end > ?3)")
    List<Booking> findByItemAndTimeRange(Long id, LocalDateTime start, LocalDateTime end);
}
