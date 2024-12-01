package com.cloudbees.trainapi.ticketing.domain.repository;

import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {


    /**
     * Finds the section with the fewest tickets sold from the RECEIPT table. If there is only one
     * receipt in the entire table, it defaults to section 'B'. Otherwise, it returns the section
     * with the minimum count of tickets. If there are multiple sections with the same count,
     * the section with the smallest ID is returned.
     *
     * @return the section identifier with the fewest number of tickets. If no sections are found
     *         or the database is empty, it defaults to 'A'.
     */
    @Query(value = "SELECT CASE WHEN (SELECT COUNT(*) FROM RECEIPT) = 1 THEN 'B' "
            + "ELSE COALESCE(MIN(section), 'A') END "
            + "FROM ("
            + "SELECT section FROM RECEIPT "
            + "GROUP BY section "
            + "ORDER BY COUNT(section) ASC, MIN(id) ASC "
            + "LIMIT 1"
            + ") AS result", nativeQuery = true)
    String findSectionWithFewestTickets();

    /**
     * Finds and returns a list of occupied seat numbers within a specific section.
     *
     * @param section the section identifier for which to find the occupied seats. This parameter
     *                must match a valid section value in the database.
     * @return a list of integers representing the occupied seat numbers in the specified section,
     *         ordered in ascending order. If no seats are occupied, an empty list is returned.
     */
    @Query(value = "SELECT SEAT_NUMBER FROM RECEIPT "
            + "WHERE section = :section "
            + "ORDER BY SEAT_NUMBER ASC", nativeQuery = true)
    List<Integer> findOccupiedSeatsBySection(@Param("section") String section);

    default Optional<Integer> findFirstFreeSeatNumber(String section) {
        List<Integer> occupiedSeats = findOccupiedSeatsBySection(section);

        for (int i = 1; i <= 10; i++) {
            if (!occupiedSeats.contains(i)) {
                return Optional.of(i); // Devuelve el primer asiento libre
            }
        }

        return Optional.empty(); // Si todos los asientos están ocupados
    }

}
