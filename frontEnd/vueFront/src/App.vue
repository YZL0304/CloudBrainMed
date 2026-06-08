<script setup lang="ts">
import { useRoute } from 'vue-router'
import { computed, ref } from 'vue'
import { ElIcon } from 'element-plus'
import {
  UserFilled,
  List,
  DataAnalysis,
  CollectionTag,
  Cpu,
  Camera,
} from '@element-plus/icons-vue'

const route = useRoute()
const collapsed = ref(false)

// 判断是否需要显示侧栏
const showSidebar = computed(() => {
  return route.path !== '/login'
})

const iconMap: Record<string, any> = {
  UserFilled,
  List,
  DataAnalysis,
  CollectionTag,
  Cpu,
  Camera,
}

const menuItems = [
  { path: '/doctor/profile', title: '医生个人信息', icon: 'UserFilled', group: '医生端' },
  { path: '/doctor/consult', title: '接诊工作台', icon: 'List', group: '医生端' },
  { path: '/admin/ml/dashboard', title: 'AI 推理看板', icon: 'DataAnalysis', group: '管理员端' },
  { path: '/admin/ml/samples', title: '样本标注', icon: 'CollectionTag', group: '管理员端' },
  { path: '/admin/ml/models', title: '模型管理', icon: 'Cpu', group: '管理员端' },
  { path: '/admin/ml/ct-inference', title: 'CT 伪影检测', icon: 'Camera', group: '管理员端' },
]

function showGroupLabel(item: typeof menuItems[number]) {
  const idx = menuItems.indexOf(item)
  return idx > 0 && menuItems[idx - 1]!.group !== item.group
}
</script>

<template>
  <div class="app-shell" v-if="showSidebar">
    <aside class="sidebar" :class="{ collapsed }">
      <div class="brand" @click="$router.push('/')">
        <div class="brand-icon">
          <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
            <rect width="28" height="28" rx="8" fill="#2563eb"/>
            <path d="M6 14h4l2-5 4 10 2-5h4" stroke="#fff" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="brand-text" v-show="!collapsed">CloudBrain<span class="brand-accent">Med</span></span>
      </div>

      <nav class="nav">
        <router-link to="/" class="nav-item home-link">
          <el-icon :size="20"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg></el-icon>
          <span v-show="!collapsed">首页概览</span>
        </router-link>

        <template v-for="item in menuItems" :key="item.path">
          <div class="nav-group-label" v-show="!collapsed && showGroupLabel(item)">
            {{ item.group }}
          </div>
          <router-link :to="item.path" class="nav-item">
            <el-icon :size="20"><component :is="iconMap[item.icon]" /></el-icon>
            <span v-show="!collapsed">{{ item.title }}</span>
          </router-link>
        </template>
      </nav>

      <div class="sidebar-footer" v-show="!collapsed">
        <div class="status-dot"></div>
        <span>系统运行中</span>
      </div>
    </aside>

    <main class="main">
      <RouterView />
    </main>
  </div>
  <!-- 登录页面独立显示 -->
  <RouterView v-else />
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap');

:root {
  --sidebar-w: 240px;
  --brand: #2563eb;
  --brand-light: #3b82f6;
  --brand-dark: #1d4ed8;
  --surface: #ffffff;
  --bg: #f1f5f9;
  --text: #1e293b;
  --text-muted: #64748b;
  --border: #e2e8f0;
  --radius: 12px;
  --shadow: 0 1px 3px rgba(0,0,0,.06), 0 1px 2px rgba(0,0,0,.04);
  --shadow-md: 0 4px 6px rgba(0,0,0,.04), 0 2px 4px rgba(0,0,0,.04);
}

* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: 'Inter', 'PingFang SC', 'Microsoft YaHei', sans-serif; background: var(--bg); color: var(--text); -webkit-font-smoothing: antialiased; }
#app { width: 100%; height: 100vh; }
</style>

<style scoped>
.app-shell { display: flex; height: 100vh; overflow: hidden; }

.sidebar {
  width: var(--sidebar-w); min-width: var(--sidebar-w);
  background: linear-gradient(180deg, #0f172a 0%, #1e293b 100%);
  display: flex; flex-direction: column;
  transition: width .25s ease, min-width .25s ease;
  position: relative; z-index: 10;
}
.sidebar.collapsed { width: 68px; min-width: 68px; }

.brand {
  display: flex; align-items: center; gap: 10px;
  padding: 22px 20px; cursor: pointer;
  border-bottom: 1px solid rgba(255,255,255,.06);
}
.brand-icon { flex-shrink: 0; }
.brand-text { font-size: 18px; font-weight: 700; color: #f1f5f9; letter-spacing: -.3px; white-space: nowrap; }
.brand-accent { color: #60a5fa; }

.nav { flex: 1; padding: 12px 10px; overflow-y: auto; display: flex; flex-direction: column; gap: 2px; }
.nav-group-label { font-size: 11px; font-weight: 600; color: #64748b; text-transform: uppercase; letter-spacing: .5px; padding: 12px 12px 4px; margin-top: 4px; }
.nav-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 12px; border-radius: 8px;
  color: #94a3b8; text-decoration: none;
  font-size: 14px; font-weight: 500;
  transition: all .15s ease; white-space: nowrap;
}
.nav-item:hover { background: rgba(255,255,255,.06); color: #e2e8f0; }
.nav-item.router-link-active { background: rgba(37,99,235,.25); color: #bfdbfe; }
.nav-item.router-link-active::before { content: ''; position: absolute; left: 0; top: 8px; bottom: 8px; width: 3px; background: var(--brand); border-radius: 0 3px 3px 0; }
.nav-item { position: relative; }

.home-link { margin-bottom: 4px; }

.sidebar-footer {
  padding: 14px 20px; border-top: 1px solid rgba(255,255,255,.06);
  display: flex; align-items: center; gap: 8px;
  font-size: 12px; color: #64748b;
}
.status-dot { width: 7px; height: 7px; border-radius: 50%; background: #22c55e; box-shadow: 0 0 6px rgba(34,197,94,.5); }

.main { flex: 1; overflow-y: auto; overflow-x: hidden; background: var(--bg); }
</style>