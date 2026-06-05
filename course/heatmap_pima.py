import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import numpy as np

# 加载数据集
columns = [
    "Pregnancies", "Glucose", "BloodPressure", "SkinThickness",
    "Insulin", "BMI", "DiabetesPedigreeFunction", "Age", "Outcome"
]
df = pd.read_csv("pima-indians-diabetes.csv", header=None, names=columns)

# 计算相关性矩阵
corr = df.corr()

# 画热力图
plt.figure(figsize=(12, 10))
mask = np.triu(np.ones_like(corr, dtype=bool), k=1)  # 只显示下三角
sns.heatmap(
    corr,
    mask=mask,
    annot=True,
    fmt=".2f",
    cmap="RdBu_r",
    vmin=-1, vmax=1,
    center=0,
    square=True,
    linewidths=1,
    cbar_kws={"shrink": 0.8},
    annot_kws={"size": 11}
)
plt.title("Pima Indians Diabetes — Feature Correlation Heatmap", fontsize=16, fontweight="bold", pad=20)
plt.tight_layout()
plt.savefig("pima_heatmap.png", dpi=200, bbox_inches="tight")
plt.show()
print("Heatmap saved to pima_heatmap.png")
