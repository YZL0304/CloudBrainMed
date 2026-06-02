"""
3D UNet 脑部CT伪影分割 — 主训练入口
=====================================
运行方式：cd D:\CloudBrainMed && python BrainArtifacts\SegMain.py
"""

import os
import sys
import torch
from torch.utils.data import DataLoader, WeightedRandomSampler
from sklearn.model_selection import train_test_split
from tqdm import tqdm

# MONAI
from monai.transforms import LoadImaged

# ═══ 确保 D:\CloudBrainMed 在 sys.path ═══
_PROJECT_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
if _PROJECT_ROOT not in sys.path:
    sys.path.insert(0, _PROJECT_ROOT)

# ═══ 自定义模块 ═══
from BrainArtifacts.datasets.DataPreprocessor import DataPreprocessor
from BrainArtifacts.datasets.BrainCTArtifactSegDataset import BrainCTArtifactSegDataset
from BrainArtifacts.utils.WeightUtils import WeightUtils
from BrainArtifacts.trainer.SegTrainer import SegTrainer
from BrainArtifacts.model.UNet3D import UNet3D

# ══════════════════════════════════════════
# 全局配置
# ══════════════════════════════════════════
# 数据路径 — 把你的 CT 和 mask .nii.gz 文件放到这两个目录下
CT_PATH = r'D:\CloudBrainMed\BrainArtifacts\datasets\CT'
MASK_PATH = r'D:\CloudBrainMed\BrainArtifacts\datasets\mask'
BATCH_SIZE = 1
EPOCHS = 50
LR = 1e-3
DEVICE = torch.device("cuda" if torch.cuda.is_available() else "cpu")


def create_seg_dataloaders(img_dir, mask_dir, batch_size=1, num_workers=0):
    """创建训练/验证 DataLoader（含均衡采样）"""
    all_files = sorted([f for f in os.listdir(img_dir) if f.endswith(".nii.gz")])
    print(f"总样本数：{len(all_files)}")

    if len(all_files) == 0:
        raise FileNotFoundError(f"{img_dir} 中没有 .nii.gz 文件！请放入 CT 数据。")

    # 划分训练/验证
    train_files, val_files = train_test_split(all_files, test_size=0.2, random_state=42)

    # 数据增强
    preData = DataPreprocessor(datapath=img_dir)
    trainTransform = preData.get_train_transform()
    valTransform = preData.get_val_transform()

    # 创建数据集
    train_ds = BrainCTArtifactSegDataset(img_dir, mask_dir, train_files, trainTransform)
    val_ds = BrainCTArtifactSegDataset(img_dir, mask_dir, val_files, valTransform)

    # 类别均衡采样
    weights = []
    for f in train_files:
        mask_path = os.path.join(mask_dir, f)
        mask = LoadImaged(keys=["label"])({"label": mask_path})["label"]
        pos = (mask > 0).sum().item()
        weights.append(5.0 if pos > 0 else 1.0)

    sampler = WeightedRandomSampler(weights, num_samples=len(weights), replacement=True)
    train_loader = DataLoader(train_ds, batch_size=batch_size, sampler=sampler, num_workers=num_workers)
    val_loader = DataLoader(val_ds, batch_size=batch_size, shuffle=False, num_workers=num_workers)

    pos_weight = WeightUtils.compute_class_weights(mask_dir, train_files, device=DEVICE)
    return train_loader, val_loader, pos_weight


def main():
    print("=" * 50)
    print("3D UNet 脑部CT伪影分割训练")
    print(f"设备：{DEVICE}")
    print(f"CT 路径：{CT_PATH}")
    print(f"Mask 路径：{MASK_PATH}")
    print("=" * 50)

    train_loader, val_loader, pos_weight = create_seg_dataloaders(CT_PATH, MASK_PATH, 1, 0)

    model = UNet3D()
    trainer = SegTrainer(
        model=model,
        train_loader=train_loader,
        val_loader=val_loader,
        device=DEVICE,
        pos_weight=pos_weight,
        lr=LR
    )
    trainer.run()


if __name__ == "__main__":
    main()
