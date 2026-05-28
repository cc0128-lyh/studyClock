/// <reference types="vite/client" />

interface ShortcutPickResult {
  name: string
  icon: string | null
  appPath: string | null
  url: string | null
  launchType: 'APP' | 'URL'
}

interface ElectronAPI {
  launchApp: (appPath: string) => Promise<boolean>
  launchUrl: (url: string) => Promise<boolean>
  pickShortcut: () => Promise<ShortcutPickResult | null>
  toggleFullscreen: () => Promise<boolean>
  quitApp: () => Promise<boolean>
}

interface Window {
  electronAPI?: ElectronAPI
}
