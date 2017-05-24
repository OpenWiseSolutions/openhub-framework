import { secondaryColor } from '../../../../styles/colors'
import { gap } from '../../../../styles/constants'

export default {
  main: {
    paddingTop: gap,
    paddingLeft: gap,
    paddingRight: gap,
    paddingBottom: gap
  },
  searchBox: {
    width: 300,
    display: 'flex',
    alignItems: 'center'
  },
  counts: {
    marginLeft: gap
  },
  loggers: {
    marginTop: gap
  },
  listControl: {
    textAlign: 'center',
    paddingTop: gap,
    paddingBottom: gap,
    color: secondaryColor,
    cursor: 'pointer',
    ':hover': {
      textDecoration: 'underline'
    }
  }
}
