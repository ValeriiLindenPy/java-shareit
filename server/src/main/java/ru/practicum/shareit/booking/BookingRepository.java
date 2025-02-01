package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND b.start < ?3 " +
            "AND b.end > ?2")
    List<Booking> findOverlappingBookings(Long id, LocalDateTime start, LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2 order by b.start DESC")
    List<Booking> findCurrentBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.end < ?2 order by b.start DESC")
    List<Booking> findPastBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = ?1 and b.start > ?2 order by b.start DESC")
    List<Booking> findFutureBookings(Long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.end < ?2 AND b.status = 'CANCELED' ORDER BY b.end DESC")
    Optional<Booking> findLastBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.item.id = ?1 AND b.start > ?2 ORDER BY b.start ASC")
    Optional<Booking> findNextBooking(Long itemId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.id = ?1 AND (b.booker.id = ?2 OR b.item.owner.id = ?2)")
    Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);

    // использовал JPA Query Methods там где названия получаются не слишком длинными
    List<Booking> findByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.item.id = ?2 " +
            "AND b.status = ?3 " +
            "AND b.end <= ?4")
    Optional<Booking> findForComments(Long bookerId, Long itemId, BookingStatus status, LocalDateTime now);

    Optional<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime now);

}
