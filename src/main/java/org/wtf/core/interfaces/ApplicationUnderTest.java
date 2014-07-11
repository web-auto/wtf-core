/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.interfaces;

import java.util.HashMap;

public interface ApplicationUnderTest {
	public HashMap<String, String[]> get();
	
	public String[] resolveGroupNames(String[] args);
	
	public String getMethodSelector(String autName);
}
