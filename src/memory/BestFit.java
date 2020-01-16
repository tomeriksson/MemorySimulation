package memory;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This memory model allocates memory cells based on the best-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class BestFit extends Memory {

	private LinkedList<int[]> flags = new LinkedList<int[]>();

	/**
	 * Initializes an instance of a best fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public BestFit(int size) {
		super(size);
		// TODO Implement this!
	}

	/**
	 * Allocates a number of memory cells.
	 * 
	 * @param size the number of cells to allocate.
	 * @return The address of the first cell.
	 */
	@Override
	public Pointer alloc(int size) {
		int startFlag = 0;
		int stopFlag = 0;
		int smallestGap = Integer.MAX_VALUE;

		int after = 0;
		int before = 0;

		if (flags.isEmpty()) {
			stopFlag = startFlag + size - 1;
			flags.addFirst(new int[] { startFlag, stopFlag });

		} else if (flags.size() == 1) {
			if (flags.get(0)[0] - size < 0) {
				startFlag = flags.get(0)[1] + 1;
				stopFlag = startFlag + size - 1;
				flags.add(new int[] { startFlag, stopFlag });

			} else if (flags.get(0)[0] - size >= 0) {
				stopFlag = startFlag + size - 1;
				flags.addFirst(new int[] { startFlag, stopFlag });
			}
		} else if (flags.size() > 1) {
			int indexSmallest = 0;
			
			if (flags.get(0)[0] - size == 0) {
				stopFlag = startFlag + size - 1;
				flags.addFirst(new int[] { startFlag, stopFlag });
			} else if (flags.get(0)[0] - size < 0) {
				boolean notFound = true;
				for (int i = 1; i < flags.size(); i++) {
					after = flags.get(i)[0];
					before = flags.get(i - 1)[1];

					if (after - before >= size) {
						if (after - before < smallestGap) {
							smallestGap = after - before;
							startFlag = before + 1;
							indexSmallest = i;
							notFound = false;
						}

					}
				}
				if(notFound) {
					startFlag = flags.getLast()[1] + 1;
					stopFlag = startFlag + size - 1;
					flags.addLast(new int[] { startFlag, stopFlag });
				}else {
					stopFlag = startFlag + size - 1;
					flags.add(indexSmallest, new int[] { startFlag, stopFlag });
				}
				
			} else {
				
				smallestGap = flags.get(0)[0] - size;
				for (int i = 1; i < flags.size(); i++) {
					after = flags.get(i)[0];
					before = flags.get(i - 1)[1];

					if (after - before >= size) {
						if (after - before < smallestGap) {
							smallestGap = after - before;
							startFlag = before + 1;
							indexSmallest = i;
						}

					}
				}
				stopFlag = startFlag + size - 1;
				flags.add(indexSmallest, new int[] { startFlag, stopFlag });

			}

		}

		Pointer p = new Pointer(startFlag, this);
		return p;

	}

	/**
	 * Releases a number of data cells
	 * 
	 * @param p The pointer to release.
	 */
	@Override
	public void release(Pointer p) {
		Pointer pnt = p;
		int address = pnt.pointsAt();

		for (int i = 0; i < flags.size(); i++) {
			if (flags.get(i)[0] == address) {
				flags.remove(i);
			}
		}
	}

	/**
	 * Prints a simple model of the memory. Example:
	 * 
	 * | 0 - 110 | Allocated | 111 - 150 | Free | 151 - 999 | Allocated | 1000 -
	 * 1024 | Free
	 */
	@Override
	public void printLayout() {
		System.out.println("Allocated: ");
		for (int i = 0; i < flags.size(); i++) {
			System.out.println(flags.get(i)[0] + " - " + flags.get(i)[1]);
		}
	}
}
