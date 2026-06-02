"""
3D U-Net CT 伪影推理
=====================
输入：CT 图像 (.nii.gz)
输出：预测掩码 (.nii.gz) + 可视化

用法：
    python SegInference.py --input CT_PATH.nii.gz --output mask_output.nii.gz
"""

import os, sys, argparse, itertools
import torch
import numpy as np
import nibabel as nib

_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

from BrainArtifacts.model.UNet3D import UNet3D
from BrainArtifacts.datasets.DataPreprocessor import DataPreprocessor

# ── 配置 ──
MODEL_PATH = os.path.join(_PROJECT_ROOT, "best_3dunet.pth")
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")
THRESHOLD = 0.35  # 二值化阈值


def load_model(model_path=MODEL_PATH):
    """加载训练好的模型"""
    model = UNet3D().to(DEVICE)
    model.load_state_dict(torch.load(model_path, map_location=DEVICE, weights_only=True))
    model.eval()
    print(f"[Model] Loaded from {model_path}")
    return model


def preprocess_ct(ct_path, preprocessor):
    """对单张 CT 做预处理，返回张量 (1,1,D,H,W)"""
    data = {"image": ct_path}
    transform = preprocessor.get_test_transform()
    data = transform(data)
    img = data["image"].unsqueeze(0).to(DEVICE)  # add batch dim
    return img


def predict_mask(model, img_tensor):
    """模型推理，返回二值掩码 numpy (D,H,W)"""
    with torch.no_grad():
        logits = model(img_tensor)
        mask = (torch.sigmoid(logits) > THRESHOLD).float()
        mask = mask.squeeze().cpu().numpy()  # (D,H,W)
    return mask


def inference(ct_path, output_path=None, model_path=MODEL_PATH):
    """
    完整推理流程

    Args:
        ct_path: 输入 CT 路径
        output_path: 输出掩码路径（可选，默认与输入同目录）
        model_path: 模型路径

    Returns:
        mask: 预测掩码 numpy 数组
    """
    print(f"[Inference] CT: {ct_path}")
    print(f"[Inference] Device: {DEVICE}")

    # 1. 加载模型
    model = load_model(model_path)

    # 2. 预处理（扫描数据集获取统一尺寸）
    ct_dir = os.path.dirname(ct_path)
    preprocessor = DataPreprocessor(datapath=ct_dir)
    img_tensor = preprocess_ct(ct_path, preprocessor)
    print(f"[Inference] Input shape: {img_tensor.shape}")

    # 3. 推理
    mask = predict_mask(model, img_tensor)
    pos_pixels = mask.sum()
    total_pixels = mask.size
    print(f"[Inference] Mask positive: {pos_pixels}/{total_pixels} "
          f"({pos_pixels/total_pixels*100:.4f}%)")

    # 4. 保存掩码
    if output_path is None:
        base = os.path.splitext(os.path.basename(ct_path))[0]
        output_path = os.path.join(os.path.dirname(ct_path), f"{base}_pred_mask.nii.gz")

    mask_nii = nib.Nifti1Image(mask.astype(np.float32), np.eye(4))
    nib.save(mask_nii, output_path)
    print(f"[Inference] Mask saved to: {output_path}")

    return mask


def batch_inference(ct_dir, output_dir, model_path=MODEL_PATH):
    """批量推理一个目录下的所有 CT"""
    ct_files = sorted([f for f in os.listdir(ct_dir) if f.endswith(".nii.gz")])
    if not ct_files:
        print(f"[Error] No .nii.gz files in {ct_dir}")
        return

    print(f"[Batch] {len(ct_files)} CT files found")
    model = load_model(model_path)
    preprocessor = DataPreprocessor(datapath=ct_dir)
    os.makedirs(output_dir, exist_ok=True)

    for fname in ct_files:
        ct_path = os.path.join(ct_dir, fname)
        out_path = os.path.join(output_dir, fname.replace(".nii.gz", "_pred_mask.nii.gz"))

        img_tensor = preprocess_ct(ct_path, preprocessor)
        mask = predict_mask(model, img_tensor)

        nib.save(nib.Nifti1Image(mask.astype(np.float32), np.eye(4)), out_path)
        pos = mask.sum()
        print(f"  {fname} -> {pos} positive pixels ({pos/mask.size*100:.3f}%)")

    print(f"[Batch] Done. {len(ct_files)} masks saved to {output_dir}")


# ════════════════════════════════════════
# CLI 入口
# ════════════════════════════════════════
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="3D U-Net CT伪影推理")
    parser.add_argument("--input", type=str, help="输入CT路径 (.nii.gz)")
    parser.add_argument("--output", type=str, default=None, help="输出掩码路径")
    parser.add_argument("--batch", type=str, default=None, help="批量推理目录")
    parser.add_argument("--outdir", type=str, default="./output_masks", help="批量输出目录")
    parser.add_argument("--model", type=str, default=MODEL_PATH, help="模型路径")

    args = parser.parse_args()

    if args.batch:
        batch_inference(args.batch, args.outdir, args.model)
    elif args.input:
        inference(args.input, args.output, args.model)
    else:
        print("用法:")
        print("  单张: python SegInference.py --input CT.nii.gz --output mask.nii.gz")
        print("  批量: python SegInference.py --batch ./CT_folder --outdir ./masks")
