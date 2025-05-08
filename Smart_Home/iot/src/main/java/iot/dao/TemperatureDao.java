package iot.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iot.model.Temperature;
import java.util.List;

@Repository
public interface TemperatureDao extends JpaRepository<Temperature, Long> {
    // Get latest temperature by deviceId
    @Query(value = "SELECT * FROM temperature t WHERE t.device_id = ?1 ORDER BY t.created_date DESC LIMIT 1", nativeQuery = true)
    Temperature getLatesTemperatureByDeviceId(Long deviceId); // Assuming deviceId is a field in the

    // Get 24 degrees
    @Query(value = "SELECT * FROM temperature t WHERE t.created_date LIKE CONCAT(?1, '%') AND DATE_FORMAT(t.created_date, '%i:%s') = '00:00' ORDER BY t.created_date ASC", nativeQuery = true)
    List<Temperature> findByCreatedDateOrderByTime(String createdDate);

}
