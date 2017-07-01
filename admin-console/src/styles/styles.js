import { lightColor } from './colors'

export default {
  panel: {
    width: '98%'
  },
  table: {
    position: 'relative',
    width: '100%',
    boxShadow: '0 2px 10px -2px silver',
    borderSpacing: 0
  },
  header: {
    textAlign: 'left',
    paddingLeft: 10,
    paddingTop: 10,
    paddingBottom: 10,
    borderBottom: `1px solid #73C2FF`
  },
  even: {
    backgroundColor: '#fff'
  },
  odd: {
    backgroundColor: '#fff'
  },
  cell: {
    margin: 0,
    paddingTop: 10,
    paddingBottom: 10,
    paddingLeft: 10,
    paddingRight: 10,
    borderBottom: `1px solid ${lightColor}`
  }
}
