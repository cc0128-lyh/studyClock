<template>
  <div class="subject-manage-view">
    <button class="back-btn" @click="goBack">← 返回</button>
    <h2>学科管理</h2>

    <div class="subject-form">
      <input v-model="newName" placeholder="学科名称" maxlength="20" @keyup.enter="addSubject" />
      <button class="btn-save-sm" @click="addSubject">添加</button>
    </div>

    <div class="subject-list">
      <div v-for="s in subjectStore.list" :key="s.id" class="subject-item">
        <span class="subject-name">{{ s.name }}</span>
        <button class="btn-del" @click="subjectStore.removeSubject(s.id)">✕</button>
      </div>
      <div v-if="subjectStore.list.length === 0" class="empty-hint">暂无学科，添加一个吧</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSubjectStore } from '@/stores/subject'

const router = useRouter()
const subjectStore = useSubjectStore()
const newName = ref('')

function goBack() {
  router.push('/settings')
}

async function addSubject() {
  const name = newName.value.trim()
  if (!name) return
  await subjectStore.addSubject(name)
  newName.value = ''
}

onMounted(() => {
  subjectStore.fetchList()
})
</script>

<style scoped>
.subject-manage-view {
  max-width: 500px;
  margin: 0 auto;
  padding: 2rem;
}
.back-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.3rem 0;
  margin-bottom: 0.5rem;
  transition: color 0.2s;
}
.back-btn:hover { color: var(--text-primary); }
h2 {
  font-weight: 400;
  letter-spacing: 0.1em;
  margin-bottom: 2rem;
  text-align: center;
}

.subject-form { display: flex; gap: 0.5rem; margin-bottom: 1.5rem; }
.subject-form input {
  flex: 1;
  padding: 0.6rem 1rem;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  color: var(--text-primary);
  font-size: 0.95rem;
  outline: none;
}
.subject-form input:focus { border-color: var(--accent-color); }
.btn-save-sm {
  padding: 0.6rem 1.2rem;
  background: var(--accent-color);
  border: none;
  border-radius: 10px;
  color: var(--text-inverse);
  cursor: pointer;
  white-space: nowrap;
  font-weight: 500;
}

.subject-list {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 0.3rem;
}
.subject-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6rem 0.8rem;
  background: var(--bg-card);
  border-radius: 8px;
}
.subject-name { font-size: 0.95rem; }
.btn-del {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.9rem;
  padding: 0.2rem 0.4rem;
}
.btn-del:hover { color: var(--accent-color); }
.empty-hint { font-size: 0.85rem; opacity: 0.4; text-align: center; padding: 2rem; }
</style>
