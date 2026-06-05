from torchvision import datasets

# 数据集保存为目录
data_path = r'CIFAR10'
# 下载数据集，即如果在指定路径中找不到CIFAR-10数据集时，自动下载并从压缩文件中提取数据集
train_data = datasets.CIFAR10(data_path, train=True, download=True)
# 从指定的数据集获取验证集，如果数据不存在，会自动下载并提取
cifar10_val = datasets.CIFAR10(data_path, train=False, download=True)
# 查看CIFAR10类的方法解析顺序（MRO）
print(*type(train_data).__mro__, sep="\n")
