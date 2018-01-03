package org.wso2.custom.handler;

import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.apache.synapse.transport.passthru.util.RelayUtils;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class PayloadHandler extends AbstractHandler {
    private static final Log log = LogFactory.getLog(PayloadHandler.class);
    private static final int PAYLOAD_LIMIT_KB = 10;
    private static final String HTTP_POST = "POST";


    public boolean handleRequest(MessageContext messageContext) {

        //check if a post request
        if ((HTTP_POST).equals(messageContext.getProperty("api.ut.HTTP_METHOD"))) {
            //calculate payload size and validate
            long payloadSize = calculatePayloadSize(messageContext);
            if (payloadSize > PAYLOAD_LIMIT_KB) {
                log.error("Payload size is " + payloadSize + " kb. Hence message is dropped. Client will timeout.");
                return false;
            }
        }
        return true;
    }

    public boolean handleResponse(MessageContext messageContext) {
        return true;
    }


    /**
     * Calculate request message size. Reads value from Content-Length header if available else build message and
     * calculate size.
     *
     * @param messageContext MessageContext object
     * @return Payload size in kilo bytes
     */
    private long calculatePayloadSize(MessageContext messageContext) {
        long requestSize = 0;
        org.apache.axis2.context.MessageContext axis2MC = ((Axis2MessageContext) messageContext)
                .getAxis2MessageContext();
        Map headers = (Map) axis2MC.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        String contentLength = (String) headers.get(HttpHeaders.CONTENT_LENGTH);

        log.info("Request Content Length : " + contentLength + " bytes.");
        if (contentLength != null) {
            requestSize = Integer.parseInt(contentLength);
        } else {
            // When chunking is enabled
            try {
                RelayUtils.buildMessage(axis2MC);
            } catch (IOException ex) {
                // In case of an exception, it won't be propagated up,and set response size to 0
                log.error("Error occurred while building the message to" + " calculate the response body size", ex);
            } catch (XMLStreamException ex) {
                log.error("Error occurred while building the message to calculate the response" + " body size", ex);
            }

            SOAPEnvelope env = messageContext.getEnvelope();
            if (env != null) {
                SOAPBody soapbody = env.getBody();
                if (soapbody != null) {
                    byte[] size = soapbody.toString().getBytes(Charset.defaultCharset());
                    requestSize = size.length;
                }

            }
        }
        requestSize = requestSize / 1000;
        log.info("Request Size : " + requestSize + " Kilobytes.");
        return requestSize;
    }

}