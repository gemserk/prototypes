package com.gemserk.prototypes;

import java.util.ArrayList;

import com.gemserk.resources.ResourceManagerImpl;
import com.gemserk.resources.dataloaders.DataLoader;

public class CustomResourceManager<K> extends ResourceManagerImpl<K> {

	private ArrayList<K> registeredResources = new ArrayList<K>();

	public ArrayList<K> getRegisteredResources() {
		return registeredResources;
	}

	public void add(K id, DataLoader dataLoader) {
		super.add(id, dataLoader);
		registeredResources.add(id);
	}

}
