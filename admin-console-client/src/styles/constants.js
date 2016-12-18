import { devColor } from './colors'

// constants
export const gap = '10px'
export const smallGap = '5px'
export const itemSize = 40
export const defaultTransitionTime = '0.3s'
export const dev = { backgroundColor: devColor, outline: `1px solid ${devColor}` }

// mixins
export const transition = (prop = 'all', time = defaultTransitionTime) => (
  {
    transitionProperty: prop,
    transitionDuration: time
  }
)
