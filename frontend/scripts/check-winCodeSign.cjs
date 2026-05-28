const fs = require('fs')
const path = require('path')
const os = require('os')

const d = path.join(os.homedir(), 'AppData/Local/electron-builder/Cache/winCodeSign')
if (!fs.existsSync(d)) {
  console.log('no winCodeSign cache')
  process.exit(0)
}
fs.readdirSync(d).forEach(f => {
  const fp = path.join(d, f)
  const stat = fs.statSync(fp)
  console.log(f, stat.isDirectory() ? '(dir)' : (stat.size / 1024 / 1024).toFixed(1) + 'MB')
})
