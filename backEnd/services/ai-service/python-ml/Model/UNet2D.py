"""
U-Net 2D 模型（基础版 + 多层融合特征提取）
对应课件: UNet2D_1780885385_2.py
"""
import torch
import torch.nn as nn


class DoubleConv(nn.Module):
    def __init__(self, in_c, out_c):
        super().__init__()
        self.conv = nn.Sequential(
            nn.Conv2d(in_c, out_c, 3, padding=1),
            nn.BatchNorm2d(out_c),
            nn.ReLU(inplace=True),
            nn.Conv2d(out_c, out_c, 3, padding=1),
            nn.BatchNorm2d(out_c),
            nn.ReLU(inplace=True)
        )

    def forward(self, x):
        return self.conv(x)


class UNet2D(nn.Module):
    """UNet2D + 多层融合特征提取（推理时自动保存特征向量）"""

    def __init__(self, in_ch=1, out_ch=1):
        super().__init__()
        self.pool = nn.MaxPool2d(2)

        # 编码器
        self.d1 = DoubleConv(in_ch, 64)
        self.d2 = DoubleConv(64, 128)
        self.d3 = DoubleConv(128, 256)
        self.d4 = DoubleConv(256, 512)

        # 解码器
        self.up4 = nn.ConvTranspose2d(512, 256, 2, stride=2)
        self.up3 = nn.ConvTranspose2d(256, 128, 2, stride=2)
        self.up2 = nn.ConvTranspose2d(128, 64, 2, stride=2)

        self.u4 = DoubleConv(512, 256)
        self.u3 = DoubleConv(256, 128)
        self.u2 = DoubleConv(128, 64)
        self.out = nn.Conv2d(64, out_ch, 1)

        # 自动保存的融合特征
        self.fused_feature = None

    def forward(self, x):
        # 编码
        d1 = self.d1(x)
        d2 = self.d2(self.pool(d1))
        d3 = self.d3(self.pool(d2))
        d4 = self.d4(self.pool(d3))

        # 多层特征融合（全局平均池化 + 拼接 + L2 归一化）
        f1 = torch.mean(d1, dim=[2, 3])
        f2 = torch.mean(d2, dim=[2, 3])
        f3 = torch.mean(d3, dim=[2, 3])
        f4 = torch.mean(d4, dim=[2, 3])
        feat = torch.cat([f1, f2, f3, f4], dim=1)
        self.fused_feature = torch.nn.functional.normalize(feat, p=2, dim=1)

        # 解码
        u4 = self.u4(torch.cat([self.up4(d4), d3], dim=1))
        u3 = self.u3(torch.cat([self.up3(u4), d2], dim=1))
        u2 = self.u2(torch.cat([self.up2(u3), d1], dim=1))
        out = self.out(u2)
        return out

    def extract_features(self):
        """
        推理后调用，返回当前样本的融合特征向量
        无需额外传入 x，直接返回 forward 时自动保存的特征
        """
        if self.fused_feature is None:
            raise ValueError("请先执行一次 model(x) 推理，再调用 extract_features()")
        return self.fused_feature
