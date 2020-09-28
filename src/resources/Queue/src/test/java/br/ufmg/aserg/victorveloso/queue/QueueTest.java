package br.ufmg.aserg.victorveloso.queue;

import static org.assertj.core.api.Assertions.*;

import com.sun.tools.javac.util.List;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Test cases where Queue is initialized with an array of ints by the static factory method .fromIntArray
 */
class IntQueueTest {
    private Queue<Integer> sut;
    private int[] array;

    @BeforeEach
    void setUp() {
        array = new int[]{23, 14, 55, 3, 0, -5};
        sut = Queue.fromIntArray(array);
    }

    @Test
    void testCapacity() {
        assertThat(sut.capacity()).isEqualTo(array.length);
    }

    @Test
    void testSize() {
        assertThat(sut.size()).isEqualTo(array.length);
    }

    @Test
    void testEmptiness() {
        assertThat(sut.isEmpty()).isFalse();
    }

    @Test
    void testEmptinessAfterDump() {
        sut.dump();
        assertThat(sut.isEmpty()).isTrue();
    }

    @Test
    void testEnqueueOnFull() {
        assertThatExceptionOfType(ArrayIndexOutOfBoundsException.class).isThrownBy(() -> {
            sut.enqueue(8);
        });
    }

    @Test
    void testToString() {
        assertThat(sut.toString()).isEqualTo("Queue{[23, 14, 55, 3, 0, -5]}");
    }

    @Test
    void testHashCode() {
        assertThat(sut.hashCode()).isEqualTo(Arrays.hashCode(Arrays.copyOf(array, array.length + 1)));
    }

    @Test
    void testEqualitySameObject() {
        assertThat(sut.equals(sut)).isTrue();
    }

    @Test
    void testEqualityGeneralizedType() {
        Object sameObject = sut;

        assertThat(sut.equals(sameObject)).isTrue();
    }

    @Test
    void testEqualityOtherType() {
        assertThat(sut.equals(array)).isFalse();
    }

    @Test
    void testEqualityOtherContainedType() {
        Queue<Long> queue = new Queue<>(new Long[]{1L, 2L, 3L, 4L});

        assertThat(sut.equals(queue)).isFalse();
    }

    @Test
    void testEqualitySmallerQueue() {
        Queue<Integer> smallerQueue = new Queue<>(new Integer[]{23, 14, 55, 3, 0});

        assertThat(sut.equals(smallerQueue)).isFalse();
    }

    @Test
    void testEqualitySlightlyDifferent() {
        Queue<Integer> slightlyDifferent = new Queue<>(new Integer[]{23, 14, 55, 3, 0, 5});

        assertThat(sut.equals(slightlyDifferent)).isFalse();
    }

    @Test
    void testEqualQueues() {
        Queue<Integer> equalQueue = new Queue<>(new Integer[]{23, 14, 55, 3, 0, -5});

        assertThat(sut.equals(equalQueue)).isTrue();
    }

}