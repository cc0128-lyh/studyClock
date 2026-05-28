const { app, BrowserWindow, ipcMain, shell } = require('electron');
const path = require('path');
const { spawn } = require('child_process');

let mainWindow;
let backendProcess;

function startBackend() {
  const jarPath = app.isPackaged
    ? path.join(process.resourcesPath, 'backend', 'study-clock.jar')
    : path.join(__dirname, '..', '..', 'backend', 'study-clock-bootstrap', 'target', 'study-clock-bootstrap-1.0.0.jar');

  try {
    backendProcess = spawn('java', ['-jar', jarPath], {
      stdio: 'pipe',
      env: { ...process.env, 'server.port': '18080' }
    });
    backendProcess.stdout.on('data', (data) => console.log(`[Backend] ${data}`));
    backendProcess.stderr.on('data', (data) => console.error(`[Backend] ${data}`));
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
