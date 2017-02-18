import { gap } from '../../../styles/constants'
import { positiveColor, secondaryColor } from '../../../styles/colors'

export default {
  main: {},
  widgets: {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    alignContent: 'flex-start'
  },
  memChart: {
    position: 'relative',
    paddingTop: gap,
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'flex-start',
    alignItems: 'flex-start',
    alignContent: 'flex-start'
  },
  info: {
    listStyle: 'none'
  },
  tag: {
    position: 'relative',
    width: gap,
    height: gap,
    borderRadius: '50%',
    display: 'inline-block',
    marginRight: gap,
    free: {
      backgroundColor: positiveColor
    },
    used: {
      backgroundColor: secondaryColor
    }
  }
}
