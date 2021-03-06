/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * $Id$
 */

package com.sun.ts.tests.jaxws.wsi.j2w.rpc.literal.R2710;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.javatest.Status;
import com.sun.ts.tests.jaxws.sharedclients.ClientFactory;
import com.sun.ts.tests.jaxws.sharedclients.SOAPClient;
import com.sun.ts.tests.jaxws.sharedclients.rpclitclient.*;
import com.sun.ts.tests.jaxws.wsi.constants.DescriptionConstants;
import com.sun.ts.tests.jaxws.wsi.constants.SOAPConstants;
import com.sun.ts.tests.jaxws.wsi.utils.DescriptionUtils;
import com.sun.ts.lib.harness.*;

public class Client extends ServiceEETest
    implements DescriptionConstants, SOAPConstants {
  /**
   * The client.
   */
  private SOAPClient client;

  static J2WRLShared service = null;

  /**
   * The document.
   */
  private Document document;

  /**
   * The signatures.
   */
  private ArrayList signatures;

  /**
   * Test entry point.
   * 
   * @param args
   *          the command-line arguments.
   */
  public static void main(String[] args) {
    Client test = new Client();
    Status status = test.run(args, System.out, System.err);
    status.exit();
  }

  /**
   * @class.testArgs: -ap jaxws-url-props.dat
   * @class.setup_props: webServerHost; webServerPort; platform.mode;
   *
   * @param args
   * @param properties
   *
   * @throws Fault
   */
  public void setup(String[] args, Properties properties) throws Fault {
    client = ClientFactory.getClient(J2WRLSharedClient.class, properties, this,
        service);
    logMsg("setup ok");
  }

  public void cleanup() {
    logMsg("cleanup");
  }

  /**
   * @testName: testWireSignatures
   *
   * @assertion_ids: WSI:SPEC:R2710
   *
   * @test_Strategy: Retrieve the WSDL, generated by the Java-to-WSDL tool, and
   *                 examine the wsdl:binding elements to ensure that their
   *                 input- and output element's soap:body elements reference
   *                 unique global part elements.
   *
   * @throws Fault
   */
  public void testWireSignatures() throws Fault {
    document = client.getDocument();
    signatures = new ArrayList();
    Element[] bindings = DescriptionUtils.getBindings(document);
    for (int i = 0; i < bindings.length; i++) {
      verifyBinding(bindings[i]);
    }
  }

  protected void verifyBinding(Element binding) throws Fault {
    Element[] operations = DescriptionUtils.getChildElements(binding,
        WSDL_NAMESPACE_URI, WSDL_OPERATION_LOCAL_NAME);
    for (int i = 0; i < operations.length; i++) {
      verifyOperation(binding, operations[i]);
    }
  }

  protected void verifyOperation(Element binding, Element operation)
      throws Fault {
    Element[] children = DescriptionUtils.getChildElements(operation,
        WSDL_NAMESPACE_URI, null);
    for (int i = 0; i < children.length; i++) {
      String localName = children[i].getLocalName();
      if ((localName.equals(WSDL_INPUT_LOCAL_NAME))) {
        verifyInput(binding, operation, children[i]);
      }
    }
  }

  protected void verifyInput(Element binding, Element operation, Element io)
      throws Fault {
    Element soapBody = DescriptionUtils.getChildElement(io, SOAP_NAMESPACE_URI,
        SOAP_BODY_LOCAL_NAME);
    if (soapBody == null) {
      return;
    }
    String partName = "";
    String parts = soapBody.getAttribute(SOAP_PARTS_ATTR);
    if (parts.length() > 0) {
      StringTokenizer tokenizer = new StringTokenizer(parts, " ");
      partName = tokenizer.nextToken();
    }
    Element message = getMessage(binding, operation, io);
    verifyMessage(binding, operation, io, message, partName);
  }

  protected void verifyMessage(Element binding, Element operation, Element io,
      Element message, String partName) throws Fault {
    Element part = null;
    if (partName.length() > 0) {
      part = DescriptionUtils.getNamedChildElement(message, WSDL_NAMESPACE_URI,
          WSDL_PART_LOCAL_NAME, partName);
      if (part == null) {
        throw new Fault("wsdl:message named '"
            + message.getAttribute(WSDL_NAME_ATTR)
            + "' does not contain part named '" + partName + "' (BP-R2710)");
      }
    } else {
      Element[] parts = DescriptionUtils.getChildElements(message,
          WSDL_NAMESPACE_URI, WSDL_PART_LOCAL_NAME);
      if (parts.length > 0) {
        part = parts[0];
      }
    }
    String signature;
    if (part == null) {
      signature = "";
    } else {
      signature = part.getAttribute(WSDL_ELEMENT_ATTR);
    }
    if (signature.length() == 0) {
      return;
    }
    if (signatures.contains(signature)) {
      throw new Fault(
          "The wire signature '" + signature + "' is not unique (BP-R2710)");
    } else {
      signatures.add(signature);
    }
  }

  protected Element getMessage(Element binding, Element operation, Element io)
      throws Fault {
    String name = binding.getAttribute(WSDL_TYPE_ATTR);
    int index;
    index = name.indexOf(':');
    if (index > 0) {
      name = name.substring(index + 1);
    }
    Element portType = DescriptionUtils.getPortType(document, name);
    if (portType == null) {
      throw new Fault("Required wsdl:portType element named '" + name
          + "' not found (BP-R2210)");
    }
    name = operation.getAttribute(WSDL_NAME_ATTR);
    operation = DescriptionUtils.getNamedChildElement(portType,
        WSDL_NAMESPACE_URI, WSDL_OPERATION_LOCAL_NAME, name);
    if (name == null) {
      throw new Fault(
          "Required wsdl:operation element is named 'null' (BP-R2210)");
    }
    name = io.getAttribute(WSDL_NAME_ATTR);
    String localName = io.getLocalName();
    io = DescriptionUtils.getNamedChildElement(operation, WSDL_NAMESPACE_URI,
        localName, name);
    if (io == null) {
      throw new Fault("Required wsdl:" + localName + " element named '" + name
          + "' not found (BP-R2210)");
    }
    name = io.getAttribute(WSDL_MESSAGE_ATTR);
    index = name.indexOf(':');
    if (index > 0) {
      name = name.substring(index + 1);
    }
    Element message = DescriptionUtils.getMessage(document, name);
    if (message == null) {
      throw new Fault("Required wsdl:message element named '" + name
          + "' not found (BP-R2210)");
    }
    return message;
  }
}
