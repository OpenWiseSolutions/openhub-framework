import { gap } from '../../../styles/constants'
import { positiveColor, secondaryColor } from '../../../styles/colors'

export default {
  container: {
    position: 'relative',
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    alignItems: 'center',
    height: '90vh'
  },
  widgets: {
    display: 'flex',
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-around',
    alignItems: 'inherit'
  },
  widget: {
    position: 'relative',
    width: '48%',
    marginTop: gap,
    marginBottom: gap
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
