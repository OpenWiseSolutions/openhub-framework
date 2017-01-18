# OpenHub integration framework - example module

Module that represents sample [route definition] for processing synchronous and asynchronous SOAP requests with OpenHub.

URI: `http://localhost:8080/ws/hello/v1` (if OpenHub runs under root context and `8080` port)

Synchronous request:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:hel="http://openhubframework.org/ws/HelloService-v1">
   <soapenv:Header/>
   <soapenv:Body>
      <hel:syncHelloRequest>
         <hel:name>Hanus</hel:name>
      </hel:syncHelloRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

Asynchronous request:

```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:com="http://openhubframework.org/ws/Common-v1" xmlns:hel="http://openhubframework.org/ws/HelloService-v1">
   <soapenv:Header>
      <com:traceHeader>
         <com:traceIdentifier>
            <com:applicationID>CRM</com:applicationID>
            <com:timestamp>2016-03-12T11:03:05</com:timestamp>
            <com:correlationID>515112a9-23ed-4b3f-83d7-125a58ec4753</com:correlationID>
         </com:traceIdentifier>
      </com:traceHeader>
   </soapenv:Header>
   <soapenv:Body>
      <hel:asyncHelloRequest>
         <hel:name>Hanus</hel:name>
      </hel:asyncHelloRequest>
   </soapenv:Body>
</soapenv:Envelope>
```



[route definition]: https://openhubframework.atlassian.net/wiki/pages/viewpage.action?pageId=33660