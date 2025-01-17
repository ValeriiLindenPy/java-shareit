package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND " +
            "(b.start < ?2 AND b.end > ?3)")
    List<Booking> findByItemAndTimeRange(Long id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 order by b.start DESC")
    List<Booking> findAllByBookerId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2 order by b.start DESC")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findPastBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findFutureBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.status = 'WAITING' order by b.start DESC")
    List<Booking> findWaitingBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.status = 'REJECTED' order by b.start DESC")
    List<Booking> findRejectedBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end < ?2 AND b.status = 'CANCELED' ORDER BY b.end DESC")
    Optional<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.start ASC")
    Optional<Booking> findNextBooking(Long itemId, LocalDateTime now);

}
