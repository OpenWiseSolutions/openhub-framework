import React from 'react'
import classes from '../../utils/classes'
// todo import { IndexLink, Link } from 'react-router'

const Sidebar = ({ extended }) => {
  const sidebarClasses = classes(
    'core-sidebar',
    extended && 'extended'
  )

  return (
    <div className={sidebarClasses} />
  )
}

Sidebar.propTypes = {
  extended: React.PropTypes.bool
}

export default Sidebar
