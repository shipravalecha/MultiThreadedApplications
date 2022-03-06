/**
 * Based on idea from Matthew Flatt, Univ of Utah
 */
#include <iostream>
#include "ThreadGroup.h"
using namespace std;

/** some variable initializations:
 * MAX is the maximum length of an array
 * MAX_THREAD is the number of threads to be used in the program
 */
#define MAX 1000 * 1000
#define MAX_THREAD 2

int encode(int v) {
    // do something time-consuming (and arbitrary)
    for (int i = 0; i < 500; i++)
        v = ((v * v) + v) % 10;
    return v;
}

int decode(int v) {
    // do something time-consuming (and arbitrary)
    return encode(v);
}

/**
 * @class EncodeThread - the template argument to ThreadGroup just has
 * to have an implementation of the ()-operator. Here, this class will do the encoding of the array elements
 * using 2 threads.
 */
class EncodeThread {
public:
    void operator()(int id, void *sharedData) {
        int *data = (int*) sharedData;
        int start = id * (MAX / MAX_THREAD);
        int end = (id + 1) * (MAX / MAX_THREAD);
        for (int i = start; i < end; i++) {
            data[i] =  encode(data[i]);
        }
    }
};

/**
 * @class DecodeThread - the template argument to ThreadGroup just has
 * to have an implementation of the ()-operator. Here, this class will do the decoding of the encoded elements
 * of an array using 2 threads.
 */
class DecodeThread {
public:
    void operator()(int id, void* sharedData) {
        int *data = (int*) sharedData;
        int start = id * (MAX / MAX_THREAD);
        int end = (id + 1) * (MAX / MAX_THREAD);
        for (int i = start; i < end; i++) {
            data[i] = decode(data[i]);
        }
    }
};

void prefixSums(int *data, int length) {
    
    /**
     * Create a thread group using ThreadGroup class. The 2 threads created in the group with createThread will run EncodeThread's operator.
     * The second parameter is the unmodified data array.
     * Then waitForAll will act as a barrier and wait for all the threads in the group to finish.
     */
    ThreadGroup<EncodeThread> encoders;
        encoders.createThread(0, data);
        encoders.createThread(1, data);
        encoders.waitForAll();
    
    /// encoded_sum_array contains the sum of the encoded elements at contiguous locations from the data array.
    int *encoded_sum_array = new int[length];
    encoded_sum_array[0] = data[0];
    for (int i = 1; i < length; i++) {
        encoded_sum_array[i] = encoded_sum_array[i - 1] + data[i];
    }
    
    /**
     * 2 threads are created for DecodeThread's operator similar to above EncodeThread's operator.
     * The second parameter is the encoded_sum_array created above. It's elements will be decoded further using decode function.
     */
    ThreadGroup<DecodeThread> decoders;
        decoders.createThread(0, encoded_sum_array);
        decoders.createThread(1, encoded_sum_array);
        decoders.waitForAll();

    /// Transfer the decoded elements to main data array.
    for (int i = 0; i < length; i++) {
        data[i] = encoded_sum_array[i];
    }
    
    /// delete encoded_sum_array at the end of the program to save some memory.
    delete[] encoded_sum_array;
}

int main() {
    int length = 1000 * 1000;

    // make array
    int *data = new int[length];
    for (int i = 1; i < length; i++)
        data[i] = 1;
    data[0] = 6;

    // transform array into converted/deconverted prefix sum of original
    prefixSums(data, length);

    // printed out result is 6, 6, and 2 when data[0] is 6 to start and the rest 1
    cout << "[0]: " << data[0] << endl
            << "[" << length/2 << "]: " << data[length/2] << endl
            << "[end]: " << data[length-1] << endl;

    delete[] data;
    return 0;
}
