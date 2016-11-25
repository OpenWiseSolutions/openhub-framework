/**
 * Standard Camel Spring WS component returns raw exception when invalid XML request occurs.
 * We need to have possibility to add own error code constant and returns fault response.
 * <p>
 * Example:
 * <pre>
     &lt;SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"&gt;
        &lt;SOAP-ENV:Header/&gt;
        &lt;SOAP-ENV:Body&gt;
           &lt;SOAP-ENV:Fault&gt;
              &lt;faultcode&gt;SOAP-ENV:Server&lt;/faultcode&gt;
              &lt;faultstring xml:lang="en"&gt;E111: the request message is not valid XML (InvalidXmlException: 
 Could not parse XML; nested exception is org.xml.sax.SAXParseException: The element type "sub:ICCID" must be 
 terminated by the matching end-tag "&lt;/sub:ICCID&gt;".)&lt;/faultstring&gt;
           &lt;/SOAP-ENV:Fault&gt;
        &lt;/SOAP-ENV:Body&gt;
     &lt;/SOAP-ENV:Envelope&gt;     
 * </pre>
 */
package org.openhubframework.openhub.core.common.ws.component;