import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch
import os

OUT = os.path.dirname(os.path.abspath(__file__))

def draw_box(ax, x, y, w, h, text, color='#4A90D9', text_color='white', fontsize=9):
    box = FancyBboxPatch((x - w/2, y - h/2), w, h,
                          boxstyle="round,pad=0.15",
                          facecolor=color, edgecolor='#2c3e50',
                          linewidth=1.5, alpha=0.95)
    ax.add_patch(box)
    lines = text.split('\n')
    for i, line in enumerate(lines):
        line_y = y + (len(lines)-1)*0.12 - i*0.24
        ax.text(x, line_y, line, ha='center', va='center',
                fontsize=fontsize, color=text_color, fontweight='bold')

def draw_arrow(ax, x1, y1, x2, y2, color='#2c3e50'):
    ax.annotate('', xy=(x2, y2+0.35), xytext=(x1, y1-0.35),
                arrowprops=dict(arrowstyle='->', color=color, lw=2))

# ========== Flowchart 1: 患者端全流程 ==========
fig, ax = plt.subplots(figsize=(10, 18))
ax.set_xlim(0, 10); ax.set_ylim(0, 18); ax.axis('off')
ax.set_facecolor('#f8f9fa'); fig.patch.set_facecolor('#f8f9fa')

ax.text(5, 17.2, '患者端核心业务流程', ha='center', va='center',
        fontsize=16, fontweight='bold', color='#2c3e50')

boxes_patient = [
    (5, 16.0, 4.5, 0.8, '注册 / 登录\n手机号 + 密码 / 验证码'),
    (5, 14.3, 4.5, 0.8, 'AI 预问诊\n症状描述 → 多轮追问 → 主诉提取'),
    (5, 12.6, 4.5, 0.8, '智能导诊\nAI 推荐科室 & 就诊方式'),
    (5, 10.9, 4.5, 0.8, '线上挂号预约\n选择医生/时段 → 锁定号源'),
    (2.5, 9.0, 4.0, 1.0, '线下就诊\n医生接诊 → AI辅助诊断\n→ 检查 / 开方'),
    (7.5, 9.0, 4.0, 1.0, '远程图文咨询\n线上问诊沟通'),
    (5, 7.0, 4.5, 0.8, '电子病历自动生成\n结构化存储 (ICD-10)'),
    (5, 5.2, 4.5, 0.8, '查看病历 / 报告 / 处方'),
    (2.5, 3.5, 4.0, 0.8, '在线支付\n挂号费 / 处方费 / 检查费'),
    (7.5, 3.5, 4.0, 0.8, 'AI 用药指导\n用药说明 & 注意事项'),
    (5, 1.8, 4.5, 0.8, 'AI 智能随访（可选）\n术后恢复建议 / 复诊提醒'),
]

colors_patient = ['#3498db', '#2ecc71', '#2ecc71', '#3498db', '#e74c3c', '#9b59b6',
                  '#f39c12', '#3498db', '#1abc9c', '#2ecc71', '#95a5a6']

for i, (x, y, w, h, text) in enumerate(boxes_patient):
    draw_box(ax, x, y, w, h, text, color=colors_patient[i], fontsize=8)

# Main arrows
for i in range(4):
    draw_arrow(ax, 5, boxes_patient[i][1], 5, boxes_patient[i+1][1])

# Branch to 线下/远程
ax.annotate('', xy=(2.5, 9.5), xytext=(4.2, 10.3),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5, connectionstyle='arc3,rad=-0.2'))
ax.annotate('', xy=(7.5, 9.5), xytext=(5.8, 10.3),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5, connectionstyle='arc3,rad=0.2'))
ax.text(3.5, 10.1, '到院', ha='center', fontsize=7, color='#7f8c8d', fontstyle='italic')
ax.text(6.5, 10.1, '在线', ha='center', fontsize=7, color='#7f8c8d', fontstyle='italic')

# Merge back
draw_arrow(ax, 2.5, 8.5, 5, 7.4)
draw_arrow(ax, 7.5, 8.5, 5, 7.4)
draw_arrow(ax, 5, 6.6, 5, 5.6)

# Branch to payment / medication
draw_arrow(ax, 5, 4.8, 2.5, 3.9)
draw_arrow(ax, 5, 4.8, 7.5, 3.9)
draw_arrow(ax, 2.5, 3.1, 5, 2.2)
draw_arrow(ax, 7.5, 3.1, 5, 2.2)

plt.tight_layout()
fig.savefig(os.path.join(OUT, '_flowchart_patient.png'), dpi=200, bbox_inches='tight',
            facecolor='#f8f9fa', edgecolor='none')
plt.close()
print('Patient flowchart saved')

# ========== Flowchart 2: 医生端流程 ==========
fig, ax = plt.subplots(figsize=(10, 14))
ax.set_xlim(0, 10); ax.set_ylim(0, 14); ax.axis('off')
ax.set_facecolor('#f8f9fa'); fig.patch.set_facecolor('#f8f9fa')

ax.text(5, 13.3, '医生端核心业务流程', ha='center', va='center',
        fontsize=16, fontweight='bold', color='#2c3e50')

