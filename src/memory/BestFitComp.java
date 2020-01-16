package memory;

import java.util.HashMap;
import java.util.LinkedList;

public class BestFitComp extends Memory{

    private LinkedList<int[]> freeList = new LinkedList<>();
    private HashMap<Integer, Integer> addressMap = new HashMap<>();
    /**
     * Initializes an instance of Memory.
     *
     * @param size The number of cells.
     */
    public BestFitComp(int size) {
        super(size);
        int[] first = new int[]{0, size};
        freeList.add(first);
    }

    @Override
    public Pointer alloc(int size) {
        Pointer p = null;
        // Om första insättningen ändra startvärde på det elementet.
        if (freeList.size() == 1){
            addressMap.put(freeList.getFirst()[0], freeList.getFirst()[0] + size);
            p = new Pointer(freeList.getFirst()[0], this);
            freeList.getFirst()[0] += size;
            return p;
        }else {
            // Om insättning med flera fria ytor, gå igenom fria och lägg till där det får plats.
            int bestAddress = Integer.MAX_VALUE, bestSize = Integer.MAX_VALUE, freeIndex = 0;
            for(int i = 0; i < freeList.size(); i++){
                // Om utrymmet är = size, välj det.

                if(freeList.get(i)[1] - freeList.get(i)[0] == size){
                    addressMap.put(freeList.get(i)[0], freeList.get(i)[0] + size);
                    p = new Pointer(freeList.get(i)[0], this);
                    freeList.remove(i);
                    return p;
                } else if((freeList.get(i)[1] - freeList.get(i)[0]) >= size && (freeList.get(i)[1] - freeList.get(i)[0]) < bestSize) {
                    bestSize = freeList.get(i)[1] - freeList.get(i)[0];
                    bestAddress = freeList.get(i)[0];
                    freeIndex = i;
                }
            }
            // Om inget returneras har man kommit till slutet och ska lägga på bästa plats.
            addressMap.put(bestAddress, bestAddress + size);
            p = new Pointer(bestAddress, this);
            freeList.get(freeIndex)[0] += size;
            return p;
        }

    }

    @Override
    public void release(Pointer p) {
        int address = p.pointsAt();
        int toAddress = addressMap.get(address);
        int size = toAddress - address;
        if(toAddress < freeList.getFirst()[0]){
            int[] a = new int[]{address, toAddress};
            freeList.addFirst(a);
        }else {
            for (int i = 1; i < freeList.size(); i++) {
                // Om det finns ett fritt block på högerkanten
                if (toAddress == freeList.get(i)[0]) {
                    // Om det även finns ett annat block på vänsterkanten

                    if (address == freeList.get(i - 1)[1]) {
                        // Mergea dem två fria blocken
                        freeList.get(i - 1)[1] = freeList.get(i)[1];
                        freeList.remove(i);
                    } else {
                        // Om finns höger, men inte väster, förläng åt vänster
                        freeList.get(i)[0] -= size;
                    }
                    break;
                    // Om finns block på vänsterkanten men ej på höger
                } else if (address == freeList.get(i - 1)[1]) {
                    // Förläng höger
                    freeList.get(i - 1)[1] += size;
                    break;
                    //Om adressen finns tilll vänster och ett fritt block finns till höger.
                } else if (toAddress == freeList.get(i-1)[0]){
                    freeList.get(i-1)[0] -= size;
                    break;
                    // Om adressen ligger mellan två block och det är för långt för att merga, lägg till ett nytt block emellan dessa.
                }else if (address > freeList.get(i - 1)[1] && toAddress < freeList.get(i)[0]) {
                    int[] a = new int[]{address, toAddress};
                    freeList.add(i, a);
                    break;
                }
            }
        }
        addressMap.remove(address);
    }

    @Override
    public void printLayout() {
        System.out.println("Free:");
        for (int[] elem : freeList){
            System.out.println("Address: " + elem[0] + ", Size: " + (elem[1] - elem[0]));
        }
    }
}
