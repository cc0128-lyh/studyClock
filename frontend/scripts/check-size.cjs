const fs = require('fs')
const path = require('path')

function getDirSize(dir) {
  let total = 0
  try {
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
      const full = path.join(dir, entry.name)
      if (entry.isDirectory()) total += getDirSize(full)
      else total += fs.statSync(full).size
    }
  } catch (e) {}
  return total
}

const jreSize = getDirSize('D:/work/StudyClock/frontend/jre')
const jarSize = fs.statSync('D:/work/StudyClock/backend/study-clock-bootstrap/target/study-clock-bootstrap-1.0.0.jar').size

console.log('JRE size:', (jreSize / 1024 / 1024).toFixed(1), 'MB')
console.log('JAR size:', (jarSize / 1024 / 1024).toFixed(1), 'MB')
console.log('Total:', ((jreSize + jarSize) / 1024 / 1024).toFixed(1), 'MB')
