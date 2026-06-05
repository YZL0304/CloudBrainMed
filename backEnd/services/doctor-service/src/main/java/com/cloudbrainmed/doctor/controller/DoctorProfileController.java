package com.cloudbrainmed.doctor.controller;

import com.cloudbrainmed.common.result.Result;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/profile")
public class DoctorProfileController {

    @GetMapping("/info")
    public Result<?> info(@RequestHeader(value = "token", required = false) String token) {
        String doctorId = extractDoctorId(token);
        return Result.ok(Map.of("doctorId", doctorId, "name", "Dr. " + doctorId));
    }

    @PutMapping("/update")
    public Result<?> update(@RequestHeader(value = "token", required = false) String token,
                            @RequestBody Map<String, String> body) {
        return Result.ok();
    }

    @PostMapping("/avatar-upload")
    public Result<?> avatarUpload(@RequestHeader(value = "token", required = false) String token,
                                   @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        return Result.ok(Map.of("avatarUrl", "/files/avatar/default.png"));
    }

    @PostMapping("/change-phone")
    public Result<?> changePhone(@RequestHeader(value = "token", required = false) String token,
                                  @RequestBody Map<String, String> body) {
        return Result.ok();
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestHeader(value = "token", required = false) String token,
                                     @RequestBody Map<String, String> body) {
        return Result.ok();
    }

    private String extractDoctorId(String token) { return "D001"; }
}
