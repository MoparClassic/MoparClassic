package org.moparscape.msc.gs.event.handler.objectaction;

import org.moparscape.msc.gs.model.event.Chain;

public class ObjectActionChain extends Chain<ObjectEvent, ObjectActionParam> {

	public ObjectActionChain(ObjectEvent... events) {
		for (ObjectEvent e : events) {
			this.addLast(e);
		}
	}

	@Override
	public Object fire(ObjectEvent next, ObjectActionParam param) {
		Boolean ret = next.fire(param);
		return ret == null || ret == false ? null : ret;
	}

}
