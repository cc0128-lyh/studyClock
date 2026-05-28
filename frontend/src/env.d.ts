/// <reference types="vite/client" />

interface ElectronAPI {
  launchApp: (appPath: string) => Promise<boolean>
  launchUrl: (url: string) => Promise<boolean>
  toggleFullscreen: () => Promise<boolean>
  quitApp: () => Promise<boolean>
}

interface Window {
  electronAPI?: ElectronAPI
}