boxes_doctor = [
    (5, 12.2, 4.5, 0.8, '医生登录\n独立入口 → JWT 认证'),
    (5, 10.7, 5.0, 0.9, '接诊面板\n就诊列表 / 排队队列 / 当天排班'),
    (5, 9.2, 5.0, 0.9, '进入问诊详情\n查看患者信息 + AI 预问诊结果'),
    (3, 7.3, 3.5, 1.1, 'AI 辅助诊断\n根据主诉+体征+历史病历\n→ TOP3 疾病 + 置信度'),
    (7, 7.3, 3.5, 1.1, 'AI 检查推荐\n判断需做检查项\n(血常规 / CT 等)'),
    (3, 5.4, 3.5, 1.1, 'AI 辅助开方\n根据诊断+体重+过敏史\n→ 推荐药品和剂量'),
    (7, 5.4, 3.5, 1.1, '医嘱质控\n自动检查药品冲突\n剂量范围校验'),
    (5, 3.5, 5.0, 0.9, '医生确认 / 修改 → 电子签名'),
    (5, 2.1, 5.0, 0.9, '处方流转到药房\n病历归档 → 更新健康档案'),
]

colors_doc = ['#3498db', '#2c3e50', '#e74c3c', '#2ecc71', '#2ecc71',
              '#f39c12', '#f39c12', '#e74c3c', '#1abc9c']

for i, (x, y, w, h, text) in enumerate(boxes_doctor):
    draw_box(ax, x, y, w, h, text, color=colors_doc[i], fontsize=8)

draw_arrow(ax, 5, 11.8, 5, 11.2)
draw_arrow(ax, 5, 10.2, 5, 9.7)

# Branch to diagnosis and check recommendation
ax.annotate('', xy=(3, 7.85), xytext=(4.2, 8.75),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5, connectionstyle='arc3,rad=-0.15'))
ax.annotate('', xy=(7, 7.85), xytext=(5.8, 8.75),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5, connectionstyle='arc3,rad=0.15'))

# Both to prescribing
ax.annotate('', xy=(3, 5.95), xytext=(3.5, 6.75),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5))
ax.annotate('', xy=(7, 5.95), xytext=(6.5, 6.75),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5))

# Merge to confirmation
draw_arrow(ax, 3, 4.85, 5, 3.95)
draw_arrow(ax, 7, 4.85, 5, 3.95)
draw_arrow(ax, 5, 3.05, 5, 2.55)

plt.tight_layout()
fig.savefig(os.path.join(OUT, '_flowchart_doctor.png'), dpi=200, bbox_inches='tight',
            facecolor='#f8f9fa', edgecolor='none')
plt.close()
print('Doctor flowchart saved')

# ========== Flowchart 3: 系统架构图 ==========
fig, ax = plt.subplots(figsize=(14, 8))
ax.set_xlim(0, 14); ax.set_ylim(0, 8); ax.axis('off')
ax.set_facecolor('#f8f9fa'); fig.patch.set_facecolor('#f8f9fa')

ax.text(7, 7.5, '系统技术架构 & 数据流', ha='center', va='center',
        fontsize=16, fontweight='bold', color='#2c3e50')

draw_box(ax, 3.5, 6.2, 5, 0.7, '客户端层：Vue 3 + Element Plus\n患者端 ｜ 医生端', '#3498db', fontsize=8)
draw_box(ax, 3.5, 5.0, 5, 0.7, '网关层：Spring Cloud Gateway\n路由转发 ｜ 统一鉴权 ｜ 限流', '#9b59b6', fontsize=8)
draw_box(ax, 3.5, 3.6, 5, 1.2, '微服务层 (Spring Boot + Spring Cloud)\n用户服务 ｜ 挂号服务 ｜ 问诊服务 ｜ AI 服务\n病历服务 ｜ 支付服务 ｜ 通知服务', '#2ecc71', fontsize=8)
draw_box(ax, 1.5, 1.8, 3, 1.2, '数据存储\nMySQL 业务数据\nRedis 缓存 / Session\nElasticsearch 检索', '#e74c3c', fontsize=7.5)
draw_box(ax, 5.5, 1.8, 3, 1.2, '中间件\nKafka / RabbitMQ 消息队列\nMinIO 文件存储\nNacos 注册 / 配置中心', '#f39c12', fontsize=7.5)
draw_box(ax, 9.5, 2.2, 3.8, 0.9, 'AI 引擎\nSpring AI + LLM\n(通义千问 / OpenAI / DeepSeek)', '#1abc9c', fontsize=8)

ax.annotate('', xy=(10.5, 3.8), xytext=(6.2, 3.8),
            arrowprops=dict(arrowstyle='<->', color='#1abc9c', lw=2))
ax.text(8.5, 4.2, 'AI 调用', ha='center', fontsize=7, color='#1abc9c', fontstyle='italic')

draw_arrow(ax, 3.5, 5.65, 3.5, 5.35)
draw_arrow(ax, 3.5, 4.65, 3.5, 4.2)

ax.annotate('', xy=(3, 2.4), xytext=(3.5, 3.0),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5))
ax.annotate('', xy=(7, 2.4), xytext=(5.5, 3.0),
            arrowprops=dict(arrowstyle='->', color='#2c3e50', lw=1.5))

plt.tight_layout()
fig.savefig(os.path.join(OUT, '_flowchart_arch.png'), dpi=200, bbox_inches='tight',
            facecolor='#f8f9fa', edgecolor='none')
plt.close()
print('Architecture flowchart saved')
print('All done!')
