<template>
  <div class="pie-chart">
    <svg :width="size" :height="size" viewBox="0 0 300 300">
      <!-- background circle -->
      <circle cx="150" cy="150" r="75" fill="none" stroke="rgba(255,255,255,0.06)" stroke-width="16" />
      <!-- slices -->
      <path
        v-for="(slice, i) in slices"
        :key="i"
        :d="slice.path"
        :fill="slice.color"
        stroke="var(--panel-bg)"
        stroke-width="1"
        class="pie-slice"
      />
      <!-- leader lines + labels -->
      <g v-for="(item, i) in labelItems" :key="i">
        <line
          :x1="item.line.x1" :y1="item.line.y1"
          :x2="item.line.x2" :y2="item.line.y2"
          stroke="rgba(255,255,255,0.25)"
          stroke-width="1"
        />
        <circle :cx="item.line.x1" :cy="item.line.y1" r="2.5" :fill="item.color" />
        <text
          :x="item.text.x" :y="item.text.y"
          :text-anchor="item.text.anchor"
          fill="#fff"
          font-size="11"
        >{{ item.text.name }}</text>
        <text
          :x="item.text.x" :y="item.text.y + 15"
          :text-anchor="item.text.anchor"
          fill="rgba(255,255,255,0.5)"
          font-size="9"
        >{{ item.text.value }}</text>
      </g>
      <!-- center text -->
      <text x="150" y="144" text-anchor="middle" class="center-total">{{ totalLabel }}</text>
      <text x="150" y="162" text-anchor="middle" class="center-unit">总计</text>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface PieDataItem {
  name: string
  value: number
}

const props = withDefaults(defineProps<{
  data: PieDataItem[]
  size?: number
  totalLabel?: string
}>(), {
  size: 280,
  totalLabel: ''
})

const colors = [
  '#4fc3f7', '#ff8a65', '#81c784', '#ba68c8',
  '#ffd54f', '#4dd0e1', '#a1887f', '#90a4ae',
  '#e57373', '#7986cb'
]

const total = computed(() => props.data.reduce((s, d) => s + d.value, 0))

const cx = 150, cy = 150, r = 75, labelR = 110

const slices = computed(() => {
  const result: { path: string; color: string }[] = []
  let cumAngle = -Math.PI / 2

  for (let i = 0; i < props.data.length; i++) {
    const item = props.data[i]
    const fraction = total.value > 0 ? item.value / total.value : 0
    const angle = fraction * 2 * Math.PI

    if (fraction === 0) {
      result.push({ path: '', color: colors[i % colors.length] })
      continue
    }

    const x1 = cx + r * Math.cos(cumAngle)
    const y1 = cy + r * Math.sin(cumAngle)
    const x2 = cx + r * Math.cos(cumAngle + angle)
    const y2 = cy + r * Math.sin(cumAngle + angle)

    const largeArc = fraction > 0.5 ? 1 : 0

    const path = total.value === item.value
      ? `M ${cx} ${cy - r} A ${r} ${r} 0 1 1 ${cx - 0.01} ${cy - r} A ${r} ${r} 0 1 1 ${cx} ${cy - r}`
      : `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 ${largeArc} 1 ${x2} ${y2} Z`

    result.push({ path, color: colors[i % colors.length] })
    cumAngle += angle
  }
  return result
})

interface LabelItem {
  line: { x1: number; y1: number; x2: number; y2: number }
  text: { x: number; y: number; anchor: string; name: string; value: string }
}

const labelItems = computed(() => {
  const result: LabelItem[] = []
  let cumAngle = -Math.PI / 2

  for (let i = 0; i < props.data.length; i++) {
    const item = props.data[i]
    const fraction = total.value > 0 ? item.value / total.value : 0
    if (fraction === 0) {
      cumAngle += 0
      continue
    }
    const angle = fraction * 2 * Math.PI

    // For single item (full circle), point label upward
    const midAngle = total.value === item.value
      ? -Math.PI / 2
      : cumAngle + angle / 2

    const x1 = cx + r * Math.cos(midAngle)
    const y1 = cy + r * Math.sin(midAngle)
    const x2 = cx + labelR * Math.cos(midAngle)
    const y2 = cy + labelR * Math.sin(midAngle)

    const isRight = Math.cos(midAngle) >= 0

    result.push({
      line: { x1, y1, x2, y2 },
      text: {
        x: x2 + (isRight ? 8 : -8),
        y: y2,
        anchor: isRight ? 'start' : 'end',
        name: item.name,
        value: formatVal(item.value)
      }
    })

    cumAngle += angle
  }
  return result
})

function formatVal(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  return h > 0 ? `${h}h${m}m` : `${m}m`
}
</script>

<style scoped>
.pie-chart {
  display: flex;
  justify-content: center;
}
svg { display: block; }
.pie-slice {
  transition: opacity 0.2s;
  cursor: pointer;
}
.pie-slice:hover { opacity: 0.8; }
.center-total {
  font-size: 18px;
  font-weight: 500;
  fill: #fff;
  font-family: var(--font-mono);
}
.center-unit {
  font-size: 10px;
  fill: rgba(255,255,255,0.4);
}
</style>
