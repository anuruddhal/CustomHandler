# CustomHandler
Custom API Handler for APIM 2.1.0

This handler will validate the post requests by calculating the payload size and drop the message if payload size is greater than 10kb.

## Please follow the instructions below to apply the custom handler.

1. Stop the server.
2. Copy the PayloadHandler-1.0.0-SNAPSHOT.jar PayloadHandler-1.0.0-SNAPSHOT.jar file to <APIM_HOME>/repository/components/lib folder. 
3. Engage the handlers to a single API or all the APIs as described in https://docs.wso2.com/display/AM1100/Writing+Custom+Handlers#WritingCustomHandlers-Engagingthecustomhandler.
Please note that you have to add this as a new handler.

<handler class="org.wso2.custom.handler.PayloadHandler"/>
4. Restart the sever.
