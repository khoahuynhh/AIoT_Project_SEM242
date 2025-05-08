package iot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iot.dto.DeviceDto.AddDeviceRequestDto;
import iot.model.Device;
import iot.model.Home;
import iot.model.User;
import iot.service.PersonalInfoService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class homeDTO {
    private String address;
    private String homeType;
}

@Getter
@Setter
class UserDTO {
    private String username;
    private String password;
    private String homeId;
}

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    @Autowired
    private PersonalInfoService personalInfoService;

    @PostMapping("/addhome")
    public String addHome(@RequestBody homeDTO homeDTO) {
        Home home = Home.builder().address(homeDTO.getAddress())
                .houseType(homeDTO.getHomeType())
                .build();
        personalInfoService.createHome(home);
        return "Home added successfully";
    }

    @PostMapping("/adduser")
    public String addUser(@RequestBody UserDTO userDTO) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .build();
        personalInfoService.createUser(user, Long.parseLong(userDTO.getHomeId()));
        return "User added successfully";
    }

    @PostMapping("/add_device")
    public String addDevice(@RequestBody AddDeviceRequestDto deviceDTO) {
        System.out.println(deviceDTO.getDeviceName());
        Device device = Device.builder()
                .deviceName(deviceDTO.getDeviceName())
                .deviceNumber(deviceDTO.getDeviceNumber())
                .deviceType(deviceDTO.getDeviceType())
                .deviceStatus(deviceDTO.getDeviceStatus())
                .build();
        personalInfoService.createDevice(device, Long.parseLong(deviceDTO.getHomeId()));
        return "Device added successfully";
    }
}
