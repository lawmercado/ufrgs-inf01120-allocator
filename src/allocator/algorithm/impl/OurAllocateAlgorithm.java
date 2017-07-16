package allocator.algorithm.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import allocator.algorithm.AllocationAlgorithm;
import allocator.data.domain.Allocable;
import allocator.data.domain.Resource;

public class OurAllocateAlgorithm implements AllocationAlgorithm {

	public Allocable run(Allocable what, List<Allocable> where) {
		Allocable bestPossibility = null;
		int bestMatchingCoefficient = Integer.MAX_VALUE;
		int matchingCoefficient = 0;

		Map<Resource, Integer> reqResources = what.getResources();

		Iterator<Allocable> itrPossibilities = where.iterator();

		while (itrPossibilities.hasNext()) {
			Allocable possibility = itrPossibilities.next();
			Map<Resource, Integer> availResources = possibility.getResources();

			Map<Resource, Integer> matchingResources = this.getMatchingResources(reqResources, availResources);

			if (matchingResources.size() == reqResources.size()) {
				matchingCoefficient = this.getMatchingCoefficient(availResources, matchingResources);

				if (matchingCoefficient < bestMatchingCoefficient) {
					bestPossibility = possibility;
					bestMatchingCoefficient = matchingCoefficient;
				}
			}
		}

		return bestPossibility;
	}

	private Map<Resource, Integer> getMatchingResources(Map<Resource, Integer> reqResources, Map<Resource, Integer> availResources) {
		Iterator<Entry<Resource, Integer>> itrReqResources = reqResources.entrySet().iterator();

		Map<Resource, Integer> matchingResources = new HashMap<Resource, Integer>();

		while (itrReqResources.hasNext()) {
			Map.Entry<Resource, Integer> reqResource = itrReqResources.next();
			Resource reqResourceId = reqResource.getKey();
			
			Iterator<Entry<Resource, Integer>> itrAvailResource = availResources.entrySet().iterator();
			
			while (itrAvailResource.hasNext()) {
				Map.Entry<Resource, Integer> availResource = itrAvailResource.next();
				
				if (reqResource.getKey() == availResource.getKey()) {
					int quantityDiff = availResource.getValue() - reqResource.getValue();
					
					if (quantityDiff >= 0) {
						matchingResources.put(reqResourceId, Math.abs(quantityDiff));
					}
				}
			}
		}

		return matchingResources;
	}
	
	private int getMatchingCoefficient(Map<Resource, Integer> availResources, Map<Resource, Integer> matchingResources) {
		int matchingCoefficient = availResources.size() - matchingResources.size();

		Iterator<Entry<Resource, Integer>> itrMatchingResources = matchingResources.entrySet().iterator();

		while (itrMatchingResources.hasNext()) {
			Map.Entry<Resource, Integer> resource = itrMatchingResources.next();

			matchingCoefficient += resource.getValue();
		}

		return matchingCoefficient;
	}
}