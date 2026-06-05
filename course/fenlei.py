import pickle
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from scipy import stats
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import (
	accuracy_score, confusion_matrix, classification_report,
	roc_auc_score, roc_curve, precision_recall_curve, average_precision_score
)

# 全局设置
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False


# ==============================
# 1. 数据处理类
# ==============================
class DataProcessor:
	def __init__(self, file_path='breast_cancer_pca.csv'):
		self.file_path = file_path
		self.df = None
		self.X = None
		self.y = None
		self.X_train = None
		self.X_test = None
		self.y_train = None
		self.y_test = None

	def load_data(self):
		self.df = pd.read_csv(self.file_path)
		self.X = self.df[['PC1', 'PC2']]
		self.y = self.df['target']
		return self.df, self.X, self.y

	def split_data(self, test_size=0.3, random_state=42):
		self.X_train, self.X_test, self.y_train, self.y_test = train_test_split(
			self.X, self.y, test_size=test_size, random_state=random_state, stratify=self.y
		)
		return self.X_train, self.X_test, self.y_train, self.y_test


# ==============================
# 2. 模型类
# ==============================
class RFModel:
	def __init__(self, model_path="random_forest_model.pkl"):
		self.model_path = model_path
		self.model = None
		self.y_pred = None
		self.y_proba = None

	def train(self, X_train, y_train):
		self.model = RandomForestClassifier(
			n_estimators=200,
			random_state=42,
			oob_score=True
		)
		self.model.fit(X_train, y_train)
		self._save_model()
		return self.model

	def _save_model(self):
		with open(self.model_path, "wb") as f:
			pickle.dump(self.model, f)

	def load_model(self):
		with open(self.model_path, "rb") as f:
			self.model = pickle.load(f)
		return self.model

	def predict(self, X_test):
		self.y_pred = self.model.predict(X_test)
		self.y_proba = self.model.predict_proba(X_test)[:, 1]
		return self.y_pred, self.y_proba

	def evaluate(self, y_test):
		print("\n=== 模型评估结果 ===")
		print(f"准确率: {accuracy_score(y_test, self.y_pred):.4f}")
		print(f"AUC: {roc_auc_score(y_test, self.y_proba):.4f}")

		cm = confusion_matrix(y_test, self.y_pred)
		print("\n混淆矩阵:")
		print(cm)
		print("\n分类报告:")
		print(classification_report(y_test, self.y_pred))
		return cm


