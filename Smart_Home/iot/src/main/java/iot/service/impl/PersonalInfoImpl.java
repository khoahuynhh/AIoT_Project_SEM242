package iot.service.impl;

import iot.service.PersonalInfoService;
import iot.model.Device;
import iot.model.Home;
import iot.model.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import iot.config.JwtUtils;
import iot.dao.DeviceDao;
import iot.dao.HomeDao;
import iot.dao.UserDao;

@Service
public class PersonalInfoImpl implements PersonalInfoService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private HomeDao homeDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private JwtUtils jwtUtils;

    // Logging
    private final Logger logger = LogManager.getLogger(PersonalInfoImpl.class);

    @Override
    public User getUserById(Long userId) {
        User user = userDao.findById(userId).orElse(null);
        if (user == null) {
            logger.error("User not found");
            return null;
        } else {
            logger.info("Fetch user successfully with username:" + userId);
            return user;
        }
    }

    @Override
    public void createUser(User user, Long homeId) {
        try {
            // check Home is exist
            Home home = homeDao.findById(homeId).orElse(null);
            if (home == null) {
                logger.error("Home not found");
                return;
            } else {
                logger.info("Fetch home successfully with homeId:" + homeId);
            }
            user.setHome(home);
            userDao.save(user);
            logger.info("Create user successfully with username:" + user.getUsername());
        } catch (Exception e) {
            logger.error("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public void updateUser(User user) {
        userDao.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            userDao.deleteById(userId);
            logger.info("Delete user successfully with username:" + userId);
        } catch (Exception e) {
            logger.error("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public String verifyAccount(String username, String password) {
        // Check if user exists
        User user = userDao.findByUsernameAndPassword(username, password);
        if (user == null) {
            logger.info("User not found");
            return null;
        } else {
            logger.info("Fetch user successfully with username:" + username);
            return jwtUtils.generateToken(username);
        }
        // Generate JWT token
    }

    @Override
    public Home getHomeById(Long homeId) {
        Home home = homeDao.findById(homeId).orElse(null);
        if (home == null) {
            logger.error("Home not found");
            return null;
        } else {
            logger.info("Fetch home successfully with homeId:" + homeId);
            return home;
        }
    }

    @Override
    public void createHome(Home home) {
        try {
            homeDao.save(home);
            logger.info("Create home successfully with homeId:" + home.getHomeId());
        } catch (Exception e) {
            logger.error("Error creating home: " + e.getMessage());
        }
    }

    @Override
    public void updateHome(Home home) {
        try {
            homeDao.save(home);
            logger.info("Update home successfully with homeId:" + home.getHomeId());
        } catch (Exception e) {
            logger.error("Error updating home: " + e.getMessage());
        }
    }

    @Override
    public void deleteHome(Long homeId) {
        try {
            homeDao.deleteById(homeId);
            logger.info("Delete home successfully with homeId:" + homeId);
        } catch (Exception e) {
            logger.error("Error deleting home: " + e.getMessage());
        }
    }

    // Device
    @Override
    public Device getDeviceById(Long deviceId) {
        Device device = deviceDao.findById(deviceId).orElse(null);
        if (device == null) {
            logger.error("Device not found");
            return null;
        } else {
            logger.info("Fetch device successfully with deviceId:" + deviceId);
            return device;
        }
    }

    @Override
    public void createDevice(Device device, Long homeId) {
        try {
            // check Home is exist
            Home home = homeDao.findById(homeId).orElse(null);
            if (home == null) {
                logger.error("Home not found");
                return;
            } else {
                logger.info("Fetch home successfully with homeId:" + homeId);
            }
            device.setHome(home);
            deviceDao.save(device);
            logger.info("Create device successfully with deviceId:" + device.getDeviceId());
        } catch (Exception e) {
            logger.error("Error creating device: " + e.getMessage());
        }
    }

    @Override
    public void updateDevice(Device device) {
        try {
            deviceDao.save(device);
            logger.info("Update device successfully with deviceId:" + device.getDeviceId());
        } catch (Exception e) {
            logger.error("Error updating device: " + e.getMessage());
        }
    }

    @Override
    public void deleteDevice(Long deviceId) {
        try {
            deviceDao.deleteById(deviceId);
            logger.info("Delete device successfully with deviceId:" + deviceId);
        } catch (Exception e) {
            logger.error("Error deleting device: " + e.getMessage());
        }
    }

}
