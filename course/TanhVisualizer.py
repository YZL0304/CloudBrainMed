import torch
import torch.nn as nn
import numpy as np
import matplotlib.pyplot as plt
import matplotlib

matplotlib.rc("font", family="Microsoft YaHei")

class TanhVisualizer:
	def __init__(self):
		self.tanh = nn.Tanh()

	def forward_calc(self, x_np):
		x = torch.tensor(x_np, dtype=torch.float32)
		return self.tanh(x).numpy()

	def plot_curve(self, x_range=(-8, 8), sample_num=200):
		x_vals = np.linspace(*x_range, sample_num)
		x_tensor = torch.tensor(x_vals, requires_grad=True)
		y_out = torch.tanh(x_tensor)

		y_out.backward(torch.ones_like(x_tensor))
		grad = x_tensor.grad.detach().numpy()
		y_np = y_out.detach().numpy()

		plt.figure(figsize=(10, 4))
		plt.subplot(1, 2, 1)
		plt.plot(x_vals, y_np, 'b-', linewidth=2, label='Tanh')
		plt.grid(True)
		plt.title('Tanh 函数曲线')
		plt.legend()

		plt.subplot(1, 2, 2)
		plt.plot(x_vals, grad, 'r-', linewidth=2, label='导数')
		plt.grid(True)
		plt.title('Tanh 导数曲线')
		plt.legend()
		plt.tight_layout()
		plt.show()


if __name__ == "__main__":
	tanh_vis = TanhVisualizer()
	test_x = [-2, -1, 0, 1, 2]
	res = tanh_vis.forward_calc(test_x)
	print("输入值：", test_x)
	print("Tanh输出：", res)
	tanh_vis.plot_curve()