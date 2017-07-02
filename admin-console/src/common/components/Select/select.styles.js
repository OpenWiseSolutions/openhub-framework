import { componentsColor, negativeColor, lightColor, darkColor } from '../../../styles/colors'
import tc from 'tinycolor2'
import { gap, depth1 } from '../../../styles/constants'

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
  label: {
    float: 'left',
    paddingLeft: gap,
    paddingTop: `${30 * 0.1}px`,
    lineHeight: `${30 * 0.9}px`
  },
  icons: {

  },
  select: {
    position: 'relative',
    height: '30px',
    width: '100%',
    outline: 'none',
    paddingLeft: gap,
    paddingRight: gap,
    lineHeight: '30px',
    backgroundColor: 'white',
    borderTopWidth: 0,
    borderLeftWidth: 0,
    borderRightWidth: 0,
    borderBottomWidth: 1,
    borderBottomColor: componentsColor,
    borderBottomStyle: 'solid',
    expanded: {
      ':focus': {
        borderTopWidth: 0,
        borderLeftWidth: 0,
        borderRightWidth: 0,
        borderBottomWidth: 0
      }
    },
    placeholder: {
      color: tc(darkColor).lighten(20).toString()
    },
    ':focus': {
      borderTopWidth: 0,
      borderLeftWidth: 0,
      borderRightWidth: 0,
      borderBottomWidth: 1
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
      borderBottomWidth: 1,
      backgroundColor: lightColor
    },
    readOnly: {
      border: 'none',
      backgroundColor: 'transparent'
    },
    value: {},
    options: {
      position: 'absolute',
      top: 0,
      left: 0,
      height: 'auto',
      width: '100%',
      boxSizing: 'border-box',
      zIndex: depth1,
      boxShadow: `0 2px 10px -2px ${darkColor}`,
      borderTopWidth: 0,
      borderLeftWidth: 0,
      borderRightWidth: 0,
      borderBottomWidth: 1,
      scrollable: {
        height: `${5 * 30}px`,
        overflow: 'auto'
      }
    },
    option: {
      position: 'relative',
      width: '100%',
      backgroundColor: tc(lightColor).lighten(30).toString(),
      height: `30px`,
      lineHeight: `30px`,
      paddingLeft: gap,
      paddingRight: gap,
      boxSizing: 'border-box',
      borderBottom: '1px solid #dddddd',
      cursor: 'pointer',
      highlighted: {
        backgroundColor: tc(componentsColor).lighten(20).toString()
      }
    }
  }
}
