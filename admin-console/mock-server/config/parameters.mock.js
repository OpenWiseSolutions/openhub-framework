module.exports = [
  {
    request: {
      method: 'GET',
      path: '/api/config-params'
    },
    response: {
      statusCode: 200,
      body: JSON.stringify({
        "data": [{
          "id": "ohf.alerts.repeatTimeSec",
          "code": "ohf.alerts.repeatTimeSec",
          "categoryCode": "core.alerts",
          "currentValue": "1",
          "defaultValue": "-1",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": ""
        }, {
          "id": "ohf.asynch.concurrentConsumers",
          "code": "ohf.asynch.concurrentConsumers",
          "categoryCode": "core.async",
          "currentValue": "4",
          "defaultValue": "5",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": ""
        }, {
          "id": "ohf.asynch.confirmation.failedLimit",
          "code": "ohf.asynch.confirmation.failedLimit",
          "categoryCode": "core.async",
          "currentValue": "3",
          "defaultValue": "3",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.confirmation.intervalSec",
          "code": "ohf.asynch.confirmation.intervalSec",
          "categoryCode": "core.async",
          "currentValue": "60",
          "defaultValue": "60",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.confirmation.repeatTimeSec",
          "code": "ohf.asynch.confirmation.repeatTimeSec",
          "categoryCode": "core.async",
          "currentValue": "60",
          "defaultValue": "60",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.countPartlyFailsBeforeFailed",
          "code": "ohf.asynch.countPartlyFailsBeforeFailed",
          "categoryCode": "core.async",
          "currentValue": "3",
          "defaultValue": "3",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.externalCall.skipUriPattern",
          "code": "ohf.asynch.externalCall.skipUriPattern",
          "categoryCode": "core.async",
          "currentValue": null,
          "defaultValue": null,
          "dataType": "STRING",
          "mandatory": false,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.partlyFailedIntervalSec",
          "code": "ohf.asynch.partlyFailedIntervalSec",
          "categoryCode": "core.async",
          "currentValue": "60",
          "defaultValue": "60",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.partlyFailedRepeatTimeSec",
          "code": "ohf.asynch.partlyFailedRepeatTimeSec",
          "categoryCode": "core.async",
          "currentValue": "60",
          "defaultValue": "60",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.postponedIntervalSec",
          "code": "ohf.asynch.postponedIntervalSec",
          "categoryCode": "core.async",
          "currentValue": "6",
          "defaultValue": "5",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": ""
        }, {
          "id": "ohf.asynch.postponedIntervalWhenFailedSec",
          "code": "ohf.asynch.postponedIntervalWhenFailedSec",
          "categoryCode": "core.async",
          "currentValue": "300",
          "defaultValue": "300",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.asynch.repairRepeatTimeSec",
          "code": "ohf.asynch.repairRepeatTimeSec",
          "categoryCode": "core.async",
          "currentValue": "123132",
          "defaultValue": "300",
          "dataType": "INT",
          "mandatory": true,
          "description": null,
          "validationRegEx": ""
        }, {
          "id": "ohf.dir.fileRepository",
          "code": "ohf.dir.fileRepository",
          "categoryCode": "core.dir",
          "currentValue": null,
          "defaultValue": null,
          "dataType": "FILE",
          "mandatory": false,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.dir.temp",
          "code": "ohf.dir.temp",
          "categoryCode": "core.dir",
          "currentValue": null,
          "defaultValue": null,
          "dataType": "FILE",
          "mandatory": false,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.endpoints.includePattern",
          "code": "ohf.endpoints.includePattern",
          "categoryCode": "core.endpoints",
          "currentValue": "^(spring-ws|servlet).*$",
          "defaultValue": "^(spring-ws|servlet).*$",
          "dataType": "STRING",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.mail.admin",
          "code": "ohf.mail.admin",
          "categoryCode": "core.mail",
          "currentValue": null,
          "defaultValue": null,
          "dataType": "STRING",
          "mandatory": false,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.mail.from",
          "code": "ohf.mail.from",
          "categoryCode": "core.mail",
          "currentValue": "OpenHub integration platform <openhub@openwise.cz>",
          "defaultValue": null,
          "dataType": "STRING",
          "mandatory": false,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.mail.smtp.server",
          "code": "ohf.mail.smtp.server",
          "categoryCode": "core.mail",
          "currentValue": "localhost",
          "defaultValue": null,
          "dataType": "STRING",
          "mandatory": false,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.requestSaving.enable",
          "code": "ohf.requestSaving.enable",
          "categoryCode": "core.requestSaving",
          "currentValue": "false",
          "defaultValue": "false",
          "dataType": "BOOLEAN",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.requestSaving.endpointFilter",
          "code": "ohf.requestSaving.endpointFilter",
          "categoryCode": "core.requestSaving",
          "currentValue": "^(spring-ws|servlet).*$",
          "defaultValue": "^(spring-ws|servlet).*$",
          "dataType": "STRING",
          "mandatory": true,
          "description": null,
          "validationRegEx": null
        }, {
          "id": "ohf.server.localhostUri",
          "code": "ohf.server.localhostUri",
          "categoryCode": "core.server",
          "currentValue": "http://localhost:8080",
          "defaultValue": "http://localhost:8080",
          "dataType": "STRING",
          "mandatory": false,
          "description": null,
          "validationRegEx": ""
        }, {
          "id": "ohf.server.localhostUri.check",
          "code": "ohf.server.localhostUri.check",
          "categoryCode": "core.server",
          "currentValue": "false",
          "defaultValue": "false",
          "dataType": "BOOLEAN",
          "mandatory": true,
          "description": null,
          "validationRegEx": ""
        }, {
          "id": "ohf.uri.inputPattern",
          "code": "ohf.uri.inputPattern",
          "categoryCode": "core.server",
          "currentValue": "^(spring-ws).*$",
          "defaultValue": "^(spring-ws).*$",
          "dataType": "STRING",
          "mandatory": true,
          "description": "Pattern for all input URIs into ESB",
          "validationRegEx": null
        }, {
          "id": "ohf.throttling.disable",
          "code": "ohf.throttling.disable",
          "categoryCode": "core.throttling",
          "currentValue": "false",
          "defaultValue": "false",
          "dataType": "BOOLEAN",
          "mandatory": true,
          "description": null,
          "validationRegEx": ""
        }],
        "paging": {
          "last": true,
          "sort": null,
          "totalElements": 24,
          "first": true,
          "numberOfElements": 24,
          "totalPages": 1,
          "size": 100,
          "number": 1
        }
      })
    }
  }
]
