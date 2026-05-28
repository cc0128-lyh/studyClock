const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  launchApp: (appPath) => ipcRenderer.invoke('launch-app', appPath),
  launchUrl: (url) => ipcRenderer.invoke('launch-url', url),
  toggleFullscreen: () => ipcRenderer.invoke('toggle-fullscreen'),
  quitApp: () => ipcRenderer.invoke('quit-app')
});
