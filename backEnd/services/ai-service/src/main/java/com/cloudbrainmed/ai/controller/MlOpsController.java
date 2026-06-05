package org.example.backend.guanliyuan.controller;

import org.example.backend.common.Result;
import org.example.backend.guanliyuan.service.MlOpsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/ml")
public class MlOpsController {

    private final MlOpsService service;

    public MlOpsController(MlOpsService service) {
        this.service = service;
    }

    /** 4.5.4.1 AI推理统计看板 */
    @GetMapping("/dashboard/inference-stats")
    public Result<?> inferenceStats(@RequestHeader(value = "token", required = false) String token) {
        return Result.ok(service.getDashboard());
    }

    /** 4.5.4.2 AI推理日志查询 */
    @GetMapping("/inference/logs")
    public Result<?> inferenceLogs(@RequestHeader(value = "token", required = false) String token,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "20") int limit) {
        return Result.ok(service.getLogs(page, limit));
    }

    /** 4.5.4.3 训练样本池查询 */
    @GetMapping("/sample/list")
    public Result<?> sampleList(@RequestHeader(value = "token", required = false) String token,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "20") int limit) {
        return Result.ok(service.getSamples(page, limit));
    }

    /** 4.5.4.4 样本标注 */
    @PutMapping("/sample/label")
    public Result<?> labelSample(@RequestHeader(value = "token", required = false) String token,
                                 @RequestBody Map<String, String> body) {
        service.labelSample(body.get("sampleId"), body.get("labelTag"));
        return Result.ok();
    }

    /** 4.5.4.5 手动触发模型训练 */
    @PostMapping("/model/train")
    public Result<?> trainModel(@RequestHeader(value = "token", required = false) String token) {
        return Result.ok(service.triggerTraining());
    }

    /** 4.5.4.6 模型版本流量配置 */
    @PutMapping("/model/traffic")
    public Result<?> setTraffic(@RequestHeader(value = "token", required = false) String token,
                                @RequestBody Map<String, Object> body) {
        service.setTraffic(
                body.get("modelId").toString(),
                Integer.parseInt(body.get("trafficPct").toString()));
        return Result.ok();
    }
}
