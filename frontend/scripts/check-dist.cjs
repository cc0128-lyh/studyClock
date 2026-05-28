const fs = require('fs')
const path = require('path')

const distDir = 'D:/work/StudyClock/frontend/dist-electron'

if (!fs.existsSync(distDir)) {
  console.log('dist-electron directory not found')
  process.exit(0)
}

function list(dir, indent) {
  const items = []
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name)
    const stat = fs.statSync(full)
    const sizeMB = entry.isDirectory() ? 0 : (stat.size / 1024 / 1024).toFixed(1)
    items.push(indent + entry.name + (entry.isDirectory() ? '/' : '') + (entry.isFile() ? ' ' + sizeMB + 'MB' : ''))
    if (entry.isDirectory() && indent.length < 4) {
      items.push(...list(full, indent + '  '))
    }
  }
  return items
}

const result = list(distDir, '')
result.forEach(line => console.log(line))
