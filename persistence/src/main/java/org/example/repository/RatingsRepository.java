package org.example.repository;

import org.example.model.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RatingsRepository extends JpaRepository<Ratings, Integer> {

    Optional<Ratings> findByProperty_IdAndUser_Id(int propertyId, int userId);

    List<Ratings> findByProperty_IdOrderByRatedAtDesc(int propertyId);

    @Query("SELECT COALESCE(AVG(r.rating), 0), COUNT(r) FROM Ratings r WHERE r.property.id = :propertyId")
    List<Object[]> aggregateForProperty(@Param("propertyId") int propertyId);

    @Query("SELECT r.property.id, COALESCE(AVG(r.rating), 0), COUNT(r) FROM Ratings r WHERE r.property.id IN :ids GROUP BY r.property.id")
    List<Object[]> aggregateForPropertyIds(@Param("ids") Collection<Integer> ids);
}
