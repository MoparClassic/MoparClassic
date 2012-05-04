package org.moparscape.msc.gs.model.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.moparscape.msc.gs.util.exception.AlreadyBoundException;

public abstract class ChainManager<IdType, ChainType, ParamType> {

	protected final Map<IdType, ChainType> mapping = new ConcurrentHashMap<IdType, ChainType>();

	public final void bind(ChainType chain, List<IdType> ids)
			throws AlreadyBoundException {
		for (IdType id : ids) {
			bind(chain, id);
		}
	}

	public final void bind(ChainType chain, IdType id)
			throws AlreadyBoundException {
		ChainType prev = mapping.put(id, chain);
		if (prev != null) {
			mapping.put(id, prev);
			throw new AlreadyBoundException(id);
		}
	}

	public final void trigger(IdType id, ParamType param) {
		fire(mapping.get(id), param);
	}

	public abstract void fire(ChainType chainType, ParamType param);

	public abstract void init();

}