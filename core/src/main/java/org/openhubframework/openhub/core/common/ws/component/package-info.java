/**
 * Standard Camel Spring WS component returns raw exception when invalid XML request occurs.
 * We need to have possibility to add own error code constant and returns fault response.
 * <p/>
 * Example:
 * <pre>
     &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        &lt;SOAP-ENV:Header/>
        &lt;SOAP-ENV:Body>
           &lt;SOAP-ENV:Fault>
              &lt;faultcode>SOAP-ENV:Server&lt;/faultcode>
              &lt;faultstring xml:lang="en">E111: the request message is not valid XML (InvalidXmlException: 
 Could not parse XML; nested exception is org.xml.sax.SAXParseException: The element type "sub:ICCID" must be 
 terminated by the matching end-tag "&lt;/sub:ICCID>".)&lt;/faultstring>
           &lt;/SOAP-ENV:Fault>
        &lt;/SOAP-ENV:Body>
     &lt;/SOAP-ENV:Envelope>     
 * </pre>
 */
package org.openhubframework.openhub.core.common.ws.component;