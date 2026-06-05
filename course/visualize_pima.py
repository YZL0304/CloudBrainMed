import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import numpy as np

# 中文字体
plt.rcParams["font.sans-serif"] = ["SimHei", "Microsoft YaHei", "DejaVu Sans"]
plt.rcParams["axes.unicode_minus"] = False

# 读取数据
columns = [
    "Pregnancies", "Glucose", "BloodPressure", "SkinThickness",
    "Insulin", "BMI", "DiabetesPedigreeFunction", "Age", "Outcome"
]
df = pd.read_csv("pima-indians-diabetes.csv", header=None, names=columns)

# ==================== 图1：相关性热力图 ====================
fig1, ax1 = plt.subplots(figsize=(12, 10))
corr = df.corr()
mask = np.triu(np.ones_like(corr, dtype=bool), k=1)
sns.heatmap(
    corr, mask=mask, annot=True, fmt=".2f",
    cmap="RdBu_r", vmin=-1, vmax=1, center=0,
    square=True, linewidths=1,
    cbar_kws={"shrink": 0.75},
    annot_kws={"size": 10},
    ax=ax1
)
ax1.set_title("Pima Indians Diabetes — Feature Correlation Heatmap", fontsize=15, fontweight="bold", pad=18)
fig1.tight_layout()
fig1.savefig("pima_heatmap.png", dpi=200, bbox_inches="tight")
plt.close(fig1)
print("[1/2] Heatmap saved -> pima_heatmap.png")

# ==================== 图2：分布直方图 ====================
fig2, axes = plt.subplots(3, 3, figsize=(15, 12))
axes = axes.flatten()

for i, col in enumerate(columns):
    ax = axes[i]
    # 按 Outcome 分组绘制
    for label, color, name in zip([0, 1], ["#4ECDC4", "#FF6B6B"], ["No Diabetes", "Diabetes"]):
        subset = df[df["Outcome"] == label][col]
        ax.hist(subset, bins=25, alpha=0.6, color=color, label=name, edgecolor="white", linewidth=0.5)
    ax.set_title(col, fontsize=12, fontweight="bold")
    ax.legend(fontsize=7)

fig2.suptitle("Pima Indians Diabetes — Feature Distribution Histograms", fontsize=16, fontweight="bold", y=1.01)
fig2.tight_layout()
fig2.savefig("pima_histograms.png", dpi=200, bbox_inches="tight")
plt.close(fig2)
print("[2/2] Histograms saved -> pima_histograms.png")
print("Done!")
