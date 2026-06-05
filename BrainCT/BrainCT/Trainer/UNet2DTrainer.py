import torch
import tqdm
from BrainCT.Utils.EvalMetrics import MetricCalculator


class Trainer:
    def __init__(
            self,
            model,
            loss_fn,
            optimizer,
            device,
            save_path,
            epochs,
            scheduler=None  # 🔥 新增 scheduler
    ):
        # 核心组件
        self.model = model
        self.loss_fn = loss_fn
        self.optimizer = optimizer
        self.device = device
        self.scheduler = scheduler  # 🔥 加入学习率调度器

        # 训练配置
        self.epochs = epochs
        self.save_path = save_path  # 最优模型保存路径

        # 记录历史
        self.history = {
            "train_loss": [],
            "val_loss": [],
            "acc": [],
            "precision": [],
            "recall": [],
            "dice": []
        }
        self.best_dice = 0.0

    # ------------------- 单轮训练 -------------------
    def train_one_epoch(self, loader):
        self.model.train()
        total_loss = 0
        bar = tqdm.tqdm(loader, desc="Training")

        for img, gt in bar:
            # ✅ 确保 img 和 gt 都在正确设备上
            img = img.to(self.device)
            gt = gt.to(self.device)

            pred = self.model(img)
            loss = self.loss_fn(pred, gt)

            self.optimizer.zero_grad()
            loss.backward()
            self.optimizer.step()

            total_loss += loss.item()
            bar.set_postfix(loss=loss.item())

        return total_loss / len(loader)

    # ------------------- 单轮验证【已加进度条】 -------------------
    @torch.no_grad()
    def val_one_epoch(self, loader):
        self.model.eval()
        total_loss = 0
        total_pre = 0
        total_rec = 0
        total_dice = 0
        total_acc = 0

        # 🔥 🔥 🔥 验证集进度条（和训练一样美观）
        val_bar = tqdm.tqdm(loader, desc="Validating")

        for img, gt in val_bar:
            # ✅ 确保 img 和 gt 都在正确设备上
            img = img.to(self.device)
            gt = gt.to(self.device)

            pred = self.model(img)
            loss = self.loss_fn(pred, gt)
            total_loss += loss.item()

            # 实时显示验证损失
            val_bar.set_postfix(val_loss=loss.item())

            p, r, d, a = MetricCalculator.calculate_metrics(pred, gt)

            total_pre += p
            total_rec += r
            total_dice += d
            total_acc += a

        avg_loss = total_loss / len(loader)
        avg_pre = total_pre / len(loader)
        avg_rec = total_rec / len(loader)
        avg_dice = total_dice / len(loader)
        avg_acc = total_acc / len(loader)

        return avg_loss, avg_pre, avg_rec, avg_dice, avg_acc

    # ------------------- 完整训练循环（核心！） -------------------
    def Train(self, train_loader, val_loader):
        print(f"🚀 开始训练，设备：{self.device}")
        print(f"最优模型将保存至：{self.save_path}\n")

        for epoch in range(self.epochs):
            print(f"======== Epoch {epoch + 1}/{self.epochs} ========")

            # 1. 训练
            train_loss = self.train_one_epoch(train_loader)

            # 2. 验证
            val_loss, pre, rec, dice, acc = self.val_one_epoch(val_loader)

            # 3. 学习率调度 🔥🔥🔥
            if self.scheduler is not None:
                self.scheduler.step()

            # 4. 保存历史
            self.history["train_loss"].append(train_loss)
            self.history["val_loss"].append(val_loss)
            self.history["acc"].append(acc)
            self.history["precision"].append(pre)
            self.history["recall"].append(rec)
            self.history["dice"].append(dice)

            # 5. 打印日志
            print(f"Train Loss: {train_loss:.4f} | Val Loss: {val_loss:.4f}")
            print(f"Acc: {acc:.4f} | Precision: {pre:.4f} | Recall: {rec:.4f} | Dice: {dice:.4f}")

            # 6. 保存最优模型
            if dice > self.best_dice:
                self.best_dice = dice
                torch.save(self.model.state_dict(), self.save_path)
                print("✅ 已保存最优模型！\n")

        return self.history