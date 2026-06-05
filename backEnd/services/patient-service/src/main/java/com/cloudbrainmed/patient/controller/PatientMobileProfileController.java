package com.cloudbrainmed.patient.controller;

import com.cloudbrainmed.common.result.Result;
import com.cloudbrainmed.patient.module5.service.PatientProfileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/patient/profile")
public class PatientMobileProfileController {

    private final com.cloudbrainmed.patient.module5.service.PatientProfileService service;

    public PatientMobileProfileController(com.cloudbrainmed.patient.module5.service.PatientProfileService service) {
        this.service = service;
    }

    /** 4.3.1.1 查询个人脱敏信息 */
    @GetMapping("/info")
    public Result<?> info(@RequestHeader(value = "token", required = false) String token) {
        String patientId = extractPatientId(token);
        return Result.success(service.getInfo(patientId));
    }

    /** 4.3.1.2 更新基础信息 */
    @PutMapping("/update")
    public Result<?> update(@RequestHeader(value = "token", required = false) String token,
                            @RequestBody Map<String, String> body) {
        String patientId = extractPatientId(token);
        service.updateInfo(patientId,
                body.get("name"), body.get("gender"),
                body.get("address"), body.get("birthday"));
        return Result.success("成功", null);
    }

    /** 4.3.1.3 头像上传 */
    @PostMapping("/avatar-upload")
    public Result<?> avatarUpload(@RequestHeader(value = "token", required = false) String token,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        String patientId = extractPatientId(token);
        String url = service.uploadAvatar(patientId, file.getBytes(), file.getOriginalFilename());
        return Result.success(Map.of("avatarUrl", url));
    }

    /** 4.3.1.4 修改手机号 */
    @PostMapping("/change-phone")
    public Result<?> changePhone(@RequestHeader(value = "token", required = false) String token,
                                 @RequestBody Map<String, String> body) {
        String patientId = extractPatientId(token);
        service.changePhone(patientId,
                body.get("oldPhone"), body.get("newPhone"), body.get("smsCode"));
        return Result.success("成功", null);
    }

    /** 4.3.1.5 修改登录密码 */
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestHeader(value = "token", required = false) String token,
                                    @RequestBody Map<String, String> body) {
        String patientId = extractPatientId(token);
        service.changePassword(patientId,
                body.get("oldPassword"), body.get("newPassword"));
        return Result.success("成功", null);
    }

    /** 4.3.1.6 身份证修改权限验证 */
    @PostMapping("/verify-idcard")
    public Result<?> verifyIdCard(@RequestHeader(value = "token", required = false) String token,
                                  @RequestBody Map<String, String> body) {
        String patientId = extractPatientId(token);
        service.verifyIdCard(patientId, body.get("password"));
        return Result.success("成功", null);
    }

    private String extractPatientId(String token) {
        // TODO: 解析 JWT token 获取 patientId
        return "P001";
    }
}
