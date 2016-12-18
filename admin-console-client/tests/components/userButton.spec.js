const { describe, it } = global
import React from 'react'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { spy } from 'sinon'
import UserButton from '../../src/components/UserButton/UserButton'
import Avatar from '../../src/components/Avatar/Avatar'
import ArrowDown from 'react-icons/lib/md/keyboard-arrow-down'

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
      expanded: true
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
})
