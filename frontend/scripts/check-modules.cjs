const { execSync } = require('child_process')
const fs = require('fs')
const path = require('path')

const javaHome = process.env.JAVA_HOME || 'D:\\code\\java17'
const backendJar = 'D:\\work\\StudyClock\\backend\\study-clock-bootstrap\\target\\study-clock-bootstrap-1.0.0.jar'

console.log('Analyzing module dependencies...')
try {
  const result = execSync(
    `"${javaHome}\\bin\\jdeps" --multi-release 17 --ignore-missing-deps --print-module-deps "${backendJar}"`,
    { encoding: 'utf8', timeout: 60000 }
  )
  console.log('Required modules:', result.trim())
} catch (e) {
  console.log('jdeps failed:', e.stderr?.toString() || e.message)
  // Try without multi-release
  try {
    const result = execSync(
      `"${javaHome}\\bin\\jdeps" --ignore-missing-deps --print-module-deps "${backendJar}"`,
      { encoding: 'utf8', timeout: 60000 }
    )
    console.log('Required modules:', result.trim())
  } catch (e2) {
    console.log('jdeps also failed:', e2.stderr?.toString() || e2.message)
  }
}

// Also check jlink
console.log('\nChecking jlink...')
const jlinkPath = path.join(javaHome, 'bin', 'jlink.exe')
console.log('jlink exists:', fs.existsSync(jlinkPath))
if (fs.existsSync(jlinkPath)) {
  const outDir = 'D:\\work\\StudyClock\\frontend\\jre'
  try {
    const result = execSync(
      `"${jlinkPath}" --add-modules java.base,java.sql,java.desktop,java.management,java.naming,java.logging,java.instrument,java.xml,jdk.unsupported,jdk.management,java.scripting,jdk.management.agent --output "${outDir}" --strip-debug --compress=2 --no-header-files --no-man-pages 2>&1`,
      { encoding: 'utf8', timeout: 120000 }
    )
    console.log('jlink succeeded:', result)
    // Check size
    function getDirSize(dir) {
      let total = 0
      try {
        for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
          const full = path.join(dir, entry.name)
          if (entry.isDirectory()) total += getDirSize(full)
          else total += fs.statSync(full).size
        }
      } catch {}
      return total
    }
    const size = getDirSize(outDir)
    console.log('JRE size:', (size / 1024 / 1024).toFixed(1), 'MB')
  } catch (e) {
    console.log('jlink failed:', e.stdout?.toString() || e.stderr?.toString() || e.message)
  }
}
