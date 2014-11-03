/**
 * Modules which process input requests from external systems
 * - public interfaces are divided by entity type + there is special interface for communication with Vodafone (VF),
 * to be able to send messages to our system.
 * <p/>
 * Input modules publicize interfaces (mostly WSDL interface).
 * "In" modules are "servers" of the communication.
 */
package org.cleverbus.modules.in;