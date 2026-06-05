package com.cloudbrainmed.doctor.controller;

import com.cloudbrainmed.common.result.Result;
import com.cloudbrainmed.doctor.service.ConsultService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/doctor/consult")
public class DoctorConsultController {

    private final ConsultService service;

    public DoctorConsultController(ConsultService service) {
        this.service = service;
    }

    /** 4.4.2.1 查询接诊患者列表 */
    @GetMapping("/list")
    public Result<?> list(@RequestHeader(value = "token", required = false) String token,
                          @RequestParam(required = false) String consultStatus,
                          @RequestParam(required = false) String date,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int limit) {
        String doctorId = extractDoctorId(token);
        return Result.ok(service.getList(doctorId, consultStatus, date, page, limit));
    }

    /** 4.4.2.2 获取接诊详情 */
    @GetMapping("/detail")
    public Result<?> detail(@RequestHeader(value = "token", required = false) String token,
                            @RequestParam String registerId) {
        return Result.ok(service.getDetail(registerId));
    }

    /** 4.4.2.3 暂存病历草稿 */
    @PostMapping("/save-draft")
    public Result<?> saveDraft(@RequestHeader(value = "token", required = false) String token,
                               @RequestBody Map<String, String> body) {
        service.saveDraft(body.get("registerId"), body.get("recordDesc"));
        return Result.ok();
    }

    /** 4.4.2.4 确认正式病历 */
    @PostMapping("/confirm-record")
    public Result<?> confirmRecord(@RequestHeader(value = "token", required = false) String token,
                                   @RequestBody Map<String, String> body) {
        service.confirmRecord(body.get("registerId"), body.get("recordDesc"));
        return Result.ok();
    }

    /** 4.4.2.5 生成检查申请单 */
    @PostMapping("/create-exam-order")
    public Result<?> createExamOrder(@RequestHeader(value = "token", required = false) String token,
                                     @RequestBody Map<String, String> body) {
        service.createExamOrder(body.get("registerId"),
                body.get("checkItemList"), body.get("urgencyLevel"));
        return Result.ok();
    }

    /** 4.4.2.6 完成本次接诊 */
    @PostMapping("/complete")
    public Result<?> complete(@RequestHeader(value = "token", required = false) String token,
                              @RequestBody Map<String, String> body) {
        service.completeConsult(body.get("registerId"));
        return Result.ok();
    }

    private String extractDoctorId(String token) {
        // TODO: 解析 JWT token 获取 doctorId
        return "D001";
    }
}
