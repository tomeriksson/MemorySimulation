package memory;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This memory model allocates memory cells based on the first-fit method.
 * 
 * @author "Johan Holmberg, Malmö university"
 * @since 1.0
 */
public class FirstFit extends Memory {

	private LinkedList<int[]> flags = new LinkedList<int[]>();
	/**
	 * Initializes an instance of a first fit-based memory.
	 * 
	 * @param size The number of cells.
	 */
	public FirstFit(int size) {
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
		Pointer p = null;
		if (flags.isEmpty()) {
			p = new Pointer(startFlag, this);
			flags.add(new int[] { startFlag, size-1 });
		} else if (flags.size() == 1) {

			if (flags.get(0)[0] >= size) {
				
				p = new Pointer(startFlag, this);
				flags.addFirst(new int[] { startFlag, size-1 });
				

			} else {
				startFlag = flags.get(0)[1]+1;
				stopFlag = startFlag + size-1;
				p = new Pointer(startFlag, this);
				flags.add(new int[] { startFlag, stopFlag });

			}
		} else { 			
			if (flags.get(0)[0] >= size) {
				p = new Pointer(startFlag, this);
				flags.addFirst(new int[] { startFlag, size-1 });

			} else {
				// kolla om det går in mellan några
				boolean looking = true;
				int index = 1;
				do {
					if (flags.get(index)[0] - flags.get(index - 1)[1] >= size) {
//						System.out.println(flags.get(index)[0] +" - "+ flags.get(index - 1)[1] +">="+ size);

						// lägg in den och sätt looking till false
						startFlag = flags.get(index - 1)[1]+1;
						stopFlag = startFlag + size-1;
						p = new Pointer(startFlag, this);
						flags.add(index, new int[] { startFlag, stopFlag });
						looking = false;
					}else {
						index++;
					}

				} while (looking && index < flags.size());

				if (looking) {
					startFlag = flags.get(flags.size() - 1)[1]+1;
					stopFlag = startFlag + size-1;
					p = new Pointer(startFlag, this);
					flags.add(new int[] { startFlag, stopFlag });

				}
			}

		}
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

	/**
	 * Compacts the memory space.
	 */
	public void compact() {
		// TODO Implement this!
	}
}
