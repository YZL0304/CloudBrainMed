# 1. 导入需要的库
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA

plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# 2. 加载本地 CSV 文件 ✅（正确方式）
data = pd.read_csv(r'datasets\breast_cancer.csv')
print(data.columns)

# 3. 分开特征 X 和标签 y
# 假设最后一列是 target（0/1）
X = data.drop('target', axis=1)      # 所有列除了标签
y = data['target']                   # 标签列

# 4. 标准化
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X)

# 5. PCA 降维
pca = PCA(n_components=2)
X_pca = pca.fit_transform(X_scaled)

# 输出信息
print("降维前形状:", X.shape)
print("降维后形状:", X_pca.shape)

print("\n主成分1 方差解释率: {:.2f}%".format(pca.explained_variance_ratio_[0]*100))
print("主成分2 方差解释率: {:.2f}%".format(pca.explained_variance_ratio_[1]*100))
print("累计解释率: {:.2f}%".format(sum(pca.explained_variance_ratio_)*100))

# ===================== 追加：保存PCA降维结果 + 标签到CSV =====================
# 构建包含降维特征和标签的DataFrame
pca_result = pd.DataFrame({
    "PCA1": X_pca[:, 0],
    "PCA2": X_pca[:, 1],
    "target": y  # 直接把标签列加进来
})
# 保存文件
pca_result.to_csv(r"datasets/breast_cancer_pca.csv", index=False, encoding="utf-8-sig")
print("\n✅ 已保存降维结果：breast_cancer_pca.csv")
# ==========================================================================

# 6. 画图
plt.figure(figsize=(8,6))
plt.scatter(X_pca[y==0, 0], X_pca[y==0, 1], label="恶性", c="red", s=30)
plt.scatter(X_pca[y==1, 0], X_pca[y==1, 1], label="良性", c="blue", s=30)

plt.xlabel("PC1")
plt.ylabel("PC2")
plt.title("乳腺癌数据集 PCA 降维")
plt.legend()
plt.show()