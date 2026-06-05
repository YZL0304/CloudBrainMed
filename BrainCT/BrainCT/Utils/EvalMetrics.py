import torch

# ====================== 2. 评价指标计算类（静态方法） ======================
class MetricCalculator:
	"""
	分割评价指标计算器
	所有方法均为静态方法，直接通过 类名.方法名 调用
	"""

	@staticmethod
	def calculate_metrics(pred, target):
		"""
		计算精确率、召回率、Dice、准确率
		"""
		pred = torch.sigmoid(pred)
		pred = (pred > 0.5).float()

		intersection = (pred * target).sum()
		union = pred.sum() + target.sum()

		tp = intersection
		fp = pred.sum() - tp
		fn = target.sum() - tp

		precision = tp / (tp + fp + 1e-7)
		recall = tp / (tp + fn + 1e-7)
		dice = (2. * intersection + 1e-7) / (union + 1e-7)
		accuracy = (pred == target).sum() / torch.numel(pred)

		return precision.item(), recall.item(), dice.item(), accuracy.item()