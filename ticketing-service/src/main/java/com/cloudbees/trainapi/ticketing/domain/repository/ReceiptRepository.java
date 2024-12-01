package com.cloudbees.trainapi.ticketing.domain.repository;

import com.cloudbees.trainapi.ticketing.application.dto.output.UserSeatOutputDTO;
import com.cloudbees.trainapi.ticketing.domain.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {


    /**
     * Determines the section with the fewest tickets in the database.
     * The decision is based on the following logic:
     * - If there are no tickets, defaults to section 'A'.
     * - If all tickets belong to section 'A', chooses section 'B'.
     * - If all tickets belong to section 'B', chooses section 'A'.
     * - If section 'A' has fewer tickets than section 'B', returns 'A'.
     * - Otherwise, returns 'B'.
     *
     * @return a String representing the section ('A' or 'B') with the fewest tickets.
     */
    @Query(value = "SELECT CASE " +
            "WHEN COUNT(*) = 0 THEN 'A' " +
            "WHEN SUM(CASE WHEN section = 'A' THEN 1 ELSE 0 END) = COUNT(*) THEN 'B' " +
            "WHEN SUM(CASE WHEN section = 'B' THEN 1 ELSE 0 END) = COUNT(*) THEN 'A' " +
            "WHEN SUM(CASE WHEN section = 'A' THEN 1 ELSE 0 END) < SUM(CASE WHEN section = 'B' THEN 1 ELSE 0 END) THEN 'A' " +
            "ELSE 'B' END " +
            "FROM RECEIPT", nativeQuery = true)
    String findSectionWithFewestTickets();

    /**
     * Finds and returns a list of occupied seat numbers within a specific section.
     *
     * @param section the section identifier for which to find the occupied seats. This parameter
     *                must match a valid section value in the database.
     * @return a list of integers representing the occupied seat numbers in the specified section,
     * ordered in ascending order. If no seats are occupied, an empty list is returned.
     */
    @Query(value = "SELECT SEAT_NUMBER FROM RECEIPT "
            + "WHERE section = :section "
            + "ORDER BY SEAT_NUMBER ASC", nativeQuery = true)
    List<Integer> findOccupiedSeatsBySection(@Param("section") String section);

    default OptionalInt findFirstFreeSeatNumber(String section) {
        List<Integer> occupiedSeats = findOccupiedSeatsBySection(section);

        return IntStream.rangeClosed(1, 10) // Generar números del 1 al 10
                .filter(seat -> !occupiedSeats.contains(seat)) // Filtrar aquellos que no están ocupados
                .findFirst();
    }


    boolean existsBySectionAndSeatNumber(String section, Integer seatNumber);

    @Query("SELECT new com.cloudbees.trainapi.ticketing.application.dto.output.UserSeatOutputDTO(r.name, r.surname, r.email, r.section, r.seatNumber) " +
            "FROM Receipt r WHERE r.section = :section")
    List<UserSeatOutputDTO> findAllBySection(@Param("section") String section);

}
