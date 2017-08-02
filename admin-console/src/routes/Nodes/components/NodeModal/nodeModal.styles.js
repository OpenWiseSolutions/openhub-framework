import { gap } from '../../../../styles/constants'

export default {
  main: {
    overlay: {
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(255, 255, 255, 0.75)',
      zIndex: 1
    },
    content: {
      position: 'absolute',
      top: '50%',
      left: '20%',
      right: '20%',
      bottom: 'auto',
      border: '1px solid #ccc',
      transform: 'translateY(-50%)',
      background: '#fff',
      overflow: 'auto',
      WebkitOverflowScrolling: 'touch',
      borderRadius: 0,
      outline: 'none',
      padding: '20px'
    }
  },
  content:{
    position: 'relative',
    top: 0,
    left: 0,
    marginTop: 30
  },
  row: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingLeft: 100,
    paddingRight: 100,
    marginTop: gap
  },
  label: {
    flex: 0.5,
    fontWeight: 600
  },
  field: {
    flex: 1
  },
  controls: {
    height: '40px',
    paddingTop: gap,
    paddingBottom: gap,
    justifyContent: 'flex-end',
    cancel: {
      float: 'right',
      paddingRight: gap
    },
    submit: {
      float: 'right',
      marginRight: gap
    }
  },
  state: {
    textAlign: 'right'
  },
  radio:{
    float: 'right'
  }
}
