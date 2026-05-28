import api from './index'

export interface Quote {
  id: number
  content: string
  category: string
}

export const quoteApi = {
  random: () => api.get('/quotes/random')
}
