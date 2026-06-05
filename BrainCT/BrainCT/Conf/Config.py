import os
import torch

# ====================== 路径配置 ======================
SAVE_DIR = r"./run"
WEIGHT_SAVE_DIR = os.path.join(SAVE_DIR, "weights")
os.makedirs(SAVE_DIR, exist_ok=True)
os.makedirs(WEIGHT_SAVE_DIR, exist_ok=True)
WEIGHT_FILE = "best.pth"

CT_PATH = r"datasets/CT"
MASK_PATH = r"datasets/MASK"

# ====================== 训练超参数 ======================
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"
BATCH_SIZE = 1
EPOCHS = 100
LEARNING_RATE = 1e-4
VAL_SPLIT = 0.2  # 验证集比例
LR = 1e-4