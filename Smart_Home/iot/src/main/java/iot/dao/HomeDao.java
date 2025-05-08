package iot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import iot.model.Home;

@Repository
public interface HomeDao extends JpaRepository<Home, Long> {
    // Find by home name

}
