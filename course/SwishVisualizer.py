import torch
import torch.nn as nn
import numpy as np
import matplotlib.pyplot as plt
import matplotlib

matplotlib.rc("font", family="Microsoft YaHei")
class SwishVisualizer:
	def __init__(self, beta=1.0):
		self.beta = beta
		self.sigmoid = nn.Sigmoid()

	def forward(self, x):
		"""前向计算 Swish 值"""
		return x * self.sigmoid(self.beta * x)

	def derivative(self, x):
		"""解析导数（β=1）"""
		sig = self.sigmoid(x)
		return sig + x * sig * (1 - sig)

	def plot_curve(self, x_range=(-6, 6), num=200):
		x = np.linspace(*x_range, num)
		x_t = torch.tensor(x, dtype=torch.float32)

		y = self.forward(x_t).numpy()
		dy = self.derivative(x_t).numpy()

		plt.figure(figsize=(10, 4))
		plt.subplot(1, 2, 1)
		plt.plot(x, y, 'b-', linewidth=2, label='Swish')
		plt.grid(True)
		plt.title('Swish 函数曲线')
		plt.legend()

		plt.subplot(1, 2, 2)
		plt.plot(x, dy, 'r-', linewidth=2, label='导数')
		plt.grid(True)
		plt.title('Swish 导数曲线')
		plt.legend()
		plt.tight_layout()
		plt.show()


# 测试
if __name__ == "__main__":
	swish = SwishVisualizer(beta=1.0)
	test_x = torch.tensor([-2., -1., 0., 1., 2.])
	print("输入:", test_x.numpy())
	print("Swish输出:", swish.forward(test_x).numpy())
	swish.plot_curve()