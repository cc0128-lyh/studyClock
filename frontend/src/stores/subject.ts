import { defineStore } from 'pinia'
import { ref } from 'vue'
import { subjectApi, type Subject } from '@/api/subject'

export const useSubjectStore = defineStore('subject', () => {
  const list = ref<Subject[]>([])

  async function fetchList() {
    try {
      const res: any = await subjectApi.list()
      list.value = res.data || []
    } catch (_) {}
  }

  async function addSubject(name: string) {
    await subjectApi.create(name)
    await fetchList()
  }

  async function removeSubject(id: number) {
    await subjectApi.delete(id)
    await fetchList()
  }

  async function renameSubject(id: number, name: string) {
    await subjectApi.update(id, name)
    await fetchList()
  }

  return { list, fetchList, addSubject, removeSubject, renameSubject }
})
