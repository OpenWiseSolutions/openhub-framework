import React from 'react'
import { hashHistory } from 'react-router'
import { path } from 'ramda'
import ListItem from 'react-md/lib/Lists/ListItem'
import FontIcon from 'react-md/lib/FontIcons'

const isActive = (url) => url === window.location.hash.substr(1)

export default (config) => {
  if (!config) return null
  return [
    <ListItem
      key='Dashboard'
      active={isActive('/')}
      onClick={() => hashHistory.push('/')}
      leftIcon={<FontIcon >home</FontIcon >}
      primaryText={'Dashboard'}
    />,

    // Analytics
    <div key='Analytics' >
      {config.analytics &&
      <ListItem
        primaryText={'Analytics'}
        defaultOpen
        leftIcon={<FontIcon >blur_linear</FontIcon >}
        nestedItems={[
          path(['analytics', 'messages', 'enabled'], config) && <ListItem
            key='Messages'
            active={isActive('/messages')}
            onClick={() => hashHistory.push('/messages')}
            leftIcon={<FontIcon >drafts</FontIcon >}
            primaryText={'Messages'}
          />
        ]}
      />}
    </div >,

    // Infrastructure
    <div key='Infrastructure'>
      {config.cluster &&
      <ListItem
        primaryText={'Infrastructure'}
        leftIcon={<FontIcon >developer_board</FontIcon >}
        defaultOpen
        nestedItems={[
          path(['cluster', 'nodes', 'enabled'], config) && <ListItem
            key='Nodes'
            active={isActive('/nodes')}
            onClick={() => hashHistory.push('/nodes')}
            leftIcon={<FontIcon >device_hub</FontIcon >}
            primaryText={'Nodes'}
          />,
          path(['infrastructure', 'services', 'wsdl', 'enabled'], config) && <ListItem
            key='WSDL'
            active={isActive('/wsdl')}
            onClick={() => hashHistory.push('/wsdl')}
            leftIcon={<FontIcon >device_hub</FontIcon >}
            primaryText={'WSDL'}
          />
        ]}
      />}
    </div >,

    // Configuration
    <ListItem
      key='Configuration'
      primaryText={'Configuration'}
      leftIcon={<FontIcon >build</FontIcon >}
      defaultOpen
      nestedItems={[
        path(['configuration', 'systemParams', 'enabled'], config) && <ListItem
          key='params'
          active={isActive('/config-params')}
          onClick={() => hashHistory.push('/config-params')}
          leftIcon={<FontIcon >extension</FontIcon >}
          primaryText={'Config Params'}
        />,
        path(['configuration', 'logging', 'enabled'], config) && <ListItem
          key='logging'
          onClick={() => hashHistory.push('/config-logging')}
          active={isActive('/config-logging')}
          leftIcon={<FontIcon >info</FontIcon >}
          primaryText={'Config Logging'}
        />,
        path(['configuration', 'environment', 'enabled'], config) && <ListItem
          key='properties'
          onClick={() => hashHistory.push('/environment-properties')}
          active={isActive('/environment-properties')}
          leftIcon={<FontIcon >filter_hdr</FontIcon >}
          primaryText={'Environment Properties'}
        />,
        path(['configuration', 'alerts', 'enabled'], config) && <ListItem
          key='alerts'
          onClick={() => hashHistory.push('/alerts')}
          active={isActive('/alerts')}
          leftIcon={<FontIcon >notifications_active</FontIcon >}
          primaryText={'Alerts'}
        />,
        path(['configuration', 'errorCodeCatalog', 'enabled'], config) && <ListItem
          key='errors'
          onClick={() => hashHistory.push('/errors-overview')}
          active={isActive('/errors-overview')}
          leftIcon={<FontIcon >bug_report</FontIcon >}
          primaryText={'Errors Overview'}
        />
      ]}
    />,

    { divider: true },

    // External
    <div key='External' >
      {path(['externalLinks', 'enabled'], config) &&
      <ListItem
        primaryText={'External'}
        leftIcon={<FontIcon >link</FontIcon >}
        nestedItems={
          config.externalLinks.items.map(({ title, link, enabled }) => {
            if (!enabled) return null
            return (
              <ListItem
                key={title}
                onClick={() => window.open(link, '_blank')}
                leftIcon={<FontIcon >keyboard_arrow_right</FontIcon >}
                primaryText={title}
              />)
          })
        }
      />}
    </div >,

    // Changes
    <div key='Changes' >
      {path(['changes', 'enabled'], config) && <ListItem
        active={isActive('/changes')}
        onClick={() => hashHistory.push('/changes')}
        leftIcon={<FontIcon >merge_type</FontIcon >}
        primaryText={'Changes'}
      />}
    </div >

  ]
}
