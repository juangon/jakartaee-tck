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

package com.sun.ts.tests.jaxws.wsi.w2j.rpc.literal.R1027;

import com.sun.ts.tests.jaxws.sharedclients.SOAPClient;
import com.sun.ts.lib.harness.EETest;

public class W2JRLR1027Client extends SOAPClient {
  public W2JRLR1027Client(String webServerHost, int port, int mode)
      throws EETest.Fault {
    this(webServerHost, port, mode, null);
  }

  public W2JRLR1027Client(String webServerHost, int port, int mode,
      javax.xml.ws.Service webServiceRef) throws EETest.Fault {
    super(webServerHost, port, mode);
    stubContext.setNamespace("http://simpletestservice.org/wsdl");
    stubContext.setService("SimpleTest");
    stubContext.setPort("SimpleEndpointPort");
    stubContext.setEndpointInterface(SimpleEndpoint.class);
    stubContext.setWebServiceRef(webServiceRef);
  }

  protected String getEndpointURLProperty() {
    return "wsi.w2jrlr1027.endpoint.1";
  }

  protected String getWSDLURLProperty() {
    return "wsi.w2jrlr1027.wsdlloc.1";
  }

  public String helloWorld() throws Exception {
    return ((SimpleEndpoint) stubContext.getStub()).helloWorld();
  }
}
