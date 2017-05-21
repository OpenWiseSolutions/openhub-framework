const { describe, it } = global
import React from 'react'
import { expect } from 'chai'
import { shallow } from 'enzyme'
import { spy } from 'sinon'
import ArrowDown from 'react-icons/lib/md/keyboard-arrow-down'
import MenuIcon from 'react-icons/lib/md/menu'
import Item from '../../src/common/components/Item/Item'

const getWrapper = (props) => {
  return shallow(
    <Item {...props} />)
}

describe('Item Component', () => {
  const onClickSpy = spy()

  const wrapper = getWrapper(
    {
      label: 'Some Label',
      size: 50,
      onClick: onClickSpy
    }
  )

  it('should render', () => {
    expect(wrapper.find('.item')).to.have.length(1)
    expect(wrapper.find('.item')).to.have.text('Some Label')
    expect(wrapper).to.not.have.descendants(Item)
    expect(wrapper).to.not.have.descendants(ArrowDown)
  })

  it('should invoke callback', () => {
    expect(onClickSpy).to.have.property('callCount', 0)
    wrapper.find('.item').simulate('click')
    expect(onClickSpy).to.have.property('callCount', 1)
  })

  it('should render with icon', () => {
    const wrp = getWrapper(
      {
        label: 'Some Label',
        size: 50,
        icon: <MenuIcon />
      }
    )
    expect(wrp).to.have.descendants(MenuIcon)
  })

  it('should render with custom size', () => {
    const wrp = getWrapper(
      {
        label: 'Some Label',
        size: 90
      }
    )
    expect(wrp).to.have.style('min-height', '90px')
    expect(wrp).to.have.style('line-height', '90px')
  })

  it('should have children and arrow', () => {
    const wrapperSpy = spy()
    const nestedSpy = spy()
    const wrp = getWrapper(
      {
        label: 'Some Label',
        onClick: wrapperSpy,
        children: [
          <Item key={1} label='Uno' />,
          <Item key={2} label='Duo' onClick={nestedSpy} />
        ]
      }
    )
    expect(wrp).to.have.descendants(ArrowDown)
    expect(wrp).to.have.exactly(2).descendants(Item)
    expect(nestedSpy).to.have.property('callCount', 0)
    wrp.find({ label: 'Duo' }).simulate('click')
    expect(nestedSpy).to.have.property('callCount', 1)
    expect(wrapperSpy).to.have.property('callCount', 0)
    wrp.simulate('click')
    expect(wrp).to.not.have.descendants(Item)
  })
})
