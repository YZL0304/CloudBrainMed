import pandas as pd
import matplotlib.pyplot as plt
import xgboost as xgb
from sklearn.metrics import mean_squared_error, r2_score

plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# ===================== 1. 读取数据 =====================
glucoseDataFile = r'datasets/cgm_glucose/glucose_01.csv'
glucoseData = pd.read_csv(glucoseDataFile)

# ===================== 2. 时间清洗 =====================
new_glucose_data = glucoseData.copy()
new_glucose_data['datetime'] = pd.to_datetime(new_glucose_data['date'] + ' ' + new_glucose_data['time'])
new_glucose_data = new_glucose_data.drop(columns=['date', 'time']).sort_values('datetime')
new_glucose_data = new_glucose_data.drop_duplicates(subset='datetime')
asc = new_glucose_data.reset_index(drop=True)

# ===================== 3. 绘制血糖图 =====================
fig, [ax, ax1] = plt.subplots(2, 1, figsize=(10, 6))
start_date = '2014-10-01'
oneDayGlucose = asc[asc['datetime'].dt.date == pd.to_datetime(start_date).date()]
ax.plot(oneDayGlucose['datetime'], oneDayGlucose['glucose'], marker='o', color='b', label='血糖')
ax.set_title('一天血糖变化')
ax.grid(True)

ax1.plot(asc['datetime'], asc['glucose'], color='b', label='血糖')
ax1.set_title('全部血糖趋势')
ax1.grid(True)
plt.tight_layout()
plt.show()

# ===================== 4. 特征工程 =====================
asc['delta_t'] = (asc['datetime'] - asc['datetime'].shift(6)).dt.total_seconds() / 60
asc['valid_sd'] = ~asc['delta_t'].isna() & (asc['delta_t'] < 31)

# 滑动窗口标准差
asc['sd_window'] = asc['glucose'].rolling(7).std()
imputed_val = asc.loc[asc['valid_sd'], 'sd_window'].mean()
asc['sd_window'] = asc['sd_window'].fillna(imputed_val)

# 时间段特征
asc['local_hour'] = asc['datetime'].dt.hour
asc['day_q_1'] = asc['local_hour'].between(5, 10, inclusive='left')
asc['day_q_2'] = asc['local_hour'].between(11, 16, inclusive='left')
asc['day_q_3'] = asc['local_hour'].between(17, 22, inclusive='left')
asc['day_q_4'] = ~asc['day_q_1'] & ~asc['day_q_2'] & ~asc['day_q_3']

# ===================== 5. 构建预测目标（预测未来血糖） =====================
asc['target'] = asc['glucose'].shift(-6)  # 预测6步后
asc = asc.dropna(subset=['target'])

# ===================== 6. 训练用的特征（只留数字！！！） =====================
feature_cols = [
    'glucose', 'sd_window', 'valid_sd',
    'day_q_1', 'day_q_2', 'day_q_3', 'day_q_4'
]

# 只取数字特征，删除 datetime、type、字符串等
X = asc[feature_cols]
y = asc['target']

# ===================== 7. 时间序列划分数据集 =====================
train_size = int(len(asc) * 0.9)
X_train, X_test = X.iloc[:train_size], X.iloc[train_size:]
y_train, y_test = y.iloc[:train_size], y.iloc[train_size:]

# ===================== 8. XGBoost 训练 =====================
dtrain = xgb.DMatrix(X_train, label=y_train)
dtest = xgb.DMatrix(X_test, label=y_test)
'''
params = {
    'max_depth': 2,
    'eta': 0.1,          # 修复：原来 eta=1 太大，无法学习
    'objective': 'reg:squarederror',
    'seed': 42
}
'''


params = {
    'max_depth': 3,        # 树不要太深，3~4最合适
    'eta': 0.08,           # 学习率稍微降一点，更稳
    'subsample': 0.85,     # 行采样防过拟合
    'colsample_bytree': 0.85,  # 列采样
    'gamma': 0.1,          # 叶子节点最小损失下降
    'reg_alpha': 0.3,      # L1正则
    'reg_lambda': 1.2,     # L2正则
    'objective': 'reg:squarederror',
    'seed': 42
}

model = xgb.train(params, dtrain, num_boost_round=150)
y_pred = model.predict(dtest)

# ===================== 9. 模型评估 =====================
mse = mean_squared_error(y_test, y_pred)
r2 = r2_score(y_test, y_pred)
print(f"均方误差 (MSE): {mse:.2f}")
print(f"决定系数 (R²): {r2:.2f}")

# ===================== 10. 绘图 =====================
plt.figure(figsize=(14, 7))
plt.plot(y_test.values[:100], label='实际值', color='blue')
plt.plot(y_pred[:100], label='预测值', color='red', alpha=0.7)
plt.xlabel('时间点')
plt.ylabel('血糖值')
plt.title('血糖预测结果')
plt.legend()
plt.show()

# 散点图
plt.figure(figsize=(8, 8))
plt.scatter(y_test, y_pred, alpha=0.6)
plt.xlabel('实际值')
plt.ylabel('预测值')
plt.title('血糖真实值 vs 预测值')
plt.show()