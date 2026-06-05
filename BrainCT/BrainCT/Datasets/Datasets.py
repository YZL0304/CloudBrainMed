import os
import numpy as np
import torch
from torch.utils.data import Dataset
import pydicom
import torchvision.transforms.functional as TF

class CTSliceDataset(Dataset):
    def __init__(self, ct_root, mask_root):
        self.ct_root = ct_root
        self.mask_root = mask_root
        self.ct_list = []
        self.mask_list = []
        self.is_train = False

        all_dcm = sorted([f for f in os.listdir(ct_root) if f.endswith(".dcm")])
        for fname in all_dcm:
            ct_path = os.path.join(ct_root, fname)
            mask_path = os.path.join(mask_root, fname)
            if os.path.exists(mask_path):
                self.ct_list.append(ct_path)
                self.mask_list.append(mask_path)
        print(f"数据集总切片数量：{len(self.ct_list)}")

    def read_dcm(self, path):
        dcm = pydicom.dcmread(path)
        arr = dcm.pixel_array.astype(np.float32)
        return arr

    def __len__(self):
        return len(self.ct_list)

    def __getitem__(self, idx):
        ct_arr = self.read_dcm(self.ct_list[idx])
        mask_arr = self.read_dcm(self.mask_list[idx])

        ct_arr = (ct_arr - ct_arr.mean()) / (ct_arr.std() + 1e-7)
        mask_arr = (mask_arr > 0).astype(np.float32)

        ct = torch.from_numpy(ct_arr).unsqueeze(0)
        mask = torch.from_numpy(mask_arr).unsqueeze(0)

        if self.is_train:
            if torch.rand(1) > 0.5:
                ct = TF.hflip(ct)
                mask = TF.hflip(mask)
            if torch.rand(1) > 0.5:
                ct = TF.vflip(ct)
                mask = TF.vflip(mask)
            if torch.rand(1) > 0.5:
                rot_angle = np.random.randint(-10, 11)
                ct = TF.rotate(ct, rot_angle)
                mask = TF.rotate(mask, rot_angle)

        return ct, mask