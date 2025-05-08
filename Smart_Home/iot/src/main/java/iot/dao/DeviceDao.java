package iot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import iot.model.Device;

@Repository
public interface DeviceDao extends JpaRepository<Device, Long> {
    // Get all devices by home ID
    @Query("SELECT d FROM Device d WHERE d.home.homeId = ?1")
    List<Device> findAllDevicesByHomeId(Long homeId);
}
