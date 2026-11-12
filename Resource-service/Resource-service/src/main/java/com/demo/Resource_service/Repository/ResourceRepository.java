package com.demo.Resource_service.Repository;

import com.demo.Resource_service.Entity.BookableResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<BookableResource, Long> {

    List<BookableResource> findByActiveTrueOrderByNameAsc();

    List<BookableResource> findAllByOrderByNameAsc();

    boolean existsByName(String name);
}
