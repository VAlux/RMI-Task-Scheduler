package def.taskmodel;

import java.util.PriorityQueue;

/**
 * Just a simple wrapper around the ProrityQueue collection.
 * This allows all of the basic PriorityQueue functionality, but does not allow duplicate entries.
 * TODO: The 'offer' method complexity becomes O(n) in the worst case, so need to think about using something more efficient. 
 * @author ovoievodin
 * @param <T>
 */
public class PriorityQueueSet<T> extends PriorityQueue<T> {
	private static final long serialVersionUID = 2748954990709447377L;

	@Override
	public boolean offer(T e) {
		if(contains(e))	{
			return false;
		} else {
			return super.offer(e);
		}
	}
}
