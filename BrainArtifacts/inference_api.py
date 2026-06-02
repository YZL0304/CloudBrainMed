"""
CT 伪影推理 API 服务
=====================
启动：python inference_api.py
端口：5000
"""

import os, sys, io, tempfile, base64
import torch
import numpy as np
import nibabel as nib
from flask import Flask, request, jsonify
from flask_cors import CORS

_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, _PROJECT_ROOT)

from BrainArtifacts.model.UNet3D import UNet3D
from BrainArtifacts.datasets.DataPreprocessor import DataPreprocessor

app = Flask(__name__)
CORS(app)

MODEL_PATH = os.path.join(_PROJECT_ROOT, "best_3dunet.pth")
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")
THRESHOLD = 0.35

# 全局加载模型（启动时一次）
model = None
preprocessor_cache = {}


def get_preprocessor(datapath):
    """缓存 DataPreprocessor 避免反复扫描"""
    key = os.path.dirname(datapath)
    if key not in preprocessor_cache:
        preprocessor_cache[key] = DataPreprocessor(datapath=datapath)
    return preprocessor_cache[key]


@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "model_loaded": model is not None, "device": str(DEVICE)})


@app.route("/inference", methods=["POST"])
def inference():
    """
    上传 CT 文件，返回预测掩码

    POST /inference
    Content-Type: multipart/form-data
    参数: file (CT .nii.gz)

    返回:
    {
        "success": true,
        "positive_pixels": 42,
        "total_pixels": 999424,
        "ratio": 0.0042,
        "mask_base64": "..."    // mask 文件的 base64
    }
    """
    if "file" not in request.files:
        return jsonify({"success": False, "error": "No file uploaded"}), 400

    ct_file = request.files["file"]
    ct_data = ct_file.read()

    # 保存临时文件
    with tempfile.NamedTemporaryFile(suffix=".nii.gz", delete=False) as tmp:
        tmp.write(ct_data)
        tmp_path = tmp.name

    try:
        # 预处理
        img = nib.load(tmp_path)
        ct_dir = os.path.dirname(tmp_path)
        preprocessor = get_preprocessor(tmp_path)
        transform = preprocessor.get_test_transform()
        data = transform({"image": tmp_path})
        img_tensor = data["image"].unsqueeze(0).to(DEVICE)

        # 推理
        with torch.no_grad():
            logits = model(img_tensor)
            mask = (torch.sigmoid(logits) > THRESHOLD).float()
            mask_np = mask.squeeze().cpu().numpy()

        pos = int(mask_np.sum())
        total = mask_np.size

        # 保存掩码为临时 nii.gz，转 base64 返回
        mask_nii = nib.Nifti1Image(mask_np.astype(np.float32), np.eye(4))
        buf = io.BytesIO()
        nib.save(mask_nii, buf)
        mask_b64 = base64.b64encode(buf.getvalue()).decode()

        return jsonify({
            "success": True,
            "positive_pixels": pos,
            "total_pixels": total,
            "ratio": round(pos / total * 100, 6),
            "mask_base64": mask_b64,
        })

    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500
    finally:
        os.unlink(tmp_path)


@app.route("/inference/simple", methods=["POST"])
def inference_simple():
    """
    简化版：只返回统计信息，不返回 mask 文件（更快）
    """
    if "file" not in request.files:
        return jsonify({"success": False, "error": "No file"}), 400

    ct_file = request.files["file"]
    ct_data = ct_file.read()

    with tempfile.NamedTemporaryFile(suffix=".nii.gz", delete=False) as tmp:
        tmp.write(ct_data)
        tmp_path = tmp.name

    try:
        preprocessor = get_preprocessor(tmp_path)
        transform = preprocessor.get_test_transform()
        data = transform({"image": tmp_path})
        img_tensor = data["image"].unsqueeze(0).to(DEVICE)

        with torch.no_grad():
            logits = model(img_tensor)
            mask = (torch.sigmoid(logits) > THRESHOLD).float()
            mask_np = mask.squeeze().cpu().numpy()

        pos = int(mask_np.sum())
        total = mask_np.size

        return jsonify({
            "success": True,
            "positive_pixels": pos,
            "total_pixels": total,
            "ratio": round(pos / total * 100, 6),
        })

    except Exception as e:
        return jsonify({"success": False, "error": str(e)}), 500
    finally:
        os.unlink(tmp_path)


if __name__ == "__main__":
    print(f"[Server] Loading model from {MODEL_PATH}...")
    model = UNet3D().to(DEVICE)
    model.load_state_dict(torch.load(MODEL_PATH, map_location=DEVICE, weights_only=True))
    model.eval()
    print(f"[Server] Model loaded. Device: {DEVICE}")
    print(f"[Server] Starting on http://localhost:5000")
    app.run(host="0.0.0.0", port=5000, debug=False)
