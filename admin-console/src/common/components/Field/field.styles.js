import { componentsColor, negativeColor, lightColor } from '../../../styles/colors'
import { gap } from '../../../styles/constants'

export default {
  base: {
    position: 'relative',
    boxSizing: 'border-box',
    marginTop: 0,
    marginBottom: 0,
    marginLeft: 0,
    marginRight: 0,
    paddingTop: 0,
    paddingBottom: 0,
    paddingLeft: 0,
    paddingRight: 0
  },
  field: {
    position: 'relative',
    height: `30px`,
    width: '100%',
    outline: 'none',
    paddingLeft: gap,
    paddingRight: gap,
    backgroundColor: 'white',
    borderTopWidth: 0,
    borderLeftWidth: 0,
    borderRightWidth: 0,
    borderBottomWidth: 1,
    borderBottomColor: componentsColor,
    ':focus': {
      borderTopWidth: 0,
      borderLeftWidth: 0,
      borderRightWidth: 0,
      borderBottomWidth: 1,
      borderBottomColor: componentsColor
    },
    error: {
      borderTopWidth: 0,
      borderLeftWidth: 0,
      borderRightWidth: 0,
      borderBottom: `1px solid ${negativeColor}`,
      color: negativeColor
    },
    disabled: {
      borderTopWidth: 0,
      borderLeftWidth: 0,
      borderRightWidth: 0,
      borderBottomWidth: 0,
      backgroundColor: lightColor
    },
    readOnly: {
      borderTopWidth: 0,
      borderLeftWidth: 0,
      borderRightWidth: 0,
      borderBottomWidth: 0,
      backgroundColor: 'transparent'
    }
  }
}
