import { devColor } from './colors'

// constants
export const gap = '10px'
export const bigGap = '20px'
export const smallGap = '5px'
export const itemSize = 40
export const defaultTransitionTime = '0.3s'
export const userMenuWidth = 300
export const dev = { backgroundColor: devColor, outline: `1px solid ${devColor}` }
export const logo = 'logo.png'

// mixins
export const transition = (prop = 'all', time = defaultTransitionTime) => (
  {
    transitionProperty: prop,
    transitionDuration: time
  }
)
