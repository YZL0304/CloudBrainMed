"""
CT 伪影推理 API（使用老师 Detection 引擎）
==========================================
启动：python inference_api.py
端口：5000
"""

import os, sys, io, tempfile, base64
import numpy as np
import SimpleITK as sitk
from flask import Flask, request, jsonify
from flask_cors import CORS

# ── 路径 ──
_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(_PROJECT_ROOT, 'BrainCT'))
sys.path.insert(0, os.path.join(_PROJECT_ROOT, 'BrainCT', 'BrainCT'))

from Detection.CTArtifactInfer import CTArtifactInfer

MODEL_PATH = os.path.join(_PROJECT_ROOT, 'BrainCT', 'BrainCT', 'run_Attention_AdamW', 'weights', 'best.pth')

app = Flask(__name__)
CORS(app)

# 全局加载
infer = None


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "model": "AttentionUNet2D", "model_path": MODEL_PATH})


@app.route("/inference", methods=["POST"])
def inference():
    """上传 CT .nii.gz → 返回检测统计"""
    if "file" not in request.files:
        return jsonify({"success": False, "error": "No file"}), 400

    ct_file = request.files["file"]
    ct_data = ct_file.read()

    with tempfile.NamedTemporaryFile(suffix=".nii.gz", delete=False) as tmp:
        tmp.write(ct_data)
        tmp_path = tmp.name

    try:
        # 用老师的引擎推理
        mask = infer.predict_from_nii(tmp_path)
        mask_arr = sitk.GetArrayFromImage(mask)
        pos = int(mask_arr.sum())
        total = mask_arr.size

        # 提取特征向量（960维），用于聚类分析
        features = None
        try:
            if hasattr(infer.model, 'extract_features'):
                feat = infer.model.extract_features()
                features = feat.squeeze().detach().cpu().numpy().tolist()
        except Exception:
            pass

        return jsonify({
            "success": True,
            "positive_pixels": pos,
            "total_pixels": total,
            "ratio": round(pos / total * 100, 6),
            "feature_dim": len(features) if features else 0,
            "feature_vector": features,
        })

    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500
    finally:
        os.unlink(tmp_path)


if __name__ == "__main__":
    print(f"[Server] Loading model from {MODEL_PATH}...")
    infer = CTArtifactInfer(model_weight_path=MODEL_PATH)
    print(f"[Server] Model loaded. Device: cuda")
    print(f"[Server] Starting on http://localhost:5000")
    app.run(host="0.0.0.0", port=5000, debug=False)
