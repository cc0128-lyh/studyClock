const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  launchApp: (appPath) => ipcRenderer.invoke('launch-app', appPath),
  launchUrl: (url) => ipcRenderer.invoke('launch-url', url),
  pickShortcut: () => ipcRenderer.invoke('pick-shortcut'),
  toggleFullscreen: () => ipcRenderer.invoke('toggle-fullscreen'),
  quitApp: () => ipcRenderer.invoke('quit-app')
});
