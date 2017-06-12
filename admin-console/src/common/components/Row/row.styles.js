import { smallGap } from '../../../styles/constants'

export default {
  row: {
    position: 'relative',
    width: '100%',
    marginTop: smallGap,
    display: 'flex',
    flexDirection: 'row',
    boxSizing: 'border-box',
    justifyContent: 'flex-start'
  },
  label: {
    width: '50%',
    fontWeight: 600,
    lineHeight: '30px'
  },
  children: {
    width: '50%'
  }
}
