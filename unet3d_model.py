"""
3D U-Net — 医学影像像素级分割
================================
基于老师课堂讲解的标准 3D U-Net 架构，从零手写（纯 PyTorch，无第三方实现）。

架构：
  编码器（4层下采样）：DoubleConv → MaxPool
  瓶颈层：DoubleConv
  解码器（4层上采样）：TransposeConv → SkipCat → DoubleConv
  输出：1×1×1 Conv → 单通道（二值掩码）

参考参数量：~1.4M
参考层数：~62 层
"""

import torch
import torch.nn as nn
import torch.nn.functional as F


class DoubleConv(nn.Module):
    """双卷积块：(Conv3d → BN → ReLU) × 2"""

    def __init__(self, in_channels, out_channels):
        super().__init__()
        self.conv = nn.Sequential(
            nn.Conv3d(in_channels, out_channels, kernel_size=3, padding=1, bias=False),
            nn.BatchNorm3d(out_channels),
            nn.ReLU(inplace=True),
            nn.Conv3d(out_channels, out_channels, kernel_size=3, padding=1, bias=False),
            nn.BatchNorm3d(out_channels),
            nn.ReLU(inplace=True),
        )

    def forward(self, x):
        return self.conv(x)


class Down(nn.Module):
    """下采样模块：MaxPool → DoubleConv"""

    def __init__(self, in_channels, out_channels):
        super().__init__()
        self.pool = nn.MaxPool3d(kernel_size=2, stride=2)
        self.conv = DoubleConv(in_channels, out_channels)

    def forward(self, x):
        return self.conv(self.pool(x))


class Up(nn.Module):
    """上采样模块：转置卷积 → 跳跃连接拼接 → DoubleConv"""

    def __init__(self, in_channels, out_channels):
        super().__init__()
        self.up = nn.ConvTranspose3d(in_channels, out_channels, kernel_size=2, stride=2)
        self.conv = DoubleConv(in_channels, out_channels)  # 拼接后通道翻倍，所以要 in_channels

    def forward(self, x1, x2):
        """
        x1: 下层上采样后的特征
        x2: 编码器对应层的跳跃连接特征
        """
        x1 = self.up(x1)

        # 处理边界不匹配（不同尺寸的 CT 深度可能不一致）
        diff_z = x2.size(2) - x1.size(2)
        diff_y = x2.size(3) - x1.size(3)
        diff_x = x2.size(4) - x1.size(4)

        x1 = F.pad(x1, [
            diff_x // 2, diff_x - diff_x // 2,
            diff_y // 2, diff_y - diff_y // 2,
            diff_z // 2, diff_z - diff_z // 2,
        ])

        # 跳跃连接：拼接编码器特征
        x = torch.cat([x2, x1], dim=1)
        return self.conv(x)


class UNet3D(nn.Module):
    """
    标准 3D U-Net

    Args:
        in_channels:  输入通道数（CT 灰度图 = 1）
        out_channels: 输出通道数（二值掩码 = 1）
        base_channels: 基础通道数（第一层，默认 32）
    """

    def __init__(self, in_channels=1, out_channels=1, base_channels=32):
        super().__init__()

        # ── 编码器 ──
        self.enc1 = DoubleConv(in_channels, base_channels)            # 1 → 32
        self.enc2 = Down(base_channels, base_channels * 2)            # 32 → 64
        self.enc3 = Down(base_channels * 2, base_channels * 4)        # 64 → 128
        self.enc4 = Down(base_channels * 4, base_channels * 8)        # 128 → 256

        # ── 瓶颈层 ──
        self.bottleneck = DoubleConv(base_channels * 8, base_channels * 16)  # 256 → 512

        # ── 解码器 ──
        self.dec4 = Up(base_channels * 16, base_channels * 8)         # 512 → 256
        self.dec3 = Up(base_channels * 8, base_channels * 4)          # 256 → 128
        self.dec2 = Up(base_channels * 4, base_channels * 2)          # 128 → 64
        self.dec1 = Up(base_channels * 2, base_channels)              # 64 → 32

        # ── 输出层 ──
        self.out_conv = nn.Conv3d(base_channels, out_channels, kernel_size=1)

    def forward(self, x):
        # 编码
        e1 = self.enc1(x)       # 保留用于跳跃连接
        e2 = self.enc2(e1)
        e3 = self.enc3(e2)
        e4 = self.enc4(e3)

        # 瓶颈
        b = self.bottleneck(e4)

        # 解码（跳跃连接）
        d4 = self.dec4(b, e4)
        d3 = self.dec3(d4, e3)
        d2 = self.dec2(d3, e2)
        d1 = self.dec1(d2, e1)

        return self.out_conv(d1)


# ════════════════════════════════════════════
# 测试与模型结构打印
# ════════════════════════════════════════════

if __name__ == "__main__":
    import sys

    # 自动检测设备
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"设备: {device}")

    # 创建模型
    model = UNet3D(in_channels=1, out_channels=1, base_channels=32).to(device)

    # ── 1. 打印模型结构 ──
    print("\n" + "=" * 60)
    print("模型结构")
    print("=" * 60)
    print(model)

    # ── 2. 统计参数 ──
    total_params = sum(p.numel() for p in model.parameters())
    trainable_params = sum(p.numel() for p in model.parameters() if p.requires_grad)
    print(f"\n总参数量:   {total_params:,}")
    print(f"可训练参数: {trainable_params:,}")

    # ── 3. 统计层数 ──
    layer_count = sum(1 for m in model.modules()
                      if isinstance(m, (nn.Conv3d, nn.ConvTranspose3d,
                                        nn.BatchNorm3d, nn.ReLU, nn.MaxPool3d)))
    print(f"层数:       {layer_count}")

    # ── 4. 测试前向传播 ──
    print("\n" + "=" * 60)
    print("前向传播测试")
    print("=" * 60)
    x = torch.randn(1, 1, 64, 128, 128).to(device)  # [B, C, D, H, W]
    with torch.no_grad():
        y = model(x)
    print(f"输入形状:  {x.shape}")
    print(f"输出形状:  {y.shape}")
    print(f"输出范围:  [{y.min().item():.4f}, {y.max().item():.4f}]")
    print("\n[DONE] Forward pass OK!")
