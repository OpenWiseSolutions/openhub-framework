import React, { PropTypes } from 'react'
// todo links import { IndexLink, Link } from 'react-router'

const Navbar = ({ toggleSidebar }) => (
  <div className='core-navbar'>
    <div onClick={toggleSidebar} className='left-controls'>
      logo
    </div>
    <div className='right-controls'>
      <a href='#'>link</a>
      <a href='#'>link</a>
      <a href='#'>link</a>
    </div>
  </div>
)

Navbar.propTypes = {
  toggleSidebar: PropTypes.func.isRequired
}

export default Navbar
