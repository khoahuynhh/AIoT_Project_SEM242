package iot.service;

import iot.model.Device;
import iot.model.Home;
import iot.model.User;

public interface PersonalInfoService {

    // --- CRUD operations for User --- //
    public User getUserById(Long userId);

    public void createUser(User user, Long homeId);

    public void updateUser(User user);

    public void deleteUser(Long userId);

    public String verifyAccount(String email, String password);

    // --- CRUD operations for Home --- //
    public Home getHomeById(Long homeId);

    public void createHome(Home home);

    public void updateHome(Home home);

    public void deleteHome(Long homeId);

    // --- CRUD operations for Device --- //
    public Device getDeviceById(Long deviceId);

    public void createDevice(Device device, Long homeId);

    public void updateDevice(Device device);

    public void deleteDevice(Long deviceId);

}