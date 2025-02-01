package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);

    @Query(" select i from Item i " +
            "where i.available = true " +
            "  and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "    or upper(i.description) like upper(concat('%', ?1, '%'))) ")
    List<Item> search(String text);

    List<Item> findByRequest_Id(Long requestId);


}
