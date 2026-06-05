import sys, os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import torch.optim as optim
from torch.utils.data import DataLoader, random_split

# 导入所有模块
from Conf.Config import *
from Datasets.Datasets import CTSliceDataset
from Model.UNet2D import UNet2D
#from Model.AttentionUNet2D import UNet2D
from Loss.Losses import DiceCELoss
from Trainer.UNet2DTrainer import Trainer
from Vision.MeticsVisualizer import Visualizer

# ========================
# 🔥 主训练流程
# ========================
def main():
	print("=" * 50)
	print("2D UNet 脑部CT伪影分割训练")
	print(f"设备：{DEVICE}")
	print("=" * 50)

	# ====================== 数据加载 ======================
	dataset = CTSliceDataset(CT_PATH, MASK_PATH)
	train_size = int((1 - VAL_SPLIT) * len(dataset))
	val_size = len(dataset) - train_size
	train_ds, val_ds = random_split(dataset, [train_size, val_size])
	# 关键：训练集开增强，验证集关闭增强
	train_ds.dataset.is_train = True
	val_ds.dataset.is_train = False
	train_loader = DataLoader(train_ds, batch_size=BATCH_SIZE, shuffle=True)
	val_loader = DataLoader(val_ds, batch_size=BATCH_SIZE, shuffle=False)

	# ====================== 模型、损失、优化器 ======================
	model = UNet2D().to(DEVICE)
	loss_fn = DiceCELoss()
	optimizer = torch.optim.Adam(model.parameters(), lr=LEARNING_RATE)
	#optimizer = optim.AdamW(model.parameters(), lr=LR, weight_decay=1e-5)

	# 🔥 加入余弦退火学习率（你要的代码）
	scheduler = optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=EPOCHS)

	weights_file = os.path.join(WEIGHT_SAVE_DIR, WEIGHT_FILE)
	# 🔥 把 scheduler 传入 Trainer
	trainer = Trainer(
		model=model,
		loss_fn=loss_fn,
		optimizer=optimizer,
		device=DEVICE,
		epochs=EPOCHS,
		save_path=weights_file,
		scheduler=scheduler
	)
	history = trainer.Train(train_loader, val_loader)

	# ====================== 可视化 ======================
	print("\n📊 开始生成可视化图表...")
	vision = Visualizer(save_dir=SAVE_DIR)
	vision.plot_all_curves(history)
	vision.plot_confusion(model, val_loader, DEVICE)
	vision.plot_sample_pred(model, dataset, DEVICE)

	print("\n🎉 全部训练 + 可视化完成！")

# ========================
# 主入口执行
# ========================
if __name__ == "__main__":
	main()