package br.ufmg.aserg.victorveloso.queue;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A queue implementation based on ring buffer and cursors. Hence, less "memory hungry" and less flexible.
 * @param <T> The type of queue's elements
 */
public class Queue<T> {
    private T[] elements;
    private int start;
    private int end;

    public Queue(final T[] elements) {
        this.elements = addTailNode(elements);
        start = 0;
        end = scanSize(elements);
    }

    /**
     * Static factory method for Queue of Integers initialization from built-in int[] type
     * @param array Queue elements contained in an array of int
     * @return Initialized Queue
     */
    public static Queue<Integer> fromIntArray(final int[] array) {
        return new Queue<>(IntStream.of(array).boxed().toArray(Integer[]::new));
    }

    /**
     * Get queue's current size
     * @return The number of elements in queue
     */
    public int size() {
        return Math.floorMod((end - start), elements.length);
    }

    /**
     * Get queue's max size
     * @return Queue's capacity
     */
    public int capacity() {
        return elements.length - 1;
    }

    /**
     * Checks for queue's emptiness
     * @return Whether queue is empty
     */
    public boolean isEmpty() {
        return start == end;
    }

    /**
     * Checks for queue's fullness
     * @return Whether queue is full
     */
    public boolean isFull() {
        return start == next(end);
    }

    /**
     * Check whether an object similar to "o" is contained in queue
     * @param o Expected object
     * @return Whether a similar object is found
     */
    public boolean contains(T o) {
        for (int i = start; i != end; i = next(i)) {
            if (o.equals(elements[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Push new element to the end of queue
     * @param o New element
     */
    public void enqueue(T o) {
        if (isFull()) {
                throw new ArrayIndexOutOfBoundsException("Queue is full");
        }
        elements[end] = o;
        end = next(end);
    }

    /**
     * Pops the first element from queue
     * @return First element
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Queue is empty");
        }
        T detached = elements[start];
        start = next(start);
        return detached;
    }

    /**
     * Sort valid elements (non-nulls elements inside start-end range)
     */
    public void sort() {
        T[] values = dump();
        Arrays.sort(values);
        for (T value : values) {
            enqueue(value);
        }
    }

    /**
     * Dequeue all valid elements preserving (FIFO) order
     * @return Valid elements
     */
    public T[] dump() {
        T[] values = Arrays.copyOf(elements, size());
        for (int i = 0; i < values.length; i++) {
            values[i] = dequeue();
        }
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Queue)) return false;

        Queue<T> queue = (Queue<T>) o;

        if(size() != queue.size()) return false;

        return equalsDeeply(queue);
    }

    /**
     * Compare valid elements to another queue' valid elements
     * @param queue Another queue to be compared with
     * @return Whether both queues' elements are equal
     */
    private boolean equalsDeeply(Queue<T> queue) {
        //Caution: dump has collateral effect
        T[] ourElements = dump();
        T[] theirElements = queue.dump();
        //Compare
        boolean isEqual = Arrays.equals(ourElements, theirElements);
        //Restore previous state
        for (int i = 0; i < ourElements.length; i++) {
            enqueue(ourElements[i]);
            queue.enqueue(theirElements[i]);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public String toString() {
        return "Queue{" + Arrays.toString(Arrays.copyOfRange(elements, start, end)) + '}';
    }

    /**
     * Handle cyclic nature of Ring Buffer
     * @param index An elements's cursor
     * @return The next position on elements (a Ring Buffer)
     */
    private int next(int index) {
        return (index + 1) % elements.length;
    }

    /**
     * Calculates the distance (i.e. the "size" of the array) from start to last valid element
     * @param elements Array of elements
     * @return Size of elements
     */
    private int scanSize(T[] elements) {
        int lastValidElement = 0;
        for (T element : elements) {
            if (element == null) {
                break;
            }
            lastValidElement = next(lastValidElement);
        }
        return lastValidElement;
    }

    /**
     * Adds a tail node allowing full state to be distinguishable from empty state
     * @param elements Array of elements
     * @return Array of elements concatenated to a null node
     */
    private T[] addTailNode(T[] elements) {
        return Arrays.copyOf(elements, elements.length + 1);
    }
}
