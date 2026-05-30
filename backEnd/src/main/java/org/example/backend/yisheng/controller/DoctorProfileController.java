package org.example.backend.yisheng.controller;

import org.example.backend.common.Result;
import org.example.backend.yisheng.service.DoctorProfileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/profile")
public class DoctorProfileController {

    private final DoctorProfileService service;

    public DoctorProfileController(DoctorProfileService service) {
        this.service = service;
    }

    /** 4.4.1.1 查询医生资料 */
    @GetMapping("/info")
    public Result<?> info(@RequestHeader(value = "token", required = false) String token) {
        String doctorId = extractDoctorId(token);
        return Result.ok(service.getInfo(doctorId));
    }

    /** 4.4.1.2 更新医生资料 */
    @PutMapping("/update")
    public Result<?> update(@RequestHeader(value = "token", required = false) String token,
                            @RequestBody Map<String, String> body) {
        String doctorId = extractDoctorId(token);
        service.updateProfile(doctorId,
                body.get("goodAt"), body.get("introduction"), body.get("email"));
        return Result.ok();
    }

    /** 4.4.1.3 医生头像上传 */
    @PostMapping("/avatar-upload")
    public Result<?> avatarUpload(@RequestHeader(value = "token", required = false) String token,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        String doctorId = extractDoctorId(token);
        String url = service.uploadAvatar(doctorId, file.getBytes(), file.getOriginalFilename());
        return Result.ok(Map.of("avatarUrl", url));
    }

    /** 4.4.1.4 医生修改密码 */
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestHeader(value = "token", required = false) String token,
                                    @RequestBody Map<String, String> body) {
        String doctorId = extractDoctorId(token);
        service.changePassword(doctorId,
                body.get("oldPassword"), body.get("newPassword"));
        return Result.ok();
    }

    /** 4.4.1.5 查询资料完善状态 */
    @GetMapping("/setup-status")
    public Result<?> setupStatus(@RequestHeader(value = "token", required = false) String token) {
        String doctorId = extractDoctorId(token);
        boolean needSetup = service.getSetupStatus(doctorId);
        return Result.ok(Map.of("needSetup", needSetup));
    }

    private String extractDoctorId(String token) {
        // TODO: 解析 JWT token 获取 doctorId
        return "D001";
    }
}
