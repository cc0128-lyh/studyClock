const { app, BrowserWindow, ipcMain, shell, dialog } = require('electron');
const path = require('path');
const fs = require('fs');
const { spawn } = require('child_process');

// 禁用自动播放策略，确保打包后通知音效正常播放
app.commandLine.appendSwitch('autoplay-policy', 'no-user-gesture-required');

let mainWindow;
let backendProcess;

function getJavaPath() {
  if (app.isPackaged) {
    const jreBin = path.join(process.resourcesPath, 'jre', 'bin');
    const javaExe = path.join(jreBin, 'java.exe');
    if (fs.existsSync(javaExe)) {
      return javaExe;
    }
  } else {
    const devJre = path.join(__dirname, '..', 'jre', 'bin', 'java.exe');
    if (fs.existsSync(devJre)) {
      return devJre;
    }
  }
  return 'java';
}

function startBackend() {
  const jarPath = app.isPackaged
    ? path.join(process.resourcesPath, 'backend', 'study-clock.jar')
    : path.join(__dirname, '..', '..', 'backend', 'study-clock-bootstrap', 'target', 'study-clock-bootstrap-1.0.0.jar');

  const javaExe = getJavaPath();

  // 使用 Electron 的用户数据目录（Windows: %APPDATA%/StudyClock）
  const userDataDir = app.getPath('userData');

  try {
    backendProcess = spawn(javaExe, ['-jar', jarPath, `--studyclock.home=${userDataDir}`], {
      stdio: 'pipe',
      env: { ...process.env, 'server.port': '18080' }
    });
    backendProcess.stdout.on('data', (data) => console.log(`[Backend] ${data}`));
    backendProcess.stderr.on('data', (data) => console.error(`[Backend] ${data}`));
    backendProcess.on('error', (err) => {
      console.warn('Failed to start backend:', err.message);
    });
  } catch (e) {
    console.warn('Backend JAR not found, assuming backend is already running:', e.message);
  }
}

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 800,
    fullscreen: true,
    frame: false,
    kiosk: false,
    webPreferences: {
      preload: path.join(__dirname, 'preload.cjs'),
      contextIsolation: true,
      nodeIntegration: false
    }
  });

  if (app.isPackaged) {
    mainWindow.loadFile(path.join(__dirname, '..', 'dist', 'index.html'));
  } else {
    mainWindow.loadURL('http://localhost:5173');
    mainWindow.webContents.openDevTools({ mode: 'detach' });
  }

  mainWindow.on('closed', () => { mainWindow = null; });
}

ipcMain.handle('launch-app', (_, appPath) => {
  shell.openPath(appPath);
  return true;
});

ipcMain.handle('launch-url', (_, url) => {
  shell.openExternal(url);
  return true;
});

ipcMain.handle('pick-shortcut', async () => {
  const result = await dialog.showOpenDialog({
    properties: ['openFile'],
    filters: [{ name: '应用快捷方式', extensions: ['lnk', 'exe'] }]
  });
  if (result.canceled || result.filePaths.length === 0) return null;

  const filePath = result.filePaths[0];
  const ext = path.extname(filePath).toLowerCase();
  const fileName = path.basename(filePath, ext);

  if (ext === '.lnk') {
    try {
      const link = await shell.readShortcutLink(filePath);
      return {
        name: link.description || fileName,
        icon: link.icon || null,
        appPath: link.target || null,
        url: null,
        launchType: 'APP'
      };
    } catch (e) {
      return null;
    }
  } else if (ext === '.exe') {
    return {
      name: fileName,
      icon: filePath,
      appPath: filePath,
      url: null,
      launchType: 'APP'
    };
  }
  return null;
});

ipcMain.handle('toggle-fullscreen', () => {
  if (mainWindow) {
    mainWindow.setFullScreen(!mainWindow.isFullScreen());
  }
  return true;
});

ipcMain.handle('quit-app', () => {
  app.quit();
  return true;
});

app.whenReady().then(() => {
  startBackend();
  setTimeout(createWindow, 2000);
});

app.on('window-all-closed', () => {
  if (backendProcess) {
    backendProcess.kill();
  }
  app.quit();
});

app.on('before-quit', () => {
  if (backendProcess) {
    backendProcess.kill();
  }
});
