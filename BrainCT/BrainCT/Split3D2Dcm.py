import nibabel as nib
import numpy as np
import pydicom
from pydicom.dataset import FileDataset, FileMetaDataset
import os
import cv2
from glob import glob


def save_single_case(ct_path, mask_path, output_root_ct, output_root_mask, dtype=np.int16):
	"""
	处理单个病例：CT + 掩码 → 仅保存掩码>0的DICOM切片
	"""
	# 原始文件名（去除.nii/.gz后缀作为病例名）
	basename = os.path.basename(ct_path)
	case_name = basename.replace(".nii.gz", "").replace(".nii", "")

	# 每个病例单独创建文件夹
	output_ct_dir = os.path.join(output_root_ct, case_name)
	output_mask_dir = os.path.join(output_root_mask, case_name)
	os.makedirs(output_ct_dir, exist_ok=True)
	os.makedirs(output_mask_dir, exist_ok=True)

	# 读取NII
	ct_nii = nib.load(ct_path)
	mask_nii = nib.load(mask_path)
	ct_data = ct_nii.get_fdata()
	mask_data = mask_nii.get_fdata()

	# 维度转换 (X,Y,Z) → (Z,Y,X)
	ct_data = np.transpose(ct_data, (2, 1, 0))
	mask_data = np.transpose(mask_data, (2, 1, 0))

	# 筛选有效切片
	valid_slices = [z for z in range(ct_data.shape[0]) if np.any(mask_data[z] > 0)]
	if not valid_slices:
		print(f"[{case_name}] 无有效掩码切片，跳过")
		return

	print(f"[{case_name}] 找到 {len(valid_slices)} 张有效切片")

	# DICOM 头文件
	file_meta = FileMetaDataset()
	file_meta.MediaStorageSOPClassUID = '1.2.840.10008.5.1.4.1.1.2'
	file_meta.MediaStorageSOPInstanceUID = pydicom.uid.generate_uid()
	file_meta.ImplementationClassUID = pydicom.uid.generate_uid()
	file_meta.TransferSyntaxUID = pydicom.uid.ImplicitVRLittleEndian

	# 保存每一张有效切片
	for _, z in enumerate(valid_slices):
		ct_slice = ct_data[z].astype(dtype)
		mask_slice = mask_data[z].astype(np.uint8)

		# 方向校正
		ct_slice = cv2.rotate(ct_slice, cv2.ROTATE_90_CLOCKWISE)
		mask_slice = cv2.rotate(mask_slice, cv2.ROTATE_90_CLOCKWISE)
		ct_slice = np.flipud(ct_slice)
		mask_slice = np.flipud(mask_slice)

		# 文件名：原文件名_切片序号.dcm
		save_name = f"{case_name}_slice_{z:03d}.dcm"

		# ---------------- CT DICOM ----------------
		ct_ds = FileDataset(None, {}, file_meta=file_meta, preamble=b"\x00" * 128)
		ct_ds.PatientName = case_name
		ct_ds.PatientID = case_name
		ct_ds.StudyInstanceUID = pydicom.uid.generate_uid()
		ct_ds.SeriesInstanceUID = pydicom.uid.generate_uid()
		ct_ds.SOPInstanceUID = pydicom.uid.generate_uid()
		ct_ds.Rows, ct_ds.Columns = ct_slice.shape
		ct_ds.PixelData = ct_slice.tobytes()
		ct_ds.BitsAllocated = 16
		ct_ds.BitsStored = 16
		ct_ds.HighBit = 15
		ct_ds.PixelRepresentation = 1
		ct_ds.SamplesPerPixel = 1
		ct_ds.PhotometricInterpretation = "MONOCHROME2"
		ct_ds.WindowCenter = np.mean(ct_slice)
		ct_ds.WindowWidth = np.max(ct_slice) - np.min(ct_slice)
		ct_ds.save_as(os.path.join(output_ct_dir, save_name), write_like_original=False)

		# ---------------- 掩码 DICOM ----------------
		mask_ds = FileDataset(None, {}, file_meta=file_meta, preamble=b"\x00" * 128)
		mask_ds.PatientName = f"{case_name}_mask"
		mask_ds.PatientID = f"{case_name}_mask"
		mask_ds.StudyInstanceUID = pydicom.uid.generate_uid()
		mask_ds.SeriesInstanceUID = pydicom.uid.generate_uid()
		mask_ds.SOPInstanceUID = pydicom.uid.generate_uid()
		mask_ds.Rows, mask_ds.Columns = mask_slice.shape
		mask_ds.PixelData = mask_slice.tobytes()
		mask_ds.BitsAllocated = 8
		mask_ds.BitsStored = 8
		mask_ds.HighBit = 7
		mask_ds.PixelRepresentation = 0
		mask_ds.SamplesPerPixel = 1
		mask_ds.PhotometricInterpretation = "MONOCHROME2"
		mask_ds.save_as(os.path.join(output_mask_dir, save_name), write_like_original=False)


def batch_process_all_cases(ct_folder, mask_folder, output_root="output_dicom"):
	"""
	批量处理文件夹中所有配对的CT和掩码
	"""
	# 输出根目录
	output_root_ct = os.path.join(output_root, "CT")
	output_root_mask = os.path.join(output_root, "MASK")
	os.makedirs(output_root_ct, exist_ok=True)
	os.makedirs(output_root_mask, exist_ok=True)

	# 获取所有CT文件
	ct_files = sorted(glob(os.path.join(ct_folder, "*.nii*")))
	print(f"找到 {len(ct_files)} 个CT文件")

	for ct_path in ct_files:
		filename = os.path.basename(ct_path)
		mask_path = os.path.join(mask_folder, filename)

		if not os.path.exists(mask_path):
			print(f"未找到掩码：{filename}，跳过")
			continue

		# 处理这个病例
		save_single_case(ct_path, mask_path, output_root_ct, output_root_mask)

	print("\n✅ 全部病例处理完成！")


# ==================== 【只需修改这里的路径】 ====================
if __name__ == "__main__":
	# CT文件夹
	CT_FOLDER = r"CT"
	# 掩码文件夹
	MASK_FOLDER = r"mask"
	# 输出目录
	OUTPUT_ROOT = r"batch_dicom_output"

	batch_process_all_cases(CT_FOLDER, MASK_FOLDER, OUTPUT_ROOT)