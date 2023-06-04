/*
 *  -------------------------------------------------------------------------
 *  Copyright 2014 
 *  Centre for Information Modeling - Austrian Centre for Digital Humanities
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *  -------------------------------------------------------------------------
 */

package org.emile.client.gui.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.emile.client.utils.Utils;

public class CSortTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private Vector<String> columnName;
    private HashMap<UUID,Vector>data;
    private ArrayList<UUID>keys;
    
    public CSortTableModel() {}

    public CSortTableModel(String[] columnName) {
    	this.data = new HashMap<UUID,Vector>();
    	this.keys = new ArrayList<UUID>();
    	this.columnName = new Vector<String>(Arrays.asList(columnName));
    }

    public CSortTableModel(Vector data, String[] columnName) {
    	this.data = new HashMap<UUID,Vector>();
    	this.keys = new ArrayList<UUID>();
     	this.columnName = new Vector<String>(Arrays.asList(columnName));
     	if (data == null || data.size() == 0) {
     		UUID key = UUID.randomUUID();
     		Vector entry = Utils.getVector(columnName.length);
   			entry.add(0, key);
			this.data.put(key, entry);
 			this.keys.add(key); 
     	} else {
     		for (int i = 0; i < data.size(); i++) { 
     			UUID key = UUID.randomUUID();
     			Vector entry =  (Vector) data.get(i);
     			entry.add(0, key);
     			this.data.put(key, entry);
     			this.keys.add(key);
     		}
     	}
    }

    @Override
    public int getRowCount() {
        return data.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnName.size();
    }
      
    @Override
    public String getColumnName(int index) {
        return (String) columnName.get(index);
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (data.isEmpty()) {
            return Object.class;
        }        
        return String.class;
    }
    
    public Object getValueAt(UUID rowUUID, int columnIndex) {
     	Vector entry = data.get(rowUUID);
    	return entry.get(columnIndex);
    }
    
    public void setValueAt(Object value, UUID rowUUID, int columnIndex) {
        Vector entry = data.get(rowUUID);
        entry.set(columnIndex, value); 
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
    	return false;
    }
    
    public void removeRow (UUID rowUUID) {
    	data.remove(rowUUID);
    	keys.remove(rowUUID);
    }
    
    public void addRow(String[] row) {
    	UUID key = UUID.randomUUID();
    	Vector entry = new Vector<String>(Arrays.asList(row));
    	entry.add(0, key);
    	data.put(key, entry);
    	keys.add(key);
     }

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < keys.size()) {
			Vector entry = data.get(keys.get(rowIndex));
			return entry.get(columnIndex);
		}
		return null;
 	}	
	

}
