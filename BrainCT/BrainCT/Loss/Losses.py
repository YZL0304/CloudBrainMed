import torch
import torch.nn as nn


class DiceCELoss(nn.Module):
	def __init__(self):
		super().__init__()
		# 🔥 修复：先创建tensor，不指定设备
		self.pos_weight = torch.tensor([20.0])
		self.ce = nn.BCEWithLogitsLoss(pos_weight=self.pos_weight)

	def forward(self, pred, target):
		# 🔥 关键：让权重自动和输入保持同一设备（cuda / cpu）
		if self.ce.pos_weight.device != pred.device:
			self.ce.pos_weight = self.ce.pos_weight.to(pred.device)

		ce = self.ce(pred, target)
		pred = torch.sigmoid(pred)
		intersection = (pred * target).sum()
		dice = 1 - (2. * intersection + 1e-7) / (pred.sum() + target.sum() + 1e-7)
		return ce * 0.5 + dice * 0.5