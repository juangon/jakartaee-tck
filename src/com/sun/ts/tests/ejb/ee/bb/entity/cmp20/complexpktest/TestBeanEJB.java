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
 * @(#)TestBeanEJB.java	1.12 03/05/16
 */

package com.sun.ts.tests.ejb.ee.bb.entity.cmp20.complexpktest;

import com.sun.ts.lib.util.*;
import com.sun.ts.lib.porting.*;

import java.util.*;
import javax.naming.*;
import javax.ejb.*;
import java.rmi.*;
import java.sql.*;

public abstract class TestBeanEJB implements EntityBean {
  private EntityContext ectx = null;

  private TSNamingContext nctx = null;

  // JNDI Names for Local Home Interfaces

  private static final String LineItemLocal = "java:comp/env/ejb/LineItemLocal";

  private static final String LineItem = "java:comp/env/ejb/LineItem";

  // Entity instance data
  public abstract Integer getId();

  public abstract void setId(Integer i);

  public abstract String getBrandName();

  public abstract void setBrandName(String s);

  public abstract double getPrice();

  public abstract void setPrice(double p);

  public abstract Product getProduct();

  public abstract void setProduct(Product p);

  // CMR
  // 1XMany
  public abstract Collection getLineItems();

  public abstract void setLineItems(Collection v);

  public ComplexPK ejbCreate(int id, String brandName, double price,
      int quantity, String country) throws CreateException {
    TestUtil.logTrace("ejbCreate");
    Integer Id = new Integer(id);
    Product product = new Product(quantity, country);
    try {
      TestUtil.logMsg("Obtain naming context");
      nctx = new TSNamingContext();
      setId(Id);
      setBrandName(brandName);
      setPrice(price);
      setProduct(product);
    } catch (NamingException e) {
      TestUtil.printStackTrace(e);
      throw new CreateException("Unable to obtain naming context");
    } catch (Exception e) {
      TestUtil.printStackTrace(e);
      throw new CreateException("Exception occurred: " + e);
    }
    return null;
  }

  public void ejbPostCreate(int id, String brandName, double price,
      int quantity, String country) {
    TestUtil.logTrace("ejbPostCreate");
  }

  public void setEntityContext(EntityContext c) {
    TestUtil.logTrace("setEntityContext");
    ectx = c;
  }

  public void unsetEntityContext() {
    TestUtil.logTrace("unsetEntityContext");
  }

  public void ejbRemove() throws RemoveException {
    TestUtil.logTrace("ejbRemove");
  }

  public void ejbActivate() {
    TestUtil.logTrace("ejbActivate");
  }

  public void ejbPassivate() {
    TestUtil.logTrace("ejbPassivate");
  }

  public void ejbLoad() {
    TestUtil.logTrace("ejbLoad");
  }

  public void ejbStore() {
    TestUtil.logTrace("ejbStore");
  }

  // ===========================================================
  // TestBean interface (our business methods)

  public String ping(String s) {
    TestUtil.logTrace("ping : " + s);
    return "ping: " + s;
  }

  public void initLogging(Properties p) {
    TestUtil.logTrace("initLogging");
    try {
      TestUtil.init(p);
    } catch (RemoteLoggingInitException e) {
      TestUtil.printStackTrace(e);
      throw new EJBException(e.getMessage());
    }
  }

  public void addLineItem(LineItem l) {
    try {
      String liPK = (String) l.getPrimaryKey();
      TSNamingContext nctx = new TSNamingContext();
      LineItemLocalHome lineItemLocalHome = (LineItemLocalHome) nctx
          .lookup(LineItemLocal);
      LineItemLocal lLeb = lineItemLocalHome.findByPrimaryKey(liPK);
      Collection col = getLineItems();
      col.add(lLeb);
    } catch (Exception e) {
      TestUtil.printStackTrace(e);
      throw new EJBException("addLineItem:" + e);
    }
  }

}
