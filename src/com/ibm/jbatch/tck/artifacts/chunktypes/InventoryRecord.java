/**
 * Copyright 2012 International Business Machines Corp.
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.jbatch.tck.artifacts.chunktypes;

import com.sun.ts.lib.harness.*;
import com.sun.ts.lib.util.TestUtil;

public class InventoryRecord {

	private int itemID;
	private int quantity;
	
	public InventoryRecord(int itemID, int quantity) {
		this.itemID = itemID;
		this.quantity = quantity;
	}

	public int getItemID() {
		return itemID;
	}

	public int getQuantity() {
		return quantity;
	}
	
	
}
