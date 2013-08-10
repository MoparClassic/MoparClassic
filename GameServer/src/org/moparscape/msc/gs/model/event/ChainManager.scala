package org.moparscape.msc.gs.model.event
import java.util.concurrent.CopyOnWriteArrayList
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.concurrent.Lock

/**
 * Manages a chain.
 *
 * @author Joe Pritzel
 */
abstract class ChainManager[IdType, ChainType, ParamType](defaultChain : ChainType) {

	/**
	 * Should always be read/modified through the lock method.
	 */
	private var _mapping = ListBuffer[(IdType, ChainType)]()

	/**
	 * The lock for _mapping.
	 */
	private val _lock = new Lock

	/**
	 * An immutable copy of the mapping.
	 */
	def mapping = lock { _mapping.toList }

	/**
	 * Binds list of ids to a chain.
	 */
	def bind(chain : ChainType, ids : List[IdType]) {
		lock {
			ids.foreach(id => _mapping += id -> chain)
		}
	}

	/**
	 * Fires all chains with the given id.
	 */
	def trigger(id : IdType, param : ParamType) {
		lock {
			var chain = _mapping.filter(_._1 == id)
			if (chain.size == 0) fire(defaultChain, param)
			else chain.foreach(c => fire(c._2, param))
		}
	}

	/**
	 * Fires a chain with the given param.
	 */
	def fire(chain : ChainType, param : ParamType)

	/**
	 * Executes the given function while _mapping is locked.
	 */
	private def lock[T](f : => T) = try { _lock.acquire; f } finally { _lock.release }
}