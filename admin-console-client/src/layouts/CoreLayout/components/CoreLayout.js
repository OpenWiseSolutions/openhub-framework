import React from 'react'
import classes from '../../../utils/classes'
import Navbar from '../../../components/Navbar/Navbar'
import Sidebar from '../../../components/Sidebar/Sidebar'
import '../../../styles/core.scss'

export const CoreLayout = ({ children, sidebarExtended, actions }) => {
  const bodyClasses = classes(
    'core-layout-body',
    sidebarExtended && 'extended'
  )

  return (
    <div className='core-layout'>
      <Sidebar extended={sidebarExtended} />
      <div className={bodyClasses}>
        <Navbar toggleSidebar={actions.toggleSidebar} />
        <div className='core-layout-contents'>
          {children}
        </div>
      </div>
    </div>
  )
}

CoreLayout.propTypes = {
  children: React.PropTypes.element.isRequired,
  sidebarExtended: React.PropTypes.bool,
  actions: React.PropTypes.object
}

export default CoreLayout