# ==============================
# 3. 可视化类
# ==============================
class ModelVisualizer:
	@staticmethod
	def plot_fitting_curve(rf, X_train, y_train, X_test, y_test):
		n_trees = np.arange(10, 201, 10)
		train_accs, test_accs, oob_errors = [], [], []

		for n in n_trees:
			model = RandomForestClassifier(n_estimators=n, random_state=42, oob_score=True)
			model.fit(X_train, y_train)
			train_accs.append(accuracy_score(y_train, model.predict(X_train)))
			test_accs.append(accuracy_score(y_test, model.predict(X_test)))
			oob_errors.append(1 - model.oob_score_)

		plt.figure(figsize=(10, 4))
		plt.subplot(1, 2, 1)
		plt.plot(n_trees, train_accs, label='训练准确率', c='blue')
		plt.plot(n_trees, test_accs, label='测试准确率', c='red')
		plt.xlabel('决策树数量')
		plt.ylabel('准确率')
		plt.title('随机森林拟合曲线（准确率）')
		plt.legend()
		plt.grid(alpha=0.3)

		plt.subplot(1, 2, 2)
		plt.plot(n_trees, oob_errors, c='purple', label='OOB 误差')
		plt.xlabel('决策树数量')
		plt.ylabel('误差')
		plt.title('OOB 误差曲线（拟合损失）')
		plt.legend()
		plt.grid(alpha=0.3)
		plt.tight_layout()
		plt.show()

	@staticmethod
	def plot_confusion_matrix(cm):
		plt.figure(figsize=(6, 5))
		sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=['恶性', '良性'], yticklabels=['恶性', '良性'])
		plt.xlabel('预测标签')
		plt.ylabel('真实标签')
		plt.title('混淆矩阵')
		plt.show()

	@staticmethod
	def plot_normalized_confusion_matrix(cm):
		cm_norm = cm.astype('float') / cm.sum(axis=1, keepdims=True)
		plt.figure(figsize=(6, 5))
		sns.heatmap(cm_norm, annot=True, fmt='.2%', cmap='Greens', xticklabels=['恶性', '良性'],
					yticklabels=['恶性', '良性'])
		plt.xlabel('预测标签')
		plt.ylabel('真实标签')
		plt.title('归一化混淆矩阵（百分比）')
		plt.show()

	@staticmethod
	def plot_precision_recall_curve(y_test, y_scores):
		precision, recall, _ = precision_recall_curve(y_test, y_scores)
		ap = average_precision_score(y_test, y_scores)
		plt.figure(figsize=(7, 5))
		plt.plot(recall, precision, lw=2, label=f'AP = {ap:.3f}')
		plt.xlabel('召回率')
		plt.ylabel('精确率')
		plt.title('精确率-召回率曲线')
		plt.legend()
		plt.grid(alpha=0.3)
		plt.show()

	@staticmethod
	def plot_recall_curve(y_test, y_scores):
		precision, recall, thresholds_pr = precision_recall_curve(y_test, y_scores)
		plt.figure(figsize=(7, 5))
		plt.plot(thresholds_pr, recall[:-1], lw=2, c='red', label='召回率')
		plt.xlabel('阈值')
		plt.ylabel('召回率')
		plt.title('召回率随阈值变化曲线')
		plt.legend()
		plt.grid(alpha=0.3)
		plt.show()

	@staticmethod
	def plot_accuracy_curve(y_test, y_scores):
		thresholds = np.linspace(0, 1, 100)
		accuracies = [accuracy_score(y_test, (y_scores >= t).astype(int)) for t in thresholds]
		plt.figure(figsize=(7, 5))
		plt.plot(thresholds, accuracies, lw=2, c='green')
		plt.xlabel('阈值')
		plt.ylabel('准确度')
		plt.title('阈值-准确度曲线')
		plt.grid(alpha=0.3)
		plt.show()

	@staticmethod
	def plot_precision_curve(y_test, y_scores):
		precision, _, thresholds_pr = precision_recall_curve(y_test, y_scores)
		plt.figure(figsize=(7, 5))
		plt.plot(thresholds_pr, precision[:-1], lw=2, c='orange', label='精确率')
		plt.xlabel('阈值')
		plt.ylabel('精确率')
		plt.title('精确率随阈值变化曲线')
		plt.legend()
		plt.grid(alpha=0.3)
		plt.show()

	@staticmethod
	def plot_z_score_curve(y_scores):
		z_scores = stats.zscore(y_scores)
		plt.figure(figsize=(7, 5))
		plt.plot(range(len(z_scores)), np.sort(z_scores), lw=2, c='purple')
		plt.xlabel('样本排序')
		plt.ylabel('Z分数')
		plt.title('预测概率 Z 分数曲线')
		plt.grid(alpha=0.3)
		plt.show()

	@staticmethod
	def plot_roc_curve(y_test, y_proba):
		fpr, tpr, _ = roc_curve(y_test, y_proba)
		plt.figure(figsize=(6, 6))
		plt.plot(fpr, tpr, lw=2, label=f'AUC = {roc_auc_score(y_test, y_proba):.4f}')
		plt.plot([0, 1], [0, 1], '--', lw=2)
		plt.xlabel('假阳性率')
		plt.ylabel('真阳性率')
		plt.title('ROC曲线')
		plt.legend()
		plt.show()

	@staticmethod
	def plot_decision_boundary(model, X, y):
		h = 0.02
		x_min, x_max = X['PC1'].min() - 1, X['PC1'].max() + 1
		y_min, y_max = X['PC2'].min() - 1, X['PC2'].max() + 1
		xx, yy = np.meshgrid(np.arange(x_min, x_max, h), np.arange(y_min, y_max, h))
		Z = model.predict(np.c_[xx.ravel(), yy.ravel()]).reshape(xx.shape)
		plt.figure(figsize=(8, 6))
		plt.contourf(xx, yy, Z, alpha=0.3, cmap=plt.cm.coolwarm)
		plt.scatter(X[y == 0]['PC1'], X[y == 0]['PC2'], c='red', label='恶性', s=30, edgecolors='k')
		plt.scatter(X[y == 1]['PC1'], X[y == 1]['PC2'], c='blue', label='良性', s=30, edgecolors='k')
		plt.xlabel('PC1')
		plt.ylabel('PC2')
		plt.title('随机森林决策边界')
		plt.legend()
		plt.show()


# ==============================
# 主流程
# ==============================
class CancerPredictionPipeline:
	def __init__(self):
		self.data_processor = DataProcessor()
		self.model = RFModel()
		self.visualizer = ModelVisualizer()

	def run(self):
		df, X, y = self.data_processor.load_data()
		print("数据预览：")
		print(df.head())

		# 修复：正确接收变量
		X_train, X_test, y_train, y_test = self.data_processor.split_data()

		self.model.train(X_train, y_train)
		self.model.load_model()
		y_pred, y_proba = self.model.predict(X_test)

		cm = self.model.evaluate(y_test)

		self.visualizer.plot_fitting_curve(self.model.model, X_train, y_train, X_test, y_test)
		self.visualizer.plot_confusion_matrix(cm)
		self.visualizer.plot_normalized_confusion_matrix(cm)
		self.visualizer.plot_precision_recall_curve(y_test, y_proba)
		self.visualizer.plot_recall_curve(y_test, y_proba)
		self.visualizer.plot_accuracy_curve(y_test, y_proba)
		self.visualizer.plot_precision_curve(y_test, y_proba)
		self.visualizer.plot_z_score_curve(y_proba)
		self.visualizer.plot_roc_curve(y_test, y_proba)
		self.visualizer.plot_decision_boundary(self.model.model, X, y)


# ==============================
# 运行
# ==============================
if __name__ == "__main__":
	pipeline = CancerPredictionPipeline()
	pipeline.run()