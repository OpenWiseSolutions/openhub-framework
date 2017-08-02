import { gap, itemSize } from '../../../styles/constants'
import { primaryColor, secondaryColor } from '../../../styles/colors'

export default {
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(255, 255, 255, 0.75)'
  },
  content: {
    position: 'absolute',
    top: '50%',
    left: '30%',
    right: '30%',
    bottom: 'auto',
    border: '1px solid #ccc',
    transform: 'translateY(-50%)',
    background: '#fff',
    overflow: 'auto',
    WebkitOverflowScrolling: 'touch',
    borderRadius: 0,
    outline: 'none',
    padding: '20px'
  },
  header:{
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    paddingLeft: gap,
    paddingRight: gap,
    lineHeight: `${itemSize}px`,
    backgroundColor: secondaryColor,
    boxSizing: 'border-box',
    title: {
      color: primaryColor,
      fontWeight: 400
    },
    close: {
      position: 'absolute',
      right: 0,
      top: 0,
      color: primaryColor,
      paddingRight: gap,
      cursor: 'pointer'
    }
  },
  form: {
    paddingTop: `${itemSize}px`
  },
  controls: {
    position: 'relative',
    width: '100%',
    height: '40px',
    paddingTop: gap,
    paddingBottom: gap,
    cancel: {
      float: 'right'
    },
    submit: {
      float: 'right',
      marginRight: gap
    }
  }
}
