#!/bin/bash
# CT 伪影检测 AI 服务启动脚本
# 用法: bash run.sh [--model unet|attention]

MODEL_TYPE=${1:-unet}
echo "启动 CT 伪影检测 AI 服务 [模型: ${MODEL_TYPE}]"

export MODEL_TYPE="${MODEL_TYPE}"
python CTDetectionServer.py
