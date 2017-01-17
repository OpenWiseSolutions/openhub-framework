const { describe, it } = global
import React from 'react'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { spy } from 'sinon'
import UserButton from '../../src/common/components/UserButton/UserButton'
import Item from '../../src/common/components/Item/Item'
import Avatar from '../../src/common/components/Avatar/Avatar'
import ArrowDown from 'react-icons/lib/md/keyboard-arrow-down'
import Radium from 'radium'

Radium.TestMode.enable()

const getWrapper = (props, context) => {
  return shallow(
    <UserButton{...props} />, { context })
}

describe('UserButton Component', () => {
  const toggle = spy()

  const wrapper = getWrapper(
    {
      toggle,
      name: 'Test Name',
      expanded: true,
      avatar: <Avatar />,
      links: [
        <Item label='Random Label' />,
        <Item label='Random Label' />,
        <Item label='Random Label' />
      ]
    }
  )

  it('should render', () => {
    expect(wrapper).to.have.descendants(Avatar)
    expect(wrapper.find('span')).to.have.text('Test Name')
    expect(wrapper).to.have.descendants(ArrowDown)
  })

  it('should trigger toggle', () => {
    expect(toggle).to.have.property('callCount', 0)
    wrapper.simulate('click')
    expect(toggle).to.have.property('callCount', 1)
  })

  it('should be expanded', () => {
    expect(wrapper.find('.menu')).to.have.length(1)
  })

  it('should render links', () => {
    expect(wrapper.find('.menu')).to.have.exactly(3).descendants(Item)
  })

  it('should not have any links and arrow', () => {
    const wrp = getWrapper(
      {
        toggle,
        expanded: true,
        name: 'Test Name'
      }
    )
    expect(wrp).to.not.have.descendants(ArrowDown)
    expect(wrp).to.not.have.descendants('.menu')
  })
})
